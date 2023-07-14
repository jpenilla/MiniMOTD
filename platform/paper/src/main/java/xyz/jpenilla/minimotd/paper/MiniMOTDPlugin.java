/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2023 Jason Penilla
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
package xyz.jpenilla.minimotd.paper;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

public class MiniMOTDPlugin extends JavaPlugin implements MiniMOTDPlatform<CachedServerIcon> {
  private Logger logger;
  private MiniMOTD<CachedServerIcon> miniMOTD;

  @Override
  public void onEnable() {
    this.logger = this.getSLF4JLogger();
    this.miniMOTD = new MiniMOTD<>(this);

    this.getServer().getPluginManager().registerEvents(new PingListener(this.miniMOTD), this);

    final PaperCommand command = new PaperCommand(this);
    this.getServer().getCommandMap().register("minimotd", command);

    new Metrics(this, 8132);

    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      try {
        Entity.class.getDeclaredMethod("getScheduler");
        CompletableFuture.runAsync(() -> new UpdateChecker().checkVersion().forEach(this.logger::info)).whenComplete(($, thr) -> {
          if (thr != null) {
            this.logger.warn("Exception checking for updates", thr);
          }
        });
      } catch (final ReflectiveOperationException ex) {
        this.getServer().getScheduler().runTaskAsynchronously(this, () ->
          new UpdateChecker().checkVersion().forEach(this.logger::info));
      }
    }
  }

  public @NonNull MiniMOTD<CachedServerIcon> miniMOTD() {
    return this.miniMOTD;
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
  public @NonNull CachedServerIcon loadIcon(final @NonNull BufferedImage image) throws Exception {
    return this.getServer().loadServerIcon(image);
  }
}
