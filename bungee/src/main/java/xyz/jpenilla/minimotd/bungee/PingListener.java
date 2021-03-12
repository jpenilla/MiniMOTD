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
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MOTDIconPair;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.util.Optional;

public class PingListener implements Listener {
  private final MiniMOTD<Favicon> miniMOTD;

  public PingListener(final @NonNull MiniMOTD<Favicon> miniMOTD) {
    this.miniMOTD = miniMOTD;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPing(final @NonNull ProxyPingEvent e) {
    final ServerPing response = e.getResponse();

    if (response != null) {
      final String configString = this.miniMOTD.configManager().pluginSettings().configStringForHost(
        Optional.ofNullable(e.getConnection().getVirtualHost())
          .map(inetSocketAddress -> String.format("%s:%s", inetSocketAddress.getHostName(), inetSocketAddress.getPort()))
          .orElse("default")
      ).orElse("default");
      final MiniMOTDConfig cfg = this.miniMOTD.configManager().resolveConfig(configString);
      final ServerPing.Players players = response.getPlayers();
      final int onlinePlayers = this.miniMOTD.calculateOnlinePlayers(cfg, players.getOnline());
      players.setOnline(onlinePlayers);

      final int maxPlayers = cfg.adjustedMaxPlayers(onlinePlayers, players.getMax());
      players.setMax(maxPlayers);

      if (cfg.disablePlayerListHover()) {
        players.setSample(new ServerPing.PlayerInfo[]{});
      }

      final MOTDIconPair<Favicon> pair = this.miniMOTD.createMOTD(cfg, onlinePlayers, maxPlayers);

      Component motdComponent = pair.motd();
      if (motdComponent != null) {
        if (e.getConnection().getVersion() < Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
          motdComponent = GsonComponentSerializer.colorDownsamplingGson().deserialize(GsonComponentSerializer.colorDownsamplingGson().serialize(motdComponent));
        }
        response.setDescriptionComponent(new TextComponent(BungeeComponentSerializer.get().serialize(motdComponent)));
      }

      final Favicon favicon = pair.icon();
      if (favicon != null) {
        response.setFavicon(favicon);
      }

      response.setPlayers(players);
      e.setResponse(response);
    }
  }
}
