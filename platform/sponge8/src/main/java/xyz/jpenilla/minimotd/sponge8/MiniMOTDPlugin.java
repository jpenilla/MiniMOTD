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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;
import org.spongepowered.plugin.metadata.PluginMetadata;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

@Plugin("minimotd-sponge8")
public final class MiniMOTDPlugin implements MiniMOTDPlatform<Favicon> {
  private final Path dataDirectory;
  private final PluginMetadata pluginMetadata;
  private final Logger logger;
  private final PluginContainer pluginContainer;
  private final MiniMOTD<Favicon> miniMOTD;
  private final Injector injector;

  @Inject
  public MiniMOTDPlugin(
    @ConfigDir(sharedRoot = false) final @NonNull Path dataDirectory,
    final @NonNull PluginContainer pluginContainer,
    final @NonNull Injector injector
  ) {
    this.dataDirectory = dataDirectory;
    this.pluginContainer = pluginContainer;
    this.pluginMetadata = pluginContainer.metadata();
    this.logger = LoggerFactory.getLogger(this.pluginMetadata.id());
    this.miniMOTD = new MiniMOTD<>(this);
    this.injector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        this.bind(new TypeLiteral<MiniMOTD<Favicon>>() {
        }).toInstance(MiniMOTDPlugin.this.miniMOTD);
      }
    });
    Sponge.eventManager().registerListener(
      EventListenerRegistration.builder(ClientPingServerEvent.class)
        .plugin(pluginContainer)
        .listener(this.injector.getInstance(ClientPingServerEventListener.class))
        .order(Order.DEFAULT)
        .build()
    );
  }

  @Listener
  public void onGameLoaded(final @NonNull LoadedGameEvent event) {
    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      Sponge.asyncScheduler().submit(Task.builder()
        .plugin(this.pluginContainer)
        .execute(() -> new UpdateChecker().checkVersion().forEach(this.logger::info))
        .build());
    }
  }

  @Listener
  public void registerCommands(final @NonNull RegisterCommandEvent<Command.Parameterized> event) {
    final class WrappingExecutor implements CommandExecutor {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public CommandResult execute(final @NonNull CommandContext context) {
        this.handler.execute(context.cause().audience());
        return CommandResult.success();
      }
    }

    final CommandHandler handler = new CommandHandler(this.miniMOTD);
    final Command.Parameterized about = Command.builder()
      .executor(new WrappingExecutor(handler::about))
      .build();
    final Command.Parameterized help = Command.builder()
      .executor(new WrappingExecutor(handler::help))
      .build();
    final Command.Parameterized reload = Command.builder()
      .executor(new WrappingExecutor(handler::reload))
      .build();
    event.register(
      this.pluginContainer,
      Command.builder()
        .permission("minimotd.admin")
        .addChild(about, "about")
        .addChild(help, "help")
        .addChild(reload, "reload")
        .build(),
      "minimotd"
    );
  }

  @Listener
  public void onRefresh(final @NonNull RefreshGameEvent event) {
    try {
      this.miniMOTD.reload();
    } catch (final Exception ex) {
      this.miniMOTD.logger().warn("Failed to reload MiniMOTD.", ex);
    }
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
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) throws Exception {
    return Favicon.load(image);
  }
}
