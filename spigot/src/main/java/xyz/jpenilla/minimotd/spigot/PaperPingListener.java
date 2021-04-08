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

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.MOTDIconPair;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig.PlayerCount;

public class PaperPingListener implements Listener {
  private final MiniMOTDPlugin plugin;
  private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
  private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();
  private final MiniMOTD<CachedServerIcon> miniMOTD;

  public PaperPingListener(final @NonNull MiniMOTDPlugin plugin, final @NonNull MiniMOTD<CachedServerIcon> miniMOTD) {
    this.miniMOTD = miniMOTD;
    this.plugin = plugin;
  }

  @EventHandler
  public void onPing(final @NonNull PaperServerListPingEvent e) {
    final MiniMOTDConfig cfg = this.miniMOTD.configManager().mainConfig();

    final PlayerCount count = cfg.modifyPlayerCount(e.getNumPlayers(), e.getMaxPlayers());
    final int onlinePlayers = count.onlinePlayers();
    final int maxPlayers = count.maxPlayers();

    e.setNumPlayers(onlinePlayers);
    e.setMaxPlayers(maxPlayers);

    final MOTDIconPair<CachedServerIcon> pair = this.miniMOTD.createMOTD(cfg, onlinePlayers, maxPlayers);

    final Component motdComponent = pair.motd();
    if (motdComponent != null) {
      if (e.getClient().getProtocolVersion() < 735 || this.plugin.majorMinecraftVersion() < 16) {
        e.setMotd(this.legacySerializer.serialize(motdComponent));
      } else {
        e.setMotd(this.serializer.serialize(motdComponent));
      }
    }

    final CachedServerIcon favicon = pair.icon();
    if (favicon != null) {
      e.setServerIcon(favicon);
    }

    if (cfg.disablePlayerListHover()) {
      e.getPlayerSample().clear();
    }
    if (cfg.hidePlayerCount()) {
      e.setHidePlayers(true);
    }
  }
}
