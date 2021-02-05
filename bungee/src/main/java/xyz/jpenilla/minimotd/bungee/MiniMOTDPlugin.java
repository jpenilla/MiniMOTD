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
package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.UpdateChecker;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class MiniMOTDPlugin extends Plugin implements MiniMOTDPlatform<Favicon> {
  private Logger logger;
  private BungeeAudiences audiences;
  private MiniMOTD<Favicon> miniMOTD;

  public @NonNull MiniMOTD<Favicon> miniMOTD() {
    return this.miniMOTD;
  }

  @Override
  public void onEnable() {
    this.logger = LoggerFactory.getLogger(this.getDescription().getName());
    this.miniMOTD = new MiniMOTD<>(this);
    this.miniMOTD.configManager().loadExtraConfigs();
    this.audiences = BungeeAudiences.create(this);
    this.getProxy().getPluginManager().registerListener(this, new PingListener(this.miniMOTD));
    this.getProxy().getPluginManager().registerCommand(this, new BungeeCommand(this));
    final Metrics metrics = new Metrics(this, 8137);

    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      this.getProxy().getScheduler().runAsync(this, () ->
        new UpdateChecker(this.getDescription().getVersion()).checkVersion().forEach(getLogger()::info));
    }
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.getDataFolder().toPath();
  }

  @Override
  public @NonNull Logger logger() {
    return this.logger;
  }

  @Override
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) {
    return Favicon.create(image);
  }

  public @NonNull BungeeAudiences audiences() {
    return this.audiences;
  }
}
