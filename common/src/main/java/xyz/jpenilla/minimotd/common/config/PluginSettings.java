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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public final class PluginSettings {

  @Comment("Do you want the plugin to check for updates on GitHub at launch?\n"
    + "https://github.com/jpenilla/MiniMOTD")
  private boolean updateChecker = true;

  @Comment("Settings only applicable when running the plugin on a proxy (Velocity or Waterfall/Bungeecord)")
  private ProxySettings proxySettings = new ProxySettings();

  @ConfigSerializable
  public static final class ProxySettings {

    public ProxySettings() {
      this.virtualHostConfigs.put("minigames.example.com:25565", "default");
      this.virtualHostConfigs.put("survival.example.com:25565", "survival");
      this.virtualHostConfigs.put("skyblock.example.com:25565", "skyblock");
    }

    public ProxySettings(final Map<String, String> virtualHostConfigs) {
      this.virtualHostConfigs.putAll(virtualHostConfigs);
    }

    @Comment("Here you can assign configs in the 'extra-configs' folder to specific virtual hosts\n"
      + "Either use the name of the config in 'extra-configs', or use \"default\" to use the configuration in main.conf\n"
      + "\n"
      + "Format is \"hostname:port\"=\"configName|default\"\n"
      + "Parts of domains can be substituted for wildcards, i.e. \"*.mydomain.com:25565\". Wildcard-containing configs are\n"
      + "checked in the order they are declared if there are no exact matches.")
    private final Map<String, String> virtualHostConfigs = new LinkedHashMap<>();

    private transient @Nullable Map<String[], String> splitVirtualHostConfigs;

    @Comment("Set whether to enable virtual host testing mode.\n"
      + "When enabled, MiniMOTD will print virtual host debug info to the console on each server ping.")
    private boolean virtualHostTestMode = false;

    public boolean virtualHostTestMode() {
      return this.virtualHostTestMode;
    }

    public @Nullable String findConfigStringForHost(@NonNull String host, final int port) {
      Objects.requireNonNull(this.splitVirtualHostConfigs, "processVirtualHosts must be called first");
      host = processTcpShieldHostname(host).toLowerCase(Locale.ENGLISH) + ':' + port;

      final @Nullable String exactMatch = this.virtualHostConfigs.get(host);
      if (exactMatch != null) {
        return exactMatch;
      }

      if (this.splitVirtualHostConfigs.isEmpty()) {
        return null;
      }

      final String[] splitHost = host.split("\\.");

      configs:
      for (final Map.Entry<String[], String> e : this.splitVirtualHostConfigs.entrySet()) {
        final String[] splitKey = e.getKey();
        if (splitKey.length != splitHost.length) {
          continue;
        }
        for (int i = 0; i < splitHost.length; i++) {
          final String keyPart = splitKey[i];
          if (!keyPart.equals(splitHost[i]) && !keyPart.equals("*")) {
            continue configs;
          }
        }
        return e.getValue();
      }

      return null;
    }

    private static String processTcpShieldHostname(final String hostname) {
      if (hostname.contains("///")) {
        final String[] split = hostname.split("///");
        if (split.length == 4) {
          // <actual hostname>///<user-ip>:<user-port>///<unix timestamp>///<signature>
          return split[0];
        }
      }
      return hostname;
    }

    public void processVirtualHosts() {
      final Map<String, String> virtualHosts = new LinkedHashMap<>(this.virtualHostConfigs);
      this.virtualHostConfigs.clear();
      virtualHosts.forEach((host, config) -> this.virtualHostConfigs.put(host.toLowerCase(Locale.ENGLISH), config));

      this.splitVirtualHostConfigs = new LinkedHashMap<>();
      this.virtualHostConfigs.forEach((host, config) -> {
        if (!host.contains("*")) {
          return;
        }
        this.splitVirtualHostConfigs.put(host.split("\\."), config);
      });
    }
  }

  public @NonNull ProxySettings proxySettings() {
    return this.proxySettings;
  }

  public boolean updateChecker() {
    return this.updateChecker;
  }

  @PostProcessor
  private void processVirtualHosts() {
    this.proxySettings.processVirtualHosts();
  }
}
