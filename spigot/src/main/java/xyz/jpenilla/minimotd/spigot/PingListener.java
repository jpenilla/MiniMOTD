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
package xyz.jpenilla.minimotd.spigot;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.Pair;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

public class PingListener implements Listener {
  private final MiniMOTD miniMOTD;
  private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
  private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();
  private final MiniMessage miniMessage = MiniMessage.get();
  private final MiniMOTDPlugin plugin;

  public PingListener(final @NonNull MiniMOTDPlugin plugin, final @NonNull MiniMOTD miniMOTD) {
    this.plugin = plugin;
    this.miniMOTD = miniMOTD;
  }

  @EventHandler
  public void onPing(final @NonNull ServerListPingEvent e) {
    final MiniMOTDConfig cfg = this.miniMOTD.configManager().mainConfig();
    final int onlinePlayers = e.getNumPlayers();
    final int actualMaxPlayers = e.getMaxPlayers();

    final int maxPlayers = cfg.adjustedMaxPlayers(onlinePlayers, actualMaxPlayers);
    e.setMaxPlayers(maxPlayers);

    final Pair<CachedServerIcon, String> pair = this.miniMOTD.createMOTD(cfg, onlinePlayers, maxPlayers);
    final String motdString = pair.right();
    if (motdString != null) {
      if (this.plugin.getMajorMinecraftVersion() > 15) {
        e.setMotd(this.serializer.serialize(this.miniMessage.parse(motdString)));
      } else {
        e.setMotd(this.legacySerializer.serialize(this.miniMessage.parse(motdString)));
      }
    }

    final CachedServerIcon favicon = pair.left();
    if (favicon != null) {
      e.setServerIcon(favicon);
    }

  }
}
