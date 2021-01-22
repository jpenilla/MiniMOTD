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
package xyz.jpenilla.minimotd.common.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Comment("Here you can assign configs in the 'extra-configs' folder to specific virtual hosts\n"
      + "Either use the name of the config in 'extra-configs', or use \"default\" to use the configuration in main.conf\n"
      + "\n"
      + "Format is \"hostname:port\"=\"configName|default\"")
    private final Map<String, String> virtualHostConfigs = new HashMap<>();

  }

  public boolean updateChecker() {
    return this.updateChecker;
  }

  public @NonNull Optional<String> configStringForHost(final @NonNull String host) {
    return Optional.ofNullable(this.proxySettings.virtualHostConfigs.get(host));
  }

}
