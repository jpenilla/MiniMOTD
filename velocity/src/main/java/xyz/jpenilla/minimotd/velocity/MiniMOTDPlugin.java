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
package xyz.jpenilla.minimotd.velocity;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.velocity.Metrics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.CommandHandlerFactory;
import xyz.jpenilla.minimotd.common.ComponentColorDownsampler;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MOTDIconPair;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.UpdateChecker;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

@Plugin(
  id = "${project.name}",
  name = Constants.PluginMetadata.NAME,
  version = Constants.PluginMetadata.VERSION,
  description = "${description}",
  url = "${url}",
  authors = {"jmp"}
)
public final class MiniMOTDPlugin implements MiniMOTDPlatform<Favicon> {
  private final MiniMOTD<Favicon> miniMOTD;
  private final ProxyServer server;
  private final Logger logger;
  private final MiniMessage miniMessage = MiniMessage.get();
  private final PluginContainer pluginContainer;
  private final CommandManager commandManager;
  private final Path dataDirectory;
  private final Metrics.Factory metricsFactory;

  @Inject
  public MiniMOTDPlugin(
    final @NonNull ProxyServer server,
    final @NonNull Logger logger,
    final @NonNull CommandManager commandManager,
    final @NonNull PluginContainer pluginContainer,
    @DataDirectory final @NonNull Path dataDirectory,
    final Metrics.@NonNull Factory metricsFactory
  ) {
    this.server = server;
    this.logger = logger;
    this.commandManager = commandManager;
    this.pluginContainer = pluginContainer;
    this.dataDirectory = dataDirectory;
    this.metricsFactory = metricsFactory;
    this.miniMOTD = new MiniMOTD<>(this);
    this.miniMOTD.configManager().loadExtraConfigs();
  }

  @Subscribe
  public void onProxyInitialization(final @NonNull ProxyInitializeEvent event) {
    this.registerCommand();
    this.metricsFactory.make(this, 10257);
    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      this.server.getScheduler().buildTask(
        this,
        () -> new UpdateChecker().checkVersion().forEach(this.logger::info)
      ).schedule();
    }
  }

  private void registerCommand() {
    final class WrappingExecutor implements Command<CommandSource> {
      private final CommandHandlerFactory.CommandHandler handler;

      WrappingExecutor(final CommandHandlerFactory.@NonNull CommandHandler handler) {
        this.handler = handler;
      }

      @Override
      public int run(final @NonNull CommandContext<CommandSource> context) {
        this.handler.execute(context.getSource());
        return Command.SINGLE_SUCCESS;
      }
    }

    final CommandHandlerFactory handlerFactory = new CommandHandlerFactory(this.miniMOTD);
    this.commandManager.register(this.commandManager.metaBuilder("minimotd").build(), new BrigadierCommand(
      LiteralArgumentBuilder.<CommandSource>literal("minimotd")
        .requires(source -> source.hasPermission("minimotd.admin"))
        .then(LiteralArgumentBuilder.<CommandSource>literal("help").executes(new WrappingExecutor(handlerFactory.help())))
        .then(LiteralArgumentBuilder.<CommandSource>literal("about").executes(new WrappingExecutor(handlerFactory.about())))
        .then(LiteralArgumentBuilder.<CommandSource>literal("reload").executes(new WrappingExecutor(handlerFactory.reload())))
    ));
  }

  @Subscribe
  public void onServerListPing(final @NonNull ProxyPingEvent ping) {
    final String configString = this.miniMOTD.configManager().pluginSettings().configStringForHost(
      ping.getConnection().getVirtualHost()
        .map(inetSocketAddress -> String.format("%s:%s", inetSocketAddress.getHostName(), inetSocketAddress.getPort()))
        .orElse("default")
    ).orElse("default");
    final MiniMOTDConfig config = this.miniMOTD.configManager().resolveConfig(configString);

    final ServerPing.Builder pong = ping.getPing().asBuilder();

    final int onlinePlayers = this.miniMOTD.calculateOnlinePlayers(config, pong.getOnlinePlayers());
    pong.onlinePlayers(onlinePlayers);

    final int maxPlayers = config.adjustedMaxPlayers(onlinePlayers, pong.getMaximumPlayers());
    pong.maximumPlayers(maxPlayers);

    final MOTDIconPair<Favicon> pair = this.miniMOTD.createMOTD(config, onlinePlayers, maxPlayers);
    final Favicon favicon = pair.icon();
    if (favicon != null) {
      pong.favicon(favicon);
    }

    final Component motdComponent = pair.motd();
    if (motdComponent != null) {
      if (pong.getVersion().getProtocol() < Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
        pong.description(ComponentColorDownsampler.downsampler().downsample(motdComponent));
      } else {
        pong.description(motdComponent);
      }
    }

    if (config.disablePlayerListHover()) {
      pong.clearSamplePlayers();
    }

    ping.setPing(pong.build());
  }

  public @NonNull MiniMOTD<Favicon> miniMOTD() {
    return this.miniMOTD;
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
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) {
    return Favicon.create(image);
  }

  public @NonNull MiniMessage miniMessage() {
    return this.miniMessage;
  }

  public @NonNull PluginDescription pluginDescription() {
    return this.pluginContainer.getDescription();
  }

  @Override
  public void onReload() {
    this.miniMOTD.configManager().loadExtraConfigs();
  }
}
