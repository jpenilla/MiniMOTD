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
package xyz.jpenilla.minimotd.fabric;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;
import xyz.jpenilla.minimotd.fabric.access.ServerStatusFaviconAccess;

import static net.minecraft.commands.Commands.literal;

public final class MiniMOTDFabric implements ModInitializer, MiniMOTDPlatform<ServerStatus.Favicon> {
  private static MiniMOTDFabric instance = null;

  private final Logger logger = LoggerFactory.getLogger(MiniMOTD.class);
  private final Path dataDirectory = FabricLoader.getInstance().getConfigDir().resolve("MiniMOTD");
  private final MiniMOTD<ServerStatus.Favicon> miniMOTD = new MiniMOTD<>(this);

  private MinecraftServer server;
  private MinecraftServerAudiences audiences;

  public MiniMOTDFabric() {
    if (instance != null) {
      throw new IllegalStateException("Cannot create a second instance of " + this.getClass().getName());
    }
    instance = this;
  }

  public @NonNull MiniMOTD<ServerStatus.Favicon> miniMOTD() {
    return this.miniMOTD;
  }

  @Override
  public void onInitialize() {
    this.registerCommand();
    ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
      this.server = minecraftServer;
      this.audiences = MinecraftServerAudiences.of(minecraftServer);
    });
    ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
      if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
        CompletableFuture.runAsync(() -> new UpdateChecker().checkVersion().forEach(this.logger()::info));
      }
    });
    ServerLifecycleEvents.SERVER_STOPPED.register($ -> {
      this.server = null;
      this.audiences = null;
    });
    this.miniMOTD.logger().info("Done initializing MiniMOTD");
  }

  private void registerCommand() {
    final class WrappingExecutor implements Command<CommandSourceStack> {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public int run(final @NonNull CommandContext<CommandSourceStack> context) {
        this.handler.execute(context.getSource());
        return Command.SINGLE_SUCCESS;
      }
    }

    final CommandHandler handler = new CommandHandler(this.miniMOTD);
    CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> dispatcher.register(
      literal("minimotd")
        .requires(source -> source.hasPermission(4))
        .then(literal("reload").executes(new WrappingExecutor(handler::reload)))
        .then(literal("about").executes(new WrappingExecutor(handler::about)))
        .then(literal("help").executes(new WrappingExecutor(handler::help)))
    ));
  }

  public @NonNull MinecraftServerAudiences audiences() {
    return this.audiences;
  }

  public static @NonNull MiniMOTDFabric get() {
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
