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
package xyz.jpenilla.minimotd.common.config;

import java.lang.reflect.Type;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import xyz.jpenilla.minimotd.common.util.Int2IntFunction;

public final class PlayerCountModifier implements Int2IntFunction {
  private final String input;
  private final Int2IntFunction function;

  private PlayerCountModifier(final @NonNull String input, final @NonNull Int2IntFunction function) {
    this.input = input;
    this.function = function;
  }

  @Override
  public int apply(final int actualPlayers) {
    return this.function.apply(actualPlayers);
  }

  public @NonNull String input() {
    return this.input;
  }

  public static @NonNull PlayerCountModifier parse(final @NonNull String input) {
    try {
      if (input.contains(":")) {
        return parseRandomModifier(input);
      } else if (input.contains("%")) {
        return parsePercentModifier(input);
      } else if (input.contains("=")) {
        return parseConstantModifier(input);
      } else if (input.contains("+")) {
        return parseMinimumModifier(input);
      } else {
        return parseAddModifier(input);
      }
    } catch (final NumberFormatException ex) {
      throw cannotParse(input, ex);
    }
  }

  private static @NonNull PlayerCountModifier parseRandomModifier(final @NonNull String input) {
    final String[] fakePlayers = input.split(":");
    if (fakePlayers.length != 2) {
      throw cannotParse(input, null);
    }
    final int start = Integer.parseInt(fakePlayers[0]);
    final int end = Integer.parseInt(fakePlayers[1]);
    return new PlayerCountModifier(input, actual -> actual + ThreadLocalRandom.current().nextInt(start, end));
  }

  private static @NonNull PlayerCountModifier parsePercentModifier(final @NonNull String input) {
    final double factor = 1.00D + Double.parseDouble(input.replace("%", "")) / 100.00D;
    return new PlayerCountModifier(input, actual -> (int) Math.ceil(factor * actual));
  }

  private static @NonNull PlayerCountModifier parseConstantModifier(final @NonNull String input) {
    final int value = Integer.parseInt(input.replace("=", ""));
    return new PlayerCountModifier(input, actual -> value);
  }

  private static @NonNull PlayerCountModifier parseMinimumModifier(final @NonNull String input) {
    final int minPlayers = Integer.parseInt(input.replace("+", ""));
    return new PlayerCountModifier(input, actual -> Math.max(minPlayers, actual));
  }

  private static @NonNull PlayerCountModifier parseAddModifier(final @NonNull String input) {
    final int addedPlayers = Integer.parseInt(input);
    return new PlayerCountModifier(input, actual -> actual + addedPlayers);
  }

  private static @NonNull IllegalArgumentException cannotParse(final @NonNull String input, final @Nullable Throwable cause) {
    return new IllegalArgumentException(String.format("Unable to parse a player count modifier from input string '%s'.", input), cause);
  }

  public static @NonNull Serializer serializer() {
    return Serializer.INSTANCE;
  }

  public static final class Serializer extends ScalarSerializer<PlayerCountModifier> {
    private static final Serializer INSTANCE = new Serializer();

    private Serializer() {
      super(PlayerCountModifier.class);
    }

    @Override
    public PlayerCountModifier deserialize(final @NonNull Type type, final @NonNull Object obj) throws SerializationException {
      try {
        return PlayerCountModifier.parse(obj.toString());
      } catch (final IllegalArgumentException ex) {
        throw new SerializationException(ex);
      }
    }

    @Override
    protected Object serialize(final @NonNull PlayerCountModifier item, final @NonNull Predicate<Class<?>> typeSupported) {
      return item.input();
    }
  }
}
