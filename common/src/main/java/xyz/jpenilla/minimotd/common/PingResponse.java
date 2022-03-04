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

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import static java.util.Objects.requireNonNull;

@DefaultQualifier(NonNull.class)
public final class PingResponse<I> {
  private final @Nullable I icon;
  private final @Nullable Component motd;
  private final PlayerCount playerCount;
  private final boolean hidePlayerCount;
  private final boolean disablePlayerListHover;

  private PingResponse(
    final @Nullable I icon,
    final @Nullable Component motd,
    final PlayerCount playerCount,
    final boolean hidePlayerCount,
    final boolean disablePlayerListHover
  ) {
    this.icon = icon;
    this.motd = motd;
    this.playerCount = playerCount;
    this.hidePlayerCount = hidePlayerCount;
    this.disablePlayerListHover = disablePlayerListHover;
  }

  public @Nullable I icon() {
    return this.icon;
  }

  /**
   * If {@link #icon()} is non-null, the provided {@link Consumer} will be called using
   * its return value. If {@link #icon()} is {@code null}, do nothing.
   *
   * @param iconConsumer icon consumer
   */
  public void icon(final Consumer<I> iconConsumer) {
    if (this.icon != null) {
      iconConsumer.accept(this.icon);
    }
  }

  public @Nullable Component motd() {
    return this.motd;
  }

  /**
   * If {@link #motd()} is non-null, the provided {@link Consumer} will be called using
   * its return value. If {@link #motd()} is {@code null}, do nothing.
   *
   * @param motdConsumer motd consumer
   */
  public void motd(final Consumer<Component> motdConsumer) {
    if (this.motd != null) {
      motdConsumer.accept(this.motd);
    }
  }

  public PlayerCount playerCount() {
    return this.playerCount;
  }

  public boolean hidePlayerCount() {
    return this.hidePlayerCount;
  }

  public boolean disablePlayerListHover() {
    return this.disablePlayerListHover;
  }

  public Builder<I> toBuilder() {
    return new BuilderImpl<>(
      this.motd,
      this.icon,
      this.playerCount,
      this.hidePlayerCount,
      this.disablePlayerListHover
    );
  }

  public static <I> Builder<I> builder() {
    return new BuilderImpl<>();
  }

  public static final class PlayerCount {
    private final int onlinePlayers;
    private final int maxPlayers;

    private PlayerCount(final int onlinePlayers, final int maxPlayers) {
      this.onlinePlayers = onlinePlayers;
      this.maxPlayers = maxPlayers;
    }

    public int onlinePlayers() {
      return this.onlinePlayers;
    }

    public int maxPlayers() {
      return this.maxPlayers;
    }

    public void applyCount(final IntConsumer onlinePlayersSetter, final IntConsumer maxPlayersSetter) {
      onlinePlayersSetter.accept(this.onlinePlayers);
      maxPlayersSetter.accept(this.maxPlayers);
    }

    public static PlayerCount playerCount(final int onlinePlayers, final int maxPlayers) {
      return new PlayerCount(onlinePlayers, maxPlayers);
    }
  }

  public interface Builder<I> {
    Builder<I> motd(@Nullable Component motd);

    Builder<I> icon(@Nullable I icon);

    Builder<I> playerCount(PlayerCount playerCount);

    Builder<I> hidePlayerCount(boolean hidePlayerCount);

    Builder<I> disablePlayerListHover(boolean disablePlayerListHover);

    PingResponse<I> build();
  }

  private static final class BuilderImpl<I> implements Builder<I> {
    private @Nullable Component motd;
    private @Nullable I icon;
    private @Nullable PlayerCount playerCount;
    private boolean hidePlayerCount = false;
    private boolean disablePlayerListHover = false;

    private BuilderImpl() {
    }

    private BuilderImpl(
      final @Nullable Component motd,
      final @Nullable I icon,
      final @Nullable PlayerCount playerCount,
      final boolean hidePlayerCount,
      final boolean disablePlayerListHover
    ) {
      this.motd = motd;
      this.icon = icon;
      this.playerCount = playerCount;
      this.hidePlayerCount = hidePlayerCount;
      this.disablePlayerListHover = disablePlayerListHover;
    }

    @Override
    public Builder<I> motd(final @Nullable Component motd) {
      this.motd = motd;
      return this;
    }

    @Override
    public Builder<I> icon(final @Nullable I icon) {
      this.icon = icon;
      return this;
    }

    @Override
    public Builder<I> playerCount(final PlayerCount playerCount) {
      this.playerCount = playerCount;
      return this;
    }

    @Override
    public Builder<I> hidePlayerCount(final boolean hidePlayerCount) {
      this.hidePlayerCount = hidePlayerCount;
      return this;
    }

    @Override
    public Builder<I> disablePlayerListHover(final boolean disablePlayerListHover) {
      this.disablePlayerListHover = disablePlayerListHover;
      return this;
    }

    @Override
    public PingResponse<I> build() {
      requireNonNull(this.playerCount, "playerCount");
      return new PingResponse<>(
        this.icon,
        this.motd,
        this.playerCount,
        this.hidePlayerCount,
        this.disablePlayerListHover
      );
    }
  }
}
