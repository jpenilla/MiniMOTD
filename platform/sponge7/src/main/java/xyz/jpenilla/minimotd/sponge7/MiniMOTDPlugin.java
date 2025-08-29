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
package xyz.jpenilla.minimotd.sponge7;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import org.bstats.sponge.Metrics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

@Plugin(id = "minimotd-sponge7")
public final class MiniMOTDPlugin implements MiniMOTDPlatform<Favicon> {
  private final Logger logger;
  private final Path dataDirectory;
  private final MiniMOTD<Favicon> miniMOTD;
  private final SpongeAudiences audiences;
  private final Game game;
  private final Injector injector;

  @Inject
  public MiniMOTDPlugin(
    final @NonNull Logger logger,
    @ConfigDir(sharedRoot = false) final @NonNull Path dataDirectory,
    final @NonNull SpongeAudiences audiences,
    final Metrics.@NonNull Factory metricsFactory,
    final @NonNull Injector injector,
    final @NonNull Game game
  ) {
    this.logger = logger;
    this.dataDirectory = dataDirectory;
    this.audiences = audiences;
    this.game = game;
    this.miniMOTD = new MiniMOTD<>(this);
    this.injector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        this.bind(new TypeLiteral<MiniMOTD<Favicon>>() {
        }).toInstance(MiniMOTDPlugin.this.miniMOTD);
      }
    });
    metricsFactory.make(10768);
  }

  @Listener
  public void gameStarted(final @NonNull GameStartedServerEvent event) {
    Sponge.getEventManager().registerListener(this, ClientPingServerEvent.class, this.injector.getInstance(ClientPingServerEventListener.class));
    this.registerCommands();
    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      Task.builder()
        .async()
        .execute(() -> new UpdateChecker().checkVersion().forEach(this.logger::info))
        .submit(this);
    }
  }

  @Listener
  public void reloaded(final @NonNull GameReloadEvent event) {
    try {
      this.miniMOTD.reload();
    } catch (final Exception ex) {
      this.miniMOTD.logger().warn("Failed to reload MiniMOTD.", ex);
    }
  }

  private void registerCommands() {
    final class WrappingExecutor implements CommandExecutor {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public @NonNull CommandResult execute(final @NonNull CommandSource src, final @NonNull CommandContext args) {
        this.handler.execute(MiniMOTDPlugin.this.audiences.receiver(src));
        return CommandResult.success();
      }
    }

    final CommandHandler handler = new CommandHandler(this.miniMOTD);
    final CommandSpec help = CommandSpec.builder()
      .executor(new WrappingExecutor(handler::help))
      .build();
    final CommandSpec about = CommandSpec.builder()
      .executor(new WrappingExecutor(handler::about))
      .build();
    final CommandSpec reload = CommandSpec.builder()
      .executor(new WrappingExecutor(handler::reload))
      .build();
    Sponge.getCommandManager().register(
      this,
      CommandSpec.builder()
        .permission("minimotd.admin")
        .child(help, "help")
        .child(about, "about")
        .child(reload, "reload")
        .build(),
      "minimotd"
    );
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
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) throws IOException {
    return this.game.getRegistry().loadFavicon(image);
  }
}
