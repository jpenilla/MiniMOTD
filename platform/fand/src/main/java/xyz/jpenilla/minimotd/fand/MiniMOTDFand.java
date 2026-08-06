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
package xyz.jpenilla.minimotd.fand;

import io.fand.api.Fand;
import io.fand.api.event.server.ServerListIcon;
import io.fand.api.event.server.ServerListPingEvent;
import io.fand.api.plugin.Plugin;
import io.fand.api.plugin.PluginContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.PingResponse;
import xyz.jpenilla.minimotd.common.config.MOTDConfig;

public final class MiniMOTDFand implements Plugin, MiniMOTDPlatform<ServerListIcon> {
  private PluginContext context;
  private MiniMOTD<ServerListIcon> miniMOTD;
  private CommandHandler commandHandler;

  @Override
  public void onEnable(final PluginContext context) {
    this.context = context;
    this.miniMOTD = new MiniMOTD<>(this);
    this.commandHandler = new CommandHandler(this.miniMOTD);

    context.commands().register("minimotd", command -> command
      .permission("minimotd.admin")
      .executes(commandContext -> this.commandHandler.help(commandContext.sender()))
      .literal("about", about -> about.executes(commandContext -> this.commandHandler.about(commandContext.sender())))
      .literal("reload", reload -> reload.executes(commandContext -> this.commandHandler.reload(commandContext.sender())))
      .literal("help", help -> help.executes(commandContext -> this.commandHandler.help(commandContext.sender()))));
    context.events().subscribe(ServerListPingEvent.class, this::handlePing);
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.context.dataDirectory();
  }

  @Override
  public @NonNull Logger logger() {
    return this.context.logger();
  }

  @Override
  public @NonNull ServerListIcon loadIcon(final @NonNull BufferedImage image) throws Exception {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageIO.write(image, "png", output);
    return new ServerListIcon(output.toByteArray());
  }

  @Override
  public void onReload() {
    Fand.server().refreshServerListStatus();
  }

  private void handlePing(final ServerListPingEvent event) {
    final MOTDConfig config = this.miniMOTD.configManager().mainConfig();
    final PingResponse<ServerListIcon> response = this.miniMOTD.createMOTD(
      config,
      event.onlinePlayers(),
      event.maxPlayers()
    );

    response.playerCount().applyCount(event::setOnlinePlayers, event::setMaxPlayers);
    response.motd(event::setMotd);
    response.icon(event::setIcon);

    if (response.disablePlayerListHover()) {
      event.setSamplePlayers(List.of());
    }
    if (response.hidePlayerCount()) {
      event.setHidePlayers(true);
    }
  }
}
