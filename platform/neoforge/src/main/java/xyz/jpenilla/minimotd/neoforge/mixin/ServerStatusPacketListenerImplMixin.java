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
package xyz.jpenilla.minimotd.neoforge.mixin;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MOTDConfig;
import xyz.jpenilla.minimotd.common.util.ComponentColorDownsampler;
import xyz.jpenilla.minimotd.neoforge.MiniMOTDNeoForge;
import xyz.jpenilla.minimotd.neoforge.access.ConnectionAccess;
import xyz.jpenilla.minimotd.neoforge.util.MutableServerStatus;

@Mixin(ServerStatusPacketListenerImpl.class)
abstract class ServerStatusPacketListenerImplMixin {
  @Shadow @Final private Connection connection;

  @Redirect(
    method = "handleStatusRequest",
    at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerStatusPacketListenerImpl;status:Lnet/minecraft/network/protocol/status/ServerStatus;")
  )
  public ServerStatus injectHandleStatusRequest(final ServerStatusPacketListenerImpl instance) {
    final MiniMOTDNeoForge miniMOTDNeoForge = MiniMOTDNeoForge.get();
    final MinecraftServer minecraftServer = miniMOTDNeoForge.requireServer();
    final ServerStatus vanillaStatus = Objects.requireNonNull(minecraftServer.getStatus(), "vanillaStatus");

    final MiniMOTD<ServerStatus.Favicon> miniMOTD = miniMOTDNeoForge.miniMOTD();
    final MOTDConfig config = miniMOTD.configManager().mainConfig();

    final PingResponse<ServerStatus.Favicon> response = miniMOTD.createMOTD(
      config,
      minecraftServer.getPlayerCount(),
      vanillaStatus.players().map(ServerStatus.Players::max).orElseGet(minecraftServer::getMaxPlayers)
    );

    final MutableServerStatus modifiedStatus = new MutableServerStatus(vanillaStatus);
    response.motd(motd -> {
      if (((ConnectionAccess) this.connection).protocolVersion() >= Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
        modifiedStatus.description(miniMOTDNeoForge.audiences().asNative(motd));
      } else {
        modifiedStatus.description(miniMOTDNeoForge.audiences().asNative(ComponentColorDownsampler.downsampler().downsample(motd)));
      }
    });
    response.icon(favicon -> modifiedStatus.favicon(Optional.of(favicon)));

    if (response.hidePlayerCount()) {
      modifiedStatus.players(Optional.empty());
    } else {
      final ServerStatus.Players newPlayers = new ServerStatus.Players(
        response.playerCount().maxPlayers(),
        response.playerCount().onlinePlayers(),
        response.disablePlayerListHover()
          ? Collections.emptyList()
          : vanillaStatus.players().map(ServerStatus.Players::sample).orElse(Collections.emptyList())
      );
      modifiedStatus.players(Optional.of(newPlayers));
    }

    return modifiedStatus.toServerStatus();
  }
}
