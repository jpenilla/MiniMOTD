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
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class IconManager<I> {

  private final Map<String, I> icons = new HashMap<>();
  private final IconLoader<I> loader;
  private final MiniMOTD<I> miniMOTD;
  private final File iconsDirectory;

  public IconManager(
    final @NonNull MiniMOTD<I> miniMOTD,
    final @NonNull IconLoader<I> loader
  ) {
    this.miniMOTD = miniMOTD;
    this.iconsDirectory = miniMOTD.dataDirectory().resolve("icons").toFile();
    this.loader = loader;
    this.loadIcons();
  }

  public void loadIcons() {
    if (!this.iconsDirectory.exists()) {
      this.iconsDirectory.mkdir();
    }
    this.icons.clear();
    final File[] icons = this.iconsDirectory.listFiles(i -> i.getName().endsWith(".png"));
    if (icons != null) {
      for (final File icon : icons) {
        try {
          final BufferedImage bufferedImage = ImageIO.read(icon);
          if (bufferedImage.getHeight() == 64 && bufferedImage.getWidth() == 64) {
            final I newIcon = this.loader.loadIcon(bufferedImage);
            this.icons.put(icon.getName().split("\\.")[0], newIcon);
          } else {
            this.miniMOTD.logger().warn("Could not load " + icon.getName() + ": image must be 64x64px");
          }
        } catch (final Exception e) {
          this.miniMOTD.logger().warn("Could not load " + icon.getName() + ": invalid image file", e);
        }
      }
    }
  }

  @FunctionalInterface
  public interface IconLoader<I> {
    @NonNull I loadIcon(@NonNull BufferedImage bufferedImage) throws Exception;
  }

  public @Nullable I icon(final int index) {
    if (this.icons.isEmpty()) {
      return null;
    }
    if (this.icons.containsKey(String.valueOf(index))) {
      return this.icons.get(String.valueOf(index));
    }
    final int randomIndex = ThreadLocalRandom.current().nextInt(this.icons.size());
    final Iterator<I> iterator = this.icons.values().iterator();
    for (int i = 0; i < randomIndex; i++) {
      iterator.next();
    }
    return iterator.next();
  }

}
