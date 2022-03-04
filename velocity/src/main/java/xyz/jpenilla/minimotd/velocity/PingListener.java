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
package xyz.jpenilla.minimotd.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

@DefaultQualifier(NonNull.class)
public final class PingListener {
  private final MiniMOTD<Favicon> miniMOTD;

  @Inject
  private PingListener(final MiniMOTD<Favicon> miniMOTD) {
    this.miniMOTD = miniMOTD;
  }

  @Subscribe
  public EventTask onProxyPingEvent(final ProxyPingEvent event) {
    return EventTask.async(() -> this.handle(event));
  }

  private void handle(final ProxyPingEvent event) {
    final MiniMOTDConfig config = this.miniMOTD.configManager().resolveConfig(event.getConnection().getVirtualHost().orElse(null));
    final ServerPing.Builder pong = event.getPing().asBuilder();

    final PingResponse<Favicon> response = this.miniMOTD.createMOTD(config, pong.getOnlinePlayers(), pong.getMaximumPlayers());
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
