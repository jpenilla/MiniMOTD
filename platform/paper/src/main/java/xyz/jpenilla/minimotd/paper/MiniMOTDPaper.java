/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2024 Jason Penilla
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

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.RegionizedServerInitEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.BrigadierUtil;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

public final class MiniMOTDPaper extends JavaPlugin implements MiniMOTDPlatform<CachedServerIcon>, Listener {

  private Logger logger;
  private MiniMOTD<CachedServerIcon> miniMOTD;

  @Override
  public void onEnable() {
    this.getServer().getPluginManager().registerEvents(this, this);
    this.logger = LoggerFactory.getLogger(this.getName());
    this.miniMOTD = new MiniMOTD<>(this);

    this.getServer().getPluginManager().registerEvents(new PingListener(this.miniMOTD), this);

    this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(
      this.getPluginMeta(),
      BrigadierUtil.<CommandSourceStack>buildTree(
        new CommandHandler(this.miniMOTD),
        sourceStack -> Objects.requireNonNullElse(sourceStack.getExecutor(), sourceStack.getSender()),
        sourceStack -> sourceStack.getSender().hasPermission("minimotd.admin")
      ).build(),
      "MiniMOTD Command",
      List.of()
    ));

    final Metrics metrics = new Metrics(this, 8132);
    metrics.addCustomChart(new SimplePie("variant", () -> "paper"));
  }

  @EventHandler
  public void updateCheck(final RegionizedServerInitEvent event) {
    if (!this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      return;
    }
    CompletableFuture.runAsync(() -> new UpdateChecker().checkVersion().forEach(this.logger::info)).whenComplete(($, thr) -> {
      if (thr != null) {
        this.logger.warn("Exception checking for updates", thr);
      }
    });
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
