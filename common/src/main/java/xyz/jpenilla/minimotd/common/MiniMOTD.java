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
package xyz.jpenilla.minimotd.common;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.config.ConfigManager;

import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public abstract class MiniMOTD<I> {

  private final ConfigManager configManager;
  private final Path dataDirectory;
  private final Logger logger;

  public MiniMOTD(final @NonNull Path dataDirectory, final @NonNull Logger logger) {
    this.dataDirectory = dataDirectory;
    this.logger = logger;
    this.configManager = new ConfigManager(this);
    this.configManager.loadConfigs();
  }

  public final @NonNull Path dataDirectory() {
    return this.dataDirectory;
  }

  public abstract @NonNull IconManager<I> iconManager();

  public final @NonNull Logger logger() {
    return this.logger;
  }

  public final @NonNull Pair<I, String> createMOTD(final int onlinePlayers, final int maxPlayers) {
    I icon = null;
    String motd = null;
    int index = 0;
    if (this.configManager().config().motdEnabled()) {
      index = this.configManager().config().motds().size() == 1 ? 0 : ThreadLocalRandom.current().nextInt(this.configManager().config().motds().size());
      motd = this.configManager().config().motds().get(index)
        .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
        .replace("{maxPlayers}", String.valueOf(maxPlayers))
        .replace("{br}", "\n");
    }
    if (this.configManager.config().iconEnabled()) {
      icon = this.iconManager().icon(index);
    }
    return new Pair<>(icon, motd);
  }

  public @NonNull ConfigManager configManager() {
    return this.configManager;
  }
}
