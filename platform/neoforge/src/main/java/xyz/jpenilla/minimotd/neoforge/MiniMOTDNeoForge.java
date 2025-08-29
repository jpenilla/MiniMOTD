/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2024 Jason Penilla
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
package xyz.jpenilla.minimotd.neoforge;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.Commands;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.BrigadierUtil;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;
import xyz.jpenilla.minimotd.neoforge.access.ServerStatusFaviconAccess;

@Mod("minimotd")
public final class MiniMOTDNeoForge implements MiniMOTDPlatform<ServerStatus.Favicon> {
  private static MiniMOTDNeoForge instance = null;

  private final Logger logger = LoggerFactory.getLogger(MiniMOTD.class);
  private final Path dataDirectory = Path.of("config", "MiniMOTD");
  private final MiniMOTD<ServerStatus.Favicon> miniMOTD = new MiniMOTD<>(this);

  private MinecraftServer server;
  private MinecraftServerAudiences audiences;

  public MiniMOTDNeoForge() {
    if (instance != null) {
      throw new IllegalStateException("Cannot create a second instance of " + this.getClass().getName());
    }
    instance = this;

    this.registerCommand();
    NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
      this.server = event.getServer();
      this.audiences = MinecraftServerAudiences.of(event.getServer());
    });
    NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
      if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
        CompletableFuture.runAsync(() -> new UpdateChecker().checkVersion().forEach(this.logger()::info));
      }
    });
    NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
      this.server = null;
      this.audiences = null;
    });
    this.miniMOTD.logger().info("Done initializing MiniMOTD");
  }

  public @NonNull MiniMOTD<ServerStatus.Favicon> miniMOTD() {
    return this.miniMOTD;
  }

  private void registerCommand() {
    NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
      event.getDispatcher().register(
        BrigadierUtil.buildTree(
          new CommandHandler(this.miniMOTD),
          sourceStack -> this.audiences().audience(sourceStack),
          sourceStack -> sourceStack.hasPermission(Commands.LEVEL_ADMINS)
        )
      );
    });
  }

  public @NonNull MinecraftServerAudiences audiences() {
    return this.audiences;
  }

  public static @NonNull MiniMOTDNeoForge get() {
    return instance;
  }

  public @NonNull MinecraftServer requireServer() {
    if (this.server == null) {
      throw new IllegalStateException("Server requested before started");
    }
    return this.server;
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.dataDirectory;
  }

  @Override
  public @NonNull Logger logger() {
    return this.logger;
  }

  @Override
  public ServerStatus.@NonNull Favicon loadIcon(final @NonNull BufferedImage bufferedImage) throws Exception {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "PNG", out);
    final ServerStatus.Favicon favicon = new ServerStatus.Favicon(out.toByteArray());
    ((ServerStatusFaviconAccess) (Object) favicon).cacheEncodedIcon();
    return favicon;
  }
}
