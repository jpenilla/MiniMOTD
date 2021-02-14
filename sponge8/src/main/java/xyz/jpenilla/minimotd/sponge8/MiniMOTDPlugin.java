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
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;
import org.spongepowered.plugin.metadata.PluginMetadata;
import xyz.jpenilla.minimotd.common.Constants;
import xyz.jpenilla.minimotd.common.MOTDIconPair;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.UpdateChecker;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Objects;

@Plugin("minimotd-sponge8") // todo commands
public class MiniMOTDPlugin implements MiniMOTDPlatform<Favicon> {
  private final Path dataDirectory;
  private final PluginMetadata pluginMetadata;
  private final Logger logger;
  private final MiniMessage miniMessage = MiniMessage.get();
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
  }

  @Listener
  public void onServerStart(final @NonNull LoadedGameEvent event) {
    Sponge.getAsyncScheduler().submit(Task.builder().plugin(this.pluginContainer).execute(() ->
      new UpdateChecker(this.pluginMetadata.getVersion()).checkVersion().forEach(this.logger::info)
    ).build());
  }

  private Method SpongeMinecraftVersion_getProtocol;

  @Listener
  public void onPing(final @NonNull ClientPingServerEvent event) {
    final ClientPingServerEvent.Response response = event.getResponse();

    response.getPlayers().ifPresent(players -> {
      final MiniMOTDConfig config = this.miniMOTD.configManager().mainConfig();

      final int onlinePlayers = this.miniMOTD.calculateOnlinePlayers(config, players.getOnline());
      players.setOnline(onlinePlayers);

      final int maxPlayers = config.adjustedMaxPlayers(onlinePlayers, players.getMax());
      players.setMax(maxPlayers);

      final @NonNull MOTDIconPair<Favicon> pair = this.miniMOTD.createMOTD(config, onlinePlayers, maxPlayers);

      final String motdString = pair.motd();
      try {
        if (motdString != null) {
          Component motdComponent = this.miniMessage.parse(motdString);
          final MinecraftVersion version = event.getClient().getVersion();
          if (!version.isLegacy() && this.SpongeMinecraftVersion_getProtocol == null) {
            this.SpongeMinecraftVersion_getProtocol = version.getClass().getMethod("getProtocol");
          }
          if (version.isLegacy() || (int) this.SpongeMinecraftVersion_getProtocol.invoke(version) < Constants.MINECRAFT_1_16_PROTOCOL_VERSION) {
            motdComponent = GsonComponentSerializer.colorDownsamplingGson().deserialize(GsonComponentSerializer.colorDownsamplingGson().serialize(motdComponent));
          }
          response.setDescription(motdComponent);
        }
      } catch (final ReflectiveOperationException e) {
        throw new IllegalStateException("Failed to get protocol version", e);
      }

      final Favicon favicon = pair.icon();
      if (favicon != null) {
        response.setFavicon(favicon);
      }

      if (config.disablePlayerListHover()) {
        players.getProfiles().clear();
      }
    });
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
