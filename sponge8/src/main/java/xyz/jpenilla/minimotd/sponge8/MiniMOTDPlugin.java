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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.plugin.jvm.Plugin;
import xyz.jpenilla.minimotd.common.IconManager;
import xyz.jpenilla.minimotd.common.Pair;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.nio.file.Path;
import java.util.Objects;

@Plugin("minimotd-sponge8")
public class MiniMOTDPlugin {

  @Inject
  private Logger logger;

  @Inject
  @ConfigDir(sharedRoot = false)
  private Path dataDirectory;

  private MiniMOTD miniMOTD;
  private final MiniMessage miniMessage = MiniMessage.get();
  private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

  @Listener
  public void onServerStart(final @NonNull LoadedGameEvent event) {
    this.miniMOTD = new MiniMOTD(this.dataDirectory, this.logger);
  }

  @Listener
  public void onPing(final @NonNull ClientPingServerEvent event) {
    final ClientPingServerEvent.Response response = event.getResponse();

    response.getPlayers().ifPresent(players -> {
      final MiniMOTDConfig config = this.miniMOTD.configManager().mainConfig();

      final int onlinePlayers = this.miniMOTD.calculateOnlinePlayers(config, players.getOnline());
      players.setOnline(onlinePlayers);

      final int maxPlayers = config.adjustedMaxPlayers(onlinePlayers, players.getMax());
      players.setMax(maxPlayers);

      final Pair<Favicon, String> pair = this.miniMOTD.createMOTD(config, onlinePlayers, maxPlayers);
      final Favicon favicon = pair.left();
      if (favicon != null) {
        response.setFavicon(favicon);
      }

      final String motdString = pair.right();
      if (motdString != null) {
        final Component motdComponent = this.miniMessage.parse(motdString);
        //if (pong.getVersion().getProtocol() < 735) { // todo
        //  motdComponent = this.legacySerializer.deserialize(this.legacySerializer.serialize(motdComponent));
        //}
        response.setDescription(motdComponent);
      }

      if (config.disablePlayerListHover()) {
        players.getProfiles().clear();
      }

    });
  }

  private static final class MiniMOTD extends xyz.jpenilla.minimotd.common.MiniMOTD<Favicon> { //todo command

    private final IconManager<Favicon> iconManager;

    MiniMOTD(final @NonNull Path dataDirectory, final @NonNull Logger logger) {
      super(dataDirectory, logger);
      this.iconManager = new IconManager<>(
        this,
        bufferedImage -> Objects.requireNonNull(Favicon.load(bufferedImage), "failed to load favicon")
      );
    }

    @Override
    public @NonNull IconManager<Favicon> iconManager() {
      return this.iconManager;
    }

  }

}
