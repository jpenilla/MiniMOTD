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
package xyz.jpenilla.minimotd.fabric.util;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unused"})
@DefaultQualifier(NonNull.class)
public final class MutableServerStatus {
  private Component description;
  private Optional<ServerStatus.Players> players;
  private Optional<ServerStatus.Version> version;
  private Optional<ServerStatus.Favicon> favicon;
  private boolean enforcesSecureChat;

  public MutableServerStatus(final ServerStatus status) {
    this.description = status.description();
    this.players = status.players();
    this.version = status.version();
    this.favicon = status.favicon();
    this.enforcesSecureChat = status.enforcesSecureChat();
  }

  public ServerStatus toServerStatus() {
    return new ServerStatus(this.description, this.players, this.version, this.favicon, this.enforcesSecureChat);
  }

  public Component description() {
    return this.description;
  }

  public void description(final Component description) {
    Objects.requireNonNull(description, "description");
    this.description = description;
  }

  public Optional<ServerStatus.Players> players() {
    return this.players;
  }

  public void players(final Optional<ServerStatus.Players> players) {
    Objects.requireNonNull(players, "players");
    this.players = players;
  }

  public Optional<ServerStatus.Version> version() {
    return this.version;
  }

  public void version(final Optional<ServerStatus.Version> version) {
    Objects.requireNonNull(version, "version");
    this.version = version;
  }

  public Optional<ServerStatus.Favicon> favicon() {
    return this.favicon;
  }

  public void favicon(final Optional<ServerStatus.Favicon> favicon) {
    Objects.requireNonNull(favicon, "favicon");
    this.favicon = favicon;
  }

  public boolean enforcesSecureChat() {
    return this.enforcesSecureChat;
  }

  public void enforcesSecureChat(final boolean enforcesSecureChat) {
    this.enforcesSecureChat = enforcesSecureChat;
  }
}
