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

import com.google.inject.Inject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;
import org.spongepowered.plugin.metadata.PluginMetadata;
import xyz.jpenilla.minimotd.common.CommandHandlerFactory;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.UpdateChecker;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Objects;

@Plugin("minimotd-sponge8")
public final class MiniMOTDPlugin implements MiniMOTDPlatform<Favicon> {
  private final Path dataDirectory;
  private final PluginMetadata pluginMetadata;
  private final Logger logger;
  private final PluginContainer pluginContainer;
  private final MiniMOTD<Favicon> miniMOTD;

  @Inject
  public MiniMOTDPlugin(
    @ConfigDir(sharedRoot = false) final @NonNull Path dataDirectory,
    final @NonNull PluginContainer pluginContainer
  ) {
    this.dataDirectory = dataDirectory;
    this.pluginContainer = pluginContainer;
    this.pluginMetadata = pluginContainer.getMetadata();
    this.logger = LoggerFactory.getLogger(this.pluginMetadata.getId());
    this.miniMOTD = new MiniMOTD<>(this);
    Sponge.getEventManager().registerListener(
      pluginContainer,
      ClientPingServerEvent.class,
      new ClientPingServerEventListener(this.miniMOTD)
    );
  }

  @Listener
  public void onGameLoaded(final @NonNull LoadedGameEvent event) {
    Sponge.getAsyncScheduler().submit(Task.builder()
      .plugin(this.pluginContainer)
      .execute(() -> new UpdateChecker().checkVersion().forEach(this.logger::info))
      .build());
  }

  @Listener
  public void registerCommands(final @NonNull RegisterCommandEvent<Command.Parameterized> event) {
    final class WrappingExecutor implements CommandExecutor {
      private final CommandHandlerFactory.CommandHandler handler;

      WrappingExecutor(final CommandHandlerFactory.@NonNull CommandHandler handler) {
        this.handler = handler;
      }

      @Override
      public CommandResult execute(final @NonNull CommandContext context) {
        this.handler.execute(context.getCause().getAudience());
        return CommandResult.success();
      }
    }

    final CommandHandlerFactory handlerFactory = new CommandHandlerFactory(this.miniMOTD);
    final Command.Parameterized about = Command.builder()
      .setExecutor(new WrappingExecutor(handlerFactory.about()))
      .build();
    final Command.Parameterized help = Command.builder()
      .setExecutor(new WrappingExecutor(handlerFactory.help()))
      .build();
    final Command.Parameterized reload = Command.builder()
      .setExecutor(new WrappingExecutor(handlerFactory.reload()))
      .build();
    event.register(
      this.pluginContainer,
      Command.builder()
        .setPermission("minimotd.admin")
        .child(about, "about")
        .child(help, "help")
        .child(reload, "reload")
        .build(),
      "minimotd"
    );
  }

  @Listener
  private void onRefresh(final @NonNull RefreshGameEvent event) {
    this.miniMOTD.reload();
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
    return Objects.requireNonNull(Favicon.load(image), "failed to load favicon");
  }
}
