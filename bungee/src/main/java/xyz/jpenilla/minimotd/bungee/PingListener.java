/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2021 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.Pair;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.util.concurrent.ThreadLocalRandom;

public class PingListener implements Listener {
  private final MiniMessage miniMessage = MiniMessage.get();
  private final MiniMOTD miniMOTD;
  private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

  public PingListener(final @NonNull MiniMOTD miniMOTD) {
    this.miniMOTD = miniMOTD;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPing(final @NonNull ProxyPingEvent e) {
    final ServerPing response = e.getResponse();

    if (response != null) {
      final MiniMOTDConfig cfg = this.miniMOTD.configManager().config();

      final ServerPing.Players players = response.getPlayers();
      int onlinePlayers = players.getOnline();
      if (cfg.fakePlayersEnabled()) {
        try {
          if (cfg.fakePlayers().contains(":")) {
            final String[] fakePlayers = cfg.fakePlayers().split(":");
            final int start = Integer.parseInt(fakePlayers[0]);
            final int end = Integer.parseInt(fakePlayers[1]);
            onlinePlayers = onlinePlayers + ThreadLocalRandom.current().nextInt(start, end);
          } else if (cfg.fakePlayers().contains("%")) {
            final double factor = 1 + (Double.parseDouble(cfg.fakePlayers().replace("%", "")) / 100);
            onlinePlayers = (int) Math.ceil(factor * onlinePlayers);
          } else {
            final int addedPlayers = Integer.parseInt(cfg.fakePlayers());
            onlinePlayers = onlinePlayers + addedPlayers;
          }
        } catch (final NumberFormatException ex) {
          this.miniMOTD.logger().warn("fakePlayers config invalid");
        }
      }
      players.setOnline(onlinePlayers);

      final int maxPlayers = cfg.adjustedMaxPlayers(onlinePlayers, players.getMax());
      players.setMax(maxPlayers);

      if (cfg.disablePlayerListHover()) {
        players.setSample(new ServerPing.PlayerInfo[]{});
      }

      final Pair<Favicon, String> pair = this.miniMOTD.createMOTD(onlinePlayers, maxPlayers);

      final String motdString = pair.right();
      if (motdString != null) {
        Component motdComponent = this.miniMessage.parse(motdString);
        if (e.getConnection().getVersion() < 735) {
          motdComponent = this.legacySerializer.deserialize(this.legacySerializer.serialize(motdComponent));
        }
        response.setDescriptionComponent(BungeeComponentSerializer.get().serialize(motdComponent)[0]);
      }

      final Favicon favicon = pair.left();
      if (favicon != null) {
        response.setFavicon(favicon);
      }

      response.setPlayers(players);
      e.setResponse(response);
    }
  }
}
