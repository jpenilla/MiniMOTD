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
package xyz.jpenilla.minimotd.fabric.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.function.Function;
import net.minecraft.network.protocol.status.ServerStatus;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.jpenilla.minimotd.fabric.access.ServerStatusFaviconAccess;

@Mixin(ServerStatus.Favicon.class)
abstract class ServerStatusFaviconMixin implements ServerStatusFaviconAccess {
  @Unique
  private static Function<ServerStatus.Favicon, String> VANILLA_ENCODE_TO_STRING;

  @Redirect(
    method = "<clinit>",
    at = @At(
      target = "Lcom/mojang/serialization/codecs/PrimitiveCodec;comapFlatMap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;",
      value = "INVOKE"
    ),
    remap = false
  )
  private static Codec<ServerStatus.Favicon> makeCodec(
    final PrimitiveCodec<String> stringCodec,
    final Function<String, DataResult<ServerStatus.Favicon>> decode,
    final Function<ServerStatus.Favicon, String> encode
  ) {
    VANILLA_ENCODE_TO_STRING = encode;
    return stringCodec.comapFlatMap(decode, favicon -> {
      final @Nullable String encodedIcon = ((ServerStatusFaviconAccess) (Object) favicon).cachedEncodedIcon();
      if (encodedIcon != null) {
        return encodedIcon;
      }
      return encode.apply(favicon);
    });
  }

  @Unique
  private String encodedIcon;

  @Override
  public @Nullable String cachedEncodedIcon() {
    return this.encodedIcon;
  }

  @Override
  public void cacheEncodedIcon() {
    this.encodedIcon = VANILLA_ENCODE_TO_STRING.apply((ServerStatus.Favicon) (Object) this);
  }
}
