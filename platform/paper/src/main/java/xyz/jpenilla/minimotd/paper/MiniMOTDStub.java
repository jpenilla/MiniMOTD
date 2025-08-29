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
package xyz.jpenilla.minimotd.paper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jpenilla.minimotd.common.util.Nag;

public final class MiniMOTDStub extends JavaPlugin {
  @Override
  public void onEnable() {
    if (lavaChicken() && !Bukkit.getBukkitVersion().contains("1.21.7-")) {
      // 1.21.8+, non-Paper
      new Nag.JavaUtilLogging(this.getLogger()).lines(
        "MiniMOTD does not support Spigot on Minecraft 1.21.8 or newer.",
        "To continue using MiniMOTD on the latest Minecraft versions,",
        "switch to Paper (https://papermc.io/software/paper)."
      ).error(true).logBanner();
      this.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    // Pre-Paper-plugins or non-Paper
    new Nag.JavaUtilLogging(this.getLogger()).lines(
      "You are using the incorrect MiniMOTD build for your server software.",
      "This build is for Paper 1.21.8+.",
      "Builds for Spigot/Paper 1.8.8 through 1.21.7 are available on Modrinth (https://modrinth.com/plugin/minimotd).",
      "When downloading be sure to check the supported versions/platforms",
      "field on the versions page, and/or make the correct selections in",
      "the download menu."
    ).error(true).logBanner();
    this.getServer().getPluginManager().disablePlugin(this);

    // Sadly I don't think there is any way to display a custom message for servers that support
    // Paper plugins but are < 1.21.8 (without lying in the plugin yaml)...
    // Hopefully users will realize they need to try a different build.
  }

  // Crude 1.21.7+ check
  private static boolean lavaChicken() {
    try {
      final Material mat = Material.MUSIC_DISC_LAVA_CHICKEN;
      new ItemStack(mat);
      return true;
    } catch (final Throwable thr) {
      return false;
    }
  }
}
