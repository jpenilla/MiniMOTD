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

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

public final class MOTDIconPair<I> {

  private final I icon;
  private final Component motd;

  public MOTDIconPair(final @Nullable I icon, final @Nullable Component motd) {
    this.icon = icon;
    this.motd = motd;
  }

  public @Nullable I icon() {
    return this.icon;
  }

  /**
   * If {@link #icon()} is non-null, the provided {@link Consumer} will be called using
   * it's return value. If {@link #icon()} is {@code null}, do nothing.
   *
   * @param iconConsumer icon consumer
   */
  public void icon(final @NonNull Consumer<@NonNull I> iconConsumer) {
    if (this.icon != null) {
      iconConsumer.accept(this.icon);
    }
  }

  public @Nullable Component motd() {
    return this.motd;
  }

  /**
   * If {@link #motd()} is non-null, the provided {@link Consumer} will be called using
   * it's return value. If {@link #motd()} is {@code null}, do nothing.
   *
   * @param motdConsumer motd consumer
   */
  public void motd(final @NonNull Consumer<@NonNull Component> motdConsumer) {
    if (this.motd != null) {
      motdConsumer.accept(this.motd);
    }
  }

}
