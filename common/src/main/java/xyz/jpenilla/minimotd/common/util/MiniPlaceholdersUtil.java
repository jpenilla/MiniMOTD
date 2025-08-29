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
package xyz.jpenilla.minimotd.common.util;

import io.github.miniplaceholders.api.MiniPlaceholders;
import java.util.Objects;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MiniPlaceholdersUtil {
  private static final String KYORI_PREFIX = new String(new char[]{'n', 'e', 't', '.', 'k', 'y', 'o', 'r', 'i'});
  private static byte miniPlaceholdersLoaded = -1;

  private MiniPlaceholdersUtil() {
  }

  private static boolean miniPlaceholdersLoaded() {
    if (miniPlaceholdersLoaded == -1) {
      if (!TagResolver.class.getName().startsWith(KYORI_PREFIX)) {
        miniPlaceholdersLoaded = 0;
        return false;
      }

      try {
        final String name = MiniPlaceholders.class.getName();
        Objects.requireNonNull(name);
        miniPlaceholdersLoaded = 1;
      } catch (final NoClassDefFoundError error) {
        miniPlaceholdersLoaded = 0;
      }
    }

    return miniPlaceholdersLoaded == 1;
  }

  public static TagResolver tagResolver() {
    if (!miniPlaceholdersLoaded()) {
      return TagResolver.empty();
    }
    return tagResolver_();
  }

  private static TagResolver tagResolver_() {
    return MiniPlaceholders.globalPlaceholders();
  }
}
