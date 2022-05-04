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
package xyz.jpenilla.minimotd.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class IconManager<I> {
  private final Map<String, I> icons = new ConcurrentHashMap<>();
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
      try (final Stream<Path> stream = Files.list(this.iconsDirectory)) {
        stream.filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(".png"))
          .forEach(this::loadIcon);
      }
    } catch (final IOException ex) {
      throw new RuntimeException("Exception loading server icons", ex);
    }
  }

  private void loadIcon(final @NonNull Path iconFile) {
    try (final InputStream inputStream = Files.newInputStream(iconFile)) {
      final BufferedImage bufferedImage = ImageIO.read(inputStream);
      if (bufferedImage.getHeight() == 64 && bufferedImage.getWidth() == 64) {
        final I newIcon = this.miniMOTD.platform().loadIcon(bufferedImage);
        this.icons.put(iconFile.getFileName().toString().split("\\.")[0], newIcon);
      } else {
        this.miniMOTD.logger().warn("Could not load {}: image must be 64x64px", iconFile.getFileName());
      }
    } catch (final Exception ex) {
      this.miniMOTD.logger().warn("Could not load {}: invalid image file", iconFile.getFileName(), ex);
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
