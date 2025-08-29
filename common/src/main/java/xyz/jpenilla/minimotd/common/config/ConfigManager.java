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
package xyz.jpenilla.minimotd.common.config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.util.Pair;

import static xyz.jpenilla.minimotd.common.util.Pair.pair;

public final class ConfigManager {

  private final MiniMOTD<?> miniMOTD;

  private final ConfigLoader<MOTDConfig> mainConfigLoader;
  private MOTDConfig mainConfig;

  private final ConfigLoader<PluginSettings> pluginSettingsLoader;
  private PluginSettings pluginSettings;

  private final Map<String, MOTDConfig> extraConfigs = new HashMap<>();

  public ConfigManager(final @NonNull MiniMOTD<?> miniMOTD) {
    this.miniMOTD = miniMOTD;
    this.mainConfigLoader = new ConfigLoader<>(
      MOTDConfig.class,
      this.miniMOTD.dataDirectory().resolve("main.conf"),
      options -> options.header("MiniMOTD Main Configuration")
    );
    this.pluginSettingsLoader = new ConfigLoader<>(
      PluginSettings.class,
      this.miniMOTD.dataDirectory().resolve("plugin_settings.conf"),
      options -> options.header("MiniMOTD Plugin Configuration")
    );
  }

  public void loadConfigs() {
    try {
      this.mainConfig = this.mainConfigLoader.load();
      this.mainConfigLoader.save(this.mainConfig);

      this.pluginSettings = this.pluginSettingsLoader.load();
      this.pluginSettingsLoader.save(this.pluginSettings);
    } catch (final ConfigurateException e) {
      throw new IllegalStateException("Failed to load config", e);
    }
  }

  public void loadExtraConfigs() {
    this.extraConfigs.clear();
    final Path extraConfigsDir = this.miniMOTD.dataDirectory().resolve("extra-configs");
    try {
      if (!Files.exists(extraConfigsDir)) {
        Files.createDirectories(extraConfigsDir);
        this.createDefaultExtraConfigs(extraConfigsDir);
      }
      try (final Stream<Path> stream = Files.list(extraConfigsDir)) {
        for (final Path path : stream.collect(Collectors.toList())) {
          if (!path.toString().endsWith(".conf")) {
            continue;
          }
          final String name = path.getFileName().toString().replace(".conf", "");
          final ConfigLoader<MOTDConfig> loader = new ConfigLoader<>(
            MOTDConfig.class,
            path,
            options -> options.header(String.format("Extra MiniMOTD config '%s'", name))
          );
          final MOTDConfig config = loader.load();
          loader.save(config);
          this.extraConfigs.put(name, config);
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Failed to load virtual host configs", e);
    }
  }

  private void createDefaultExtraConfigs(final @NonNull Path extraConfigsDir) throws ConfigurateException {
    final List<Pair<Path, MOTDConfig.MOTD>> defaults = Collections.unmodifiableList(Arrays.asList(
      pair(extraConfigsDir.resolve("skyblock.conf"), new MOTDConfig.MOTD("<green><italic>Skyblock</green>", "<bold><rainbow>MiniMOTD Skyblock Server")),
      pair(extraConfigsDir.resolve("survival.conf"), new MOTDConfig.MOTD("<gradient:blue:red>Survival Mode Hardcore", "<green><bold>MiniMOTD Survival Server"))
    ));
    for (final Pair<Path, MOTDConfig.MOTD> pair : defaults) {
      final ConfigLoader<MOTDConfig> loader = new ConfigLoader<>(
        MOTDConfig.class,
        pair.left()
      );
      loader.save(new MOTDConfig(pair.right()));
    }
  }

  public @NonNull MOTDConfig mainConfig() {
    if (this.mainConfig == null) {
      throw new IllegalStateException("Config has not yet been loaded");
    }
    return this.mainConfig;
  }

  public @NonNull PluginSettings pluginSettings() {
    if (this.pluginSettings == null) {
      throw new IllegalStateException("Config has not yet been loaded");
    }
    return this.pluginSettings;
  }

  public @NonNull MOTDConfig resolveConfig(final @Nullable InetSocketAddress address) {
    if (address == null) {
      return this.mainConfig();
    }
    final String configString = this.pluginSettings().proxySettings().findConfigStringForHost(address.getHostString(), address.getPort());

    if (this.pluginSettings().proxySettings().virtualHostTestMode()) {
      this.miniMOTD.platform().logger().info("[virtual-host-debug] Virtual Host: '{}:{}', Selected Config: '{}'", address.getHostString(), address.getPort(), configString == null ? "default" : configString);
    }

    if (configString == null) {
      return this.mainConfig();
    }
    return this.resolveConfig(configString);
  }

  public @NonNull MOTDConfig resolveConfig(final @NonNull String name) {
    if ("default".equals(name)) {
      return this.mainConfig();
    }
    final MOTDConfig cfg = this.extraConfigs.get(name);
    if (cfg != null) {
      return cfg;
    }
    this.miniMOTD.logger().warn("Invalid extra-config name: '{}', falling back to main.conf", name);
    return this.mainConfig();
  }

}
