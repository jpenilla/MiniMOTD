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

import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import xyz.jpenilla.minimotd.common.UpdateChecker;

public class MiniMOTDPlugin extends Plugin {
  @Getter private BungeeAudiences audiences;
  private MiniMOTD miniMOTD;

  public @NonNull MiniMOTD miniMOTD() {
    return this.miniMOTD;
  }

  @Override
  public void onEnable() {
    this.miniMOTD = new MiniMOTD(this);
    this.audiences = BungeeAudiences.create(this);
    this.getProxy().getPluginManager().registerListener(this, new PingListener(this.miniMOTD));
    this.getProxy().getPluginManager().registerCommand(this, new BungeeCommand(this));
    final Metrics metrics = new Metrics(this, 8137);

    if (this.miniMOTD.configManager().config().updateChecker()) {
      this.getProxy().getScheduler().runAsync(this, () ->
        new UpdateChecker(this.getDescription().getVersion()).checkVersion().forEach(getLogger()::info));
    }
  }
}
