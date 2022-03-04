/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Jason Penilla
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

import com.google.inject.Inject;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;
import xyz.jpenilla.minimotd.common.util.ComponentColorDownsampler;

final class ClientPingServerEventListener implements EventListener<ClientPingServerEvent> {
  private static final Method GET_PROTOCOL;

  static {
    try {
      final Class<?> protocolMinecraftVersion = Class.forName("org.spongepowered.common.ProtocolMinecraftVersion");
      GET_PROTOCOL = protocolMinecraftVersion.getMethod("getProtocol");
    } catch (final ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }

  private final MiniMOTD<Favicon> miniMOTD;

  @Inject
  private ClientPingServerEventListener(final @NonNull MiniMOTD<Favicon> miniMOTD) {
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

    final PingResponse<Favicon> mini = this.miniMOTD.createMOTD(config, players.online(), players.max());
    mini.playerCount().applyCount(players::setOnline, players::setMax);
    mini.motd(motd -> {
      if (this.legacy(event.client().version())) {
        response.setDescription(ComponentColorDownsampler.downsampler().downsample(motd));
      } else {
        response.setDescription(motd);
      }
    });
    mini.icon(response::setFavicon);

    if (mini.disablePlayerListHover()) {
      players.profiles().clear();
    }
    if (mini.hidePlayerCount()) {
      response.setHidePlayers(true);
    }
  }

  private boolean legacy(final @NonNull MinecraftVersion version) {
    try {
      return version.isLegacy()
        || (int) GET_PROTOCOL.invoke(version) < Constants.MINECRAFT_1_16_PROTOCOL_VERSION;
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to get protocol version", e);
    }
  }
}
