/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Jason Penilla
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
package xyz.jpenilla.minimotd.bukkit;

import io.papermc.lib.PaperLib;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

public final class MiniMOTDPlugin extends JavaPlugin implements MiniMOTDPlatform<CachedServerIcon> {
  private static final boolean PAPER_PING_EVENT_EXISTS = findClass("com.destroystokyo.paper.event.server.PaperServerListPingEvent") != null;

  private Logger logger;
  private MiniMOTD<CachedServerIcon> miniMOTD;
  private BukkitAudiences audiences;

  @Override
  public void onEnable() {
    this.logger = LoggerFactory.getLogger(this.getName());
    this.miniMOTD = new MiniMOTD<>(this);
    this.audiences = BukkitAudiences.create(this);

    if (PAPER_PING_EVENT_EXISTS) {
      this.getServer().getPluginManager().registerEvents(new PaperPingListener(this.miniMOTD), this);
    } else {
      this.getServer().getPluginManager().registerEvents(new PingListener(this.miniMOTD), this);
      if (PaperLib.getMinecraftVersion() >= 12) { // PaperServerListPingEvent was added in 1.12
        this.suggestPaper();
      }
    }

    final PluginCommand command = this.getCommand("minimotd");
    if (command != null) {
      final BukkitCommand bukkitCommand = new BukkitCommand(this);
      command.setExecutor(bukkitCommand);
      command.setTabCompleter(bukkitCommand);
    }

    final Metrics metrics = new Metrics(this, 8132);

    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      this.getServer().getScheduler().runTaskAsynchronously(this, () ->
        new UpdateChecker().checkVersion().forEach(this.logger::info));
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

  public @NonNull BukkitAudiences audiences() {
    return this.audiences;
  }

  private void suggestPaper() {
    this.logger.warn("======================================================");
    this.logger.warn(" MiniMOTD works better if you use Paper as your server");
    this.logger.warn(" software.");
    this.logger.warn(" ");
    this.logger.warn(" Spigot does not include the necessary APIs for all");
    this.logger.warn(" of MiniMOTD's features to operate. MiniMOTD was");
    this.logger.warn(" designed to work with Paper and it's expanded API");
    this.logger.warn(" for full compatibility.");
    this.logger.warn(" ");
    this.logger.warn(" Get Paper from https://papermc.io/downloads");
    this.logger.warn("======================================================");
  }

  private static @Nullable Class<?> findClass(final @NonNull String className) {
    try {
      return Class.forName(className);
    } catch (final ClassNotFoundException ex) {
      return null;
    }
  }
}
