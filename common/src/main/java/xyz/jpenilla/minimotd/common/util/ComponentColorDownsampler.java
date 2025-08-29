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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jspecify.annotations.NonNull;

public interface ComponentColorDownsampler {
  static @NonNull ComponentColorDownsampler downsampler() {
    return ComponentColorDownsamplerImpl.INSTANCE;
  }

  @NonNull Component downsample(@NonNull Component component);

  final class ComponentColorDownsamplerImpl implements ComponentColorDownsampler {
    private static final ComponentColorDownsampler INSTANCE = new ComponentColorDownsamplerImpl();

    private ComponentColorDownsamplerImpl() {
    }

    @Override
    public @NonNull Component downsample(final @NonNull Component component) {
      return GsonComponentSerializer.gson().deserializeFromTree(
        GsonComponentSerializer.colorDownsamplingGson().serializeToTree(component)
      );
    }
  }
}
