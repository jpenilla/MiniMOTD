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
import org.spongepowered.configurate.ConfigurateException;
import xyz.jpenilla.minimotd.common.MiniMOTD;

public final class ConfigManager {

  private final MiniMOTD<?> miniMOTD;
  private final ConfigLoader<MiniMOTDConfig> configLoader;
  private MiniMOTDConfig config;

  public ConfigManager(final @NonNull MiniMOTD<?> miniMOTD) {
    this.miniMOTD = miniMOTD;
    this.configLoader = new ConfigLoader<>(
      MiniMOTDConfig.class,
      this.miniMOTD.dataDirectory().resolve("main.conf"),
      options -> options.header("MiniMOTD Configuration")
    );
  }

  public void loadConfigs() {
    try {
      this.config = this.configLoader.load();
      this.configLoader.save(this.config);
    } catch (final ConfigurateException e) {
      throw new IllegalStateException("Failed to load config", e);
    }
  }

  public @NonNull MiniMOTDConfig config() {
    if (this.config == null) {
      throw new IllegalStateException("Config has not yet been loaded");
    }
    return this.config;
  }

}
