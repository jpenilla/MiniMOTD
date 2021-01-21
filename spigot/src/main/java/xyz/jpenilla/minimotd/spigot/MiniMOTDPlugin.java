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
package xyz.jpenilla.minimotd.spigot;

import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jpenilla.minimotd.common.UpdateChecker;

public final class MiniMOTDPlugin extends JavaPlugin {
  private MiniMOTD miniMOTD;
  @Getter private static MiniMOTDPlugin instance;
  @Getter private PrismaHook prisma;
  @Getter private boolean isPaperServer;
  @Getter private String serverPackageName;
  @Getter private String serverApiVersion;
  @Getter private int majorMinecraftVersion;
  @Getter private BukkitAudiences audiences;

  @Override
  public void onEnable() {
    this.miniMOTD = new MiniMOTD(this);
    this.serverPackageName = this.getServer().getClass().getPackage().getName();
    this.serverApiVersion = this.serverPackageName.substring(this.serverPackageName.lastIndexOf('.') + 1);
    this.majorMinecraftVersion = Integer.parseInt(this.serverApiVersion.split("_")[1]);

    try {
      Class.forName("com.destroystokyo.paper.event.server.PaperServerListPingEvent");
      this.isPaperServer = true;
    } catch (final ClassNotFoundException e) {
      this.isPaperServer = false;
    }
    instance = this;
    if (Bukkit.getPluginManager().isPluginEnabled("Prisma")) {
      this.prisma = new PrismaHook();
    }
    if (this.isPaperServer) {
      getServer().getPluginManager().registerEvents(new PaperPingListener(this, this.miniMOTD), this);
    } else {
      getServer().getPluginManager().registerEvents(new PingListener(this, this.miniMOTD), this);
      if (this.majorMinecraftVersion > 11) {
        getLogger().info("#");
        getLogger().info("# This server is not using Paper, and therefore some features may be limited or disabled.");
        getLogger().info("# Get Paper from https://papermc.io/downloads");
        getLogger().info("#");
      }
    }
    this.audiences = BukkitAudiences.create(this);
    final PluginCommand command = getCommand("minimotd");
    if (command != null) {
      command.setExecutor(new SpigotCommand(this));
      command.setTabCompleter(new SpigotCommand(this));
    }

    final Metrics metrics = new Metrics(this, 8132);

    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      Bukkit.getScheduler().runTaskAsynchronously(this, () ->
        new UpdateChecker(this.getDescription().getVersion()).checkVersion().forEach(getLogger()::info));
    }
  }

  public @NonNull MiniMOTD miniMOTD() {
    return this.miniMOTD;
  }

}
