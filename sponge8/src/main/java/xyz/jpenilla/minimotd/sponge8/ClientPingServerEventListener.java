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
package xyz.jpenilla.minimotd.sponge8;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import xyz.jpenilla.minimotd.common.ComponentColorDownsampler;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MOTDIconPair;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig.PlayerCount;

import java.lang.reflect.Method;

final class ClientPingServerEventListener implements EventListener<ClientPingServerEvent> {
  private final MiniMOTD<Favicon> miniMOTD;
  private @MonotonicNonNull Method SpongeMinecraftVersion_getProtocol;

  ClientPingServerEventListener(final @NonNull MiniMOTD<Favicon> miniMOTD) {
    this.miniMOTD = miniMOTD;
  }

  @Override
  public void handle(final @NonNull ClientPingServerEvent event) {
    final ClientPingServerEvent.Response response = event.response();

    final ClientPingServerEvent.Response.Players players;
    final ClientPingServerEvent.Response.Players players0 = response.players().orElse(null);
    if (players0 != null) {
      players = players0;
    } else {
      response.setHidePlayers(false);
      players = response.players().orElse(null);
      if (players == null) {
        this.miniMOTD.logger().warn(String.format("Failed to handle ClientPingServerEvent: '%s', response.players() was null.", event));
        return;
      }
    }

    final MiniMOTDConfig config = this.miniMOTD.configManager().mainConfig();

    final PlayerCount count = config.modifyPlayerCount(players.online(), players.max());
    final int onlinePlayers = count.onlinePlayers();
    final int maxPlayers = count.maxPlayers();
    players.setOnline(onlinePlayers);
    players.setMax(maxPlayers);

    final MOTDIconPair<Favicon> pair = this.miniMOTD.createMOTD(config, onlinePlayers, maxPlayers);

    final Component motdComponent = pair.motd();
    if (motdComponent != null) {
      final MinecraftVersion version = event.client().version();
      if (this.legacy(version)) {
        response.setDescription(ComponentColorDownsampler.downsampler().downsample(motdComponent));
      } else {
        response.setDescription(motdComponent);
      }
    }

    final Favicon favicon = pair.icon();
    if (favicon != null) {
      response.setFavicon(favicon);
    }

    if (config.disablePlayerListHover()) {
      players.profiles().clear();
    }
    if (config.hidePlayerCount()) {
      response.setHidePlayers(true);
    }
  }

  private boolean legacy(final @NonNull MinecraftVersion version) {
    try {
      if (!version.isLegacy() && this.SpongeMinecraftVersion_getProtocol == null) {
        this.SpongeMinecraftVersion_getProtocol = version.getClass().getMethod("getProtocol");
      }
      return version.isLegacy()
        || (int) this.SpongeMinecraftVersion_getProtocol.invoke(version) < Constants.MINECRAFT_1_16_PROTOCOL_VERSION;
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to get protocol version", e);
    }
  }
}
