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
package xyz.jpenilla.minimotd.fabric.mixin;

import com.mojang.authlib.GameProfile;
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
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;
import xyz.jpenilla.minimotd.common.util.ComponentColorDownsampler;
import xyz.jpenilla.minimotd.fabric.MiniMOTDFabric;
import xyz.jpenilla.minimotd.fabric.access.ConnectionAccess;

@Mixin(ServerStatusPacketListenerImpl.class)
abstract class ServerStatusPacketListenerImplMixin {
  @Shadow @Final private Connection connection;

  @Redirect(method = "handleStatusRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
  public ServerStatus injectHandleStatusRequest(final MinecraftServer minecraftServer) {
    final ServerStatus vanillaStatus = minecraftServer.getStatus();

    final ServerStatus modifiedStatus = new ServerStatus();
    modifiedStatus.setDescription(vanillaStatus.getDescription());
    modifiedStatus.setFavicon(vanillaStatus.getFavicon());
    modifiedStatus.setVersion(vanillaStatus.getVersion());

    final MiniMOTDFabric miniMOTDFabric = MiniMOTDFabric.get();
    final MiniMOTD<String> miniMOTD = miniMOTDFabric.miniMOTD();
    final MiniMOTDConfig config = miniMOTD.configManager().mainConfig();

    final PingResponse<String> response = miniMOTD.createMOTD(config, minecraftServer.getPlayerCount(), vanillaStatus.getPlayers().getMaxPlayers());

    response.motd(motd -> {
      if (((ConnectionAccess) this.connection).protocolVersion() >= Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
        modifiedStatus.setDescription(miniMOTDFabric.audiences().toNative(motd));
      } else {
        modifiedStatus.setDescription(miniMOTDFabric.audiences().toNative(ComponentColorDownsampler.downsampler().downsample(motd)));
      }
    });
    response.icon(modifiedStatus::setFavicon);

    if (!response.hidePlayerCount()) {
      final GameProfile[] oldSample = vanillaStatus.getPlayers().getSample();
      final ServerStatus.Players newPlayers = new ServerStatus.Players(response.playerCount().maxPlayers(), response.playerCount().onlinePlayers());
      if (response.disablePlayerListHover()) {
        newPlayers.setSample(new GameProfile[]{});
      } else {
        newPlayers.setSample(oldSample);
      }
      modifiedStatus.setPlayers(newPlayers);
    }

    return modifiedStatus;
  }
}
