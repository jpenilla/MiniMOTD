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
package xyz.jpenilla.minimotd.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.config.ConfigManager;
import xyz.jpenilla.minimotd.common.config.MiniMOTDConfig;

import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public final class MiniMOTD<I> {
  private final ConfigManager configManager;
  private final IconManager<I> iconManager;
  private final MiniMOTDPlatform<I> platform;

  public MiniMOTD(final @NonNull MiniMOTDPlatform<I> platform) {
    this.platform = platform;
    this.iconManager = new IconManager<>(this);
    this.configManager = new ConfigManager(this);
    this.configManager.loadConfigs();
  }

  public @NonNull MiniMOTDPlatform<I> platform() {
    return this.platform;
  }

  public final @NonNull Path dataDirectory() {
    return this.platform.dataDirectory();
  }

  public final @NonNull IconManager<I> iconManager() {
    return this.iconManager;
  }

  public final @NonNull Logger logger() {
    return this.platform.logger();
  }

  public final @NonNull ConfigManager configManager() {
    return this.configManager;
  }

  public final @NonNull MOTDIconPair<I> createMOTD(final @NonNull MiniMOTDConfig config, final int onlinePlayers, final int maxPlayers) {
    I icon = null;
    Component motd = null;
    String iconString = null;
    if (config.motdEnabled()) {
      if (config.motds().size() == 0) {
        throw new IllegalStateException("MOTD is enabled, but there are no MOTDs in the config file?");
      }
      final int index = config.motds().size() == 1 ? 0 : ThreadLocalRandom.current().nextInt(config.motds().size());
      final MiniMOTDConfig.MOTD motdConfig = config.motds().get(index);
      motd = TextComponent.ofChildren(
        MiniMessage.get().parse(replacePlayerCount(motdConfig.line1(), onlinePlayers, maxPlayers)),
        Component.newline(),
        MiniMessage.get().parse(replacePlayerCount(motdConfig.line2(), onlinePlayers, maxPlayers))
      );
      iconString = motdConfig.icon();
    }
    if (config.iconEnabled()) {
      icon = this.iconManager().icon(iconString);
    }
    return new MOTDIconPair<>(icon, motd);
  }

  private static @NonNull String replacePlayerCount(final @NonNull String input, final int onlinePlayers, final int maxPlayers) {
    return input.replace("{onlinePlayers}", String.valueOf(onlinePlayers))
      .replace("{maxPlayers}", String.valueOf(maxPlayers));
  }

  public final int calculateOnlinePlayers(final @NonNull MiniMOTDConfig config, final int realOnlinePlayers) {
    if (config.fakePlayersEnabled()) {
      try {
        final String fakePlayersConfigString = config.fakePlayers();
        if (fakePlayersConfigString.contains(":")) {
          final String[] fakePlayers = fakePlayersConfigString.split(":");
          final int start = Integer.parseInt(fakePlayers[0]);
          final int end = Integer.parseInt(fakePlayers[1]);

          return realOnlinePlayers + ThreadLocalRandom.current().nextInt(start, end);
        } else if (fakePlayersConfigString.contains("%")) {
          final double factor = 1 + (Double.parseDouble(fakePlayersConfigString.replace("%", "")) / 100);

          return (int) Math.ceil(factor * realOnlinePlayers);
        } else {
          final int addedPlayers = Integer.parseInt(fakePlayersConfigString);

          return realOnlinePlayers + addedPlayers;
        }
      } catch (final NumberFormatException ex) {
        this.logger().warn("fakePlayers config incorrect");
      }
    }
    return realOnlinePlayers;
  }

  public void reload() {
    this.iconManager.loadIcons();
    this.configManager.loadConfigs();
    this.platform.onReload();
  }
}
