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
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.Pair;
import xyz.jpenilla.minimotd.common.UpdateChecker;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.net.InetSocketAddress;
import java.nio.file.Path;

@Plugin(
  id = "{project.name}",
  name = "{rootProject.name}",
  version = "{version}",
  description = "{description}",
  url = "{url}",
  authors = {"jmp"}
)
public class MiniMOTDPlugin {
  public @NonNull MiniMOTD miniMOTD() {
    return this.miniMOTD;
  }

  private MiniMOTD miniMOTD;
  @Getter private final ProxyServer server;
  @Getter private final Logger logger;
  @Getter private final MiniMessage miniMessage = MiniMessage.get();
  @Getter private PluginDescription pluginDescription;
  private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

  @Inject private CommandManager commandManager;

  @Getter
  @Inject
  @DataDirectory
  private Path dataDirectory;

  @Inject
  public MiniMOTDPlugin(final @NonNull ProxyServer server, final @NonNull Logger logger) {
    this.server = server;
    this.logger = logger;
  }

  @Subscribe
  public void onProxyInitialization(final @NonNull ProxyInitializeEvent event) {
    this.miniMOTD = new MiniMOTD(this.dataDirectory, this.logger);
    this.server.getPluginManager().fromInstance(this).ifPresent(container -> this.pluginDescription = container.getDescription());
    this.commandManager.register(this.commandManager.metaBuilder("minimotd").build(), new VelocityCommand(this));

    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      this.server.getScheduler().buildTask(this, () ->
        new UpdateChecker(this.getPluginDescription().getVersion().orElse("")).checkVersion().forEach(this.logger::info))
        .schedule();
    }
  }

  @Subscribe
  public void onServerListPing(final @NonNull ProxyPingEvent ping) {
    final String configString = this.miniMOTD.configManager().pluginSettings().configStringForHost(ping.getConnection().getVirtualHost().map(InetSocketAddress::toString).orElse("default")).orElse("default");
    final MiniMOTDConfig config = this.miniMOTD.configManager().resolveConfig(configString);

    final ServerPing.Builder pong = ping.getPing().asBuilder();

    final int onlinePlayers = this.miniMOTD.calculateOnlinePlayers(config, pong.getOnlinePlayers());
    pong.onlinePlayers(onlinePlayers);

    final int maxPlayers = config.adjustedMaxPlayers(onlinePlayers, pong.getMaximumPlayers());
    pong.maximumPlayers(maxPlayers);

    final Pair<Favicon, String> pair = this.miniMOTD.createMOTD(config, onlinePlayers, maxPlayers);
    final Favicon favicon = pair.left();
    if (favicon != null) {
      pong.favicon(favicon);
    }

    final String motdString = pair.right();
    if (motdString != null) {
      Component motdComponent = this.miniMessage.parse(motdString);
      if (pong.getVersion().getProtocol() < 735) {
        motdComponent = this.legacySerializer.deserialize(this.legacySerializer.serialize(motdComponent));
      }
      pong.description(motdComponent);
    }

    if (config.disablePlayerListHover()) {
      pong.clearSamplePlayers();
    }

    ping.setPing(pong.build());
  }

}
