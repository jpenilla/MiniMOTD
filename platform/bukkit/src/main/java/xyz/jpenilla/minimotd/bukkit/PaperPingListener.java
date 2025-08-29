/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2025 Jason Penilla
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
package xyz.jpenilla.minimotd.bukkit;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.lib.PaperLib;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MOTDConfig;

public final class PaperPingListener implements Listener {
  private static final Method WORK_AROUND_PAPER_BUG;

  static {
    Method paperWorkaround;
    try {
      paperWorkaround = PaperServerListPingEvent.class.getMethod("getListedPlayers");
    } catch (final NoSuchMethodException e) {
      paperWorkaround = null;
    }
    WORK_AROUND_PAPER_BUG = paperWorkaround;
  }

  private final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
  private final MiniMOTD<CachedServerIcon> miniMOTD;

  PaperPingListener(final @NonNull MiniMOTD<CachedServerIcon> miniMOTD) {
    this.miniMOTD = miniMOTD;
  }

  @EventHandler
  public void handlePing(final @NonNull PaperServerListPingEvent event) {
    final MOTDConfig cfg = this.miniMOTD.configManager().mainConfig();

    final PingResponse<CachedServerIcon> response = this.miniMOTD.createMOTD(cfg, event.getNumPlayers(), event.getMaxPlayers());

    response.playerCount().applyCount(event::setNumPlayers, event::setMaxPlayers);
    response.motd(motd -> {
      if (event.getClient().getProtocolVersion() < Constants.MINECRAFT_1_16_PROTOCOL_VERSION || PaperLib.getMinecraftVersion() < 16) {
        event.setMotd(LegacyComponentSerializer.legacySection().serialize(motd));
      } else {
        event.setMotd(this.unusualHexSerializer.serialize(motd));
      }
    });
    response.icon(event::setServerIcon);

    if (response.disablePlayerListHover()) {
      if (WORK_AROUND_PAPER_BUG != null) {
        // Newer Paper replaces getPlayerSample with getListedPlayers,
        // the old method is supposed to still work, but it just silently does nothing on 1.20.6 (fixed in 1.21.1)
        try {
          ((List) WORK_AROUND_PAPER_BUG.invoke(event)).clear();
        } catch (final IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException("Failed to invoke " + WORK_AROUND_PAPER_BUG.getName(), e);
        }
      } else {
        event.getPlayerSample().clear();
      }
    }
    if (response.hidePlayerCount()) {
      event.setHidePlayers(true);
    }
  }
}
