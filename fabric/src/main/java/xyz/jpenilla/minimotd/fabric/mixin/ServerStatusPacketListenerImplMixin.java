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
package xyz.jpenilla.minimotd.fabric.mixin;

import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.jpenilla.minimotd.common.MOTDIconPair;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;
import xyz.jpenilla.minimotd.fabric.MiniMOTDFabric;

@Mixin(ServerStatusPacketListenerImpl.class)
public class ServerStatusPacketListenerImplMixin {
  @Shadow @Final private MinecraftServer server;
  @Shadow private boolean hasRequestedStatus;

  @Inject(method = "handleStatusRequest", at = @At("HEAD"))
  public void onRequest(final ServerboundStatusRequestPacket serverboundStatusRequestPacket, final CallbackInfo ci) {
    if (!this.hasRequestedStatus) {
      final MiniMOTDFabric miniMOTDFabric = MiniMOTDFabric.get();
      final MiniMOTD<String> miniMOTD = miniMOTDFabric.miniMOTD();
      final MiniMOTDConfig config = miniMOTD.configManager().mainConfig();

      final int onlinePlayers = miniMOTD.calculateOnlinePlayers(config, this.server.getPlayerCount());
      final int maxPlayers = config.adjustedMaxPlayers(onlinePlayers, this.server.getStatus().getPlayers().getMaxPlayers());

      final MOTDIconPair<String> pair = miniMOTD.createMOTD(config, onlinePlayers, maxPlayers);

      final String motdString = pair.motd();
      if (motdString != null) {
        final Component motdComponent = miniMOTDFabric.miniMessage().parse(motdString);
        this.server.getStatus().setDescription(net.minecraft.network.chat.Component.Serializer.fromJson(
          miniMOTDFabric.protocolVersionCache() >= 735
            ? miniMOTDFabric.gsonComponentSerializer().serialize(motdComponent)
            : miniMOTDFabric.downsamplingGsonComponentSerializer().serialize(motdComponent))
        );
      }

      final String favicon = pair.icon();
      if (favicon != null) {
        this.server.getStatus().setFavicon(favicon);
      }

      final GameProfile[] oldSample = this.server.getStatus().getPlayers().getSample();
      final ServerStatus.Players newPlayers = new ServerStatus.Players(maxPlayers, onlinePlayers);

      if (config.disablePlayerListHover()) {
        newPlayers.setSample(new GameProfile[]{});
      } else {
        newPlayers.setSample(oldSample);
      }

      this.server.getStatus().setPlayers(newPlayers);
    }
  }
}
