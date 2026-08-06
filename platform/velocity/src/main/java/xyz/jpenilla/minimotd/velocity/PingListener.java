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
package xyz.jpenilla.minimotd.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MOTDConfig;

@NullMarked
public final class PingListener {
  private final MiniMOTD<Favicon> miniMOTD;
  private final ProxyServer proxy;

  @Inject
  private PingListener(final MiniMOTD<Favicon> miniMOTD, final ProxyServer proxy) {
    this.miniMOTD = miniMOTD;
    this.proxy = proxy;
  }

  @Subscribe
  public EventTask onProxyPingEvent(final ProxyPingEvent event) {
    return EventTask.async(() -> this.handle(event));
  }

  private void handle(final ProxyPingEvent event) {
    final MOTDConfig config = this.miniMOTD.configManager().resolveConfig(event.getConnection().getVirtualHost().orElse(null));
    final ServerPing.Builder pong = event.getPing().asBuilder();
    final List<String> targetServers = config.targetServers();

    int playersCount = 0;
    boolean allServersOffline = false;
    if (targetServers.isEmpty()) {
      playersCount = pong.getOnlinePlayers();
      // See if all servers offline when no targets are specified
      allServersOffline = this.proxy.getAllServers().stream()
        .allMatch(server -> server.getPlayersConnected().isEmpty());
    } else {
      final Set<ServerPing.SamplePlayer> players = new HashSet<>();
      int onlineServerCount = 0;
      for (final String serverName : targetServers) {
        final @Nullable RegisteredServer server = this.proxy.getServer(serverName).orElse(null);
        if (server != null) {
          final int serverPlayers = server.getPlayersConnected().size();
          if (serverPlayers > 0) {
            onlineServerCount++;
          }
          playersCount += serverPlayers;
          players.addAll(server.getPlayersConnected().stream()
            .map(p -> new ServerPing.SamplePlayer(p.getGameProfile().getName(), p.getUniqueId()))
            .collect(Collectors.toList()));
        }
      }
      // All servers offline if 0 players and 0 online servers
      allServersOffline = (onlineServerCount == 0 && playersCount == 0);
      pong.clearSamplePlayers();
      pong.samplePlayers(players.toArray(new ServerPing.SamplePlayer[0]));
    }

    // Use offline config if true and all servers are offline
    final MOTDConfig activeConfig;

    if (allServersOffline && config.useOfflineMotd()) {
      // Try for offline-specific config
      final @Nullable MOTDConfig offlineConfig = this.miniMOTD.configManager().getOfflineConfig();
      activeConfig = offlineConfig != null ? offlineConfig : config;
    } else {
      activeConfig = config;
    }

    final PingResponse<Favicon> response = this.miniMOTD.createMOTD(activeConfig, playersCount, pong.getMaximumPlayers());
    response.icon(pong::favicon);
    response.motd(pong::description);
    response.playerCount().applyCount(pong::onlinePlayers, pong::maximumPlayers);

    if (response.disablePlayerListHover()) {
      pong.clearSamplePlayers();
    }
    if (response.hidePlayerCount()) {
      pong.nullPlayers();
    }

    event.setPing(pong.build());
  }
}
