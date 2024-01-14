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
package xyz.jpenilla.minimotd.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.nearestTo;
import static net.kyori.adventure.text.format.TextColor.color;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentColorDownsamplerTest {
  private static final TextColor GREEN = color(0x04E762);
  private static final TextColor ORANGE_YELLOW = color(0xF5B700);
  private static final TextColor PINK = color(0xDC0073);
  private static final TextColor BLUE = color(0x008BF8);
  private static final TextColor LIME_GREEN = color(0x89FC00);

  @Test
  void testColorDownsampling() {
    final Component fullColor = Component.textOfChildren(
      text("Green ", GREEN),
      text("Orange Yellow ", ORANGE_YELLOW),
      text("Pink ", PINK),
      text("Blue ", BLUE),
      text("Lime Green", LIME_GREEN)
    );
    final Component expectedDownsample = Component.textOfChildren(
      text("Green ", nearestTo(GREEN)),
      text("Orange Yellow ", nearestTo(ORANGE_YELLOW)),
      text("Pink ", nearestTo(PINK)),
      text("Blue ", nearestTo(BLUE)),
      text("Lime Green", nearestTo(LIME_GREEN))
    );
    final Component actualDownsample = ComponentColorDownsampler.downsampler().downsample(fullColor);

    assertEquals(expectedDownsample, actualDownsample);
  }
}
