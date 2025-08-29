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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Function;
import java.util.function.Predicate;
import net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.CommandHandler;

public final class BrigadierUtil {
  private BrigadierUtil() {
  }

  public static <S> LiteralArgumentBuilder<S> buildTree(
    final CommandHandler handler,
    final Function<S, Audience> audienceExtractor,
    final Predicate<S> permissionChecker
  ) {
    final class WrappingExecutor implements Command<S> {
      private final CommandHandler.Executor handler;

      WrappingExecutor(final CommandHandler.@NonNull Executor handler) {
        this.handler = handler;
      }

      @Override
      public int run(final @NonNull CommandContext<S> context) {
        this.handler.execute(audienceExtractor.apply(context.getSource()));
        return Command.SINGLE_SUCCESS;
      }
    }

    return LiteralArgumentBuilder.<S>literal("minimotd")
      .requires(permissionChecker)
      .executes(new WrappingExecutor(handler::help))
      .then(LiteralArgumentBuilder.<S>literal("help").executes(new WrappingExecutor(handler::help)))
      .then(LiteralArgumentBuilder.<S>literal("about").executes(new WrappingExecutor(handler::about)))
      .then(LiteralArgumentBuilder.<S>literal("reload").executes(new WrappingExecutor(handler::reload)));
  }
}
