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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class IconManager<I> {

  private final Map<String, I> icons = new HashMap<>();
  private final MiniMOTD<I> miniMOTD;
  private final Path iconsDirectory;

  public IconManager(final @NonNull MiniMOTD<I> miniMOTD) {
    this.miniMOTD = miniMOTD;
    this.iconsDirectory = miniMOTD.dataDirectory().resolve("icons");
    this.loadIcons();
  }

  public void loadIcons() {
    this.icons.clear();
    try {
      if (!Files.exists(this.iconsDirectory)) {
        Files.createDirectories(this.iconsDirectory);
      }
      Files.list(this.iconsDirectory)
        .map(Path::toFile)
        .filter(File::isFile)
        .filter(file -> file.getName().endsWith(".png"))
        .forEach(this::loadIcon);
    } catch (final IOException ex) {
      throw new RuntimeException("Exception loading server icons", ex);
    }
  }

  private void loadIcon(final @NonNull File iconFile) {
    try {
      final BufferedImage bufferedImage = ImageIO.read(iconFile);
      if (bufferedImage.getHeight() == 64 && bufferedImage.getWidth() == 64) {
        final I newIcon = this.miniMOTD.platform().loadIcon(bufferedImage);
        this.icons.put(iconFile.getName().split("\\.")[0], newIcon);
      } else {
        this.miniMOTD.logger().warn("Could not load " + iconFile.getName() + ": image must be 64x64px");
      }
    } catch (final Exception ex) {
      this.miniMOTD.logger().warn("Could not load " + iconFile.getName() + ": invalid image file", ex);
    }
  }

  public @Nullable I icon(final @Nullable String iconString) {
    if (this.icons.isEmpty()) {
      return null;
    }

    if (iconString == null || "random".equals(iconString)) {
      final int randomIndex = ThreadLocalRandom.current().nextInt(this.icons.size());
      final Iterator<I> iterator = this.icons.values().iterator();
      for (int i = 0; i < randomIndex; i++) {
        iterator.next();
      }
      return iterator.next();
    }

    return this.icons.get(iconString);
  }

}
