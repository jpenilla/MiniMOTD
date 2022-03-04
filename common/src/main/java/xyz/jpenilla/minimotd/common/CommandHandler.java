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

import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.minimotd.common.util.Components;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.LinearComponents.linear;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

@DefaultQualifier(NonNull.class)
public final class CommandHandler {
  private final MiniMOTD<?> miniMOTD;

  public CommandHandler(final MiniMOTD<?> miniMOTD) {
    this.miniMOTD = miniMOTD;
  }

  public void about(final Audience audience) {
    final Component header = miniMessage("<gradient:white:#007FFF:white>                               ").decorate(STRIKETHROUGH);
    Stream.of(
      header,
      text()
        .hoverEvent(miniMessage("<rainbow>click me!"))
        .clickEvent(openUrl(Constants.PluginMetadata.WEBSITE))
        .content(Constants.PluginMetadata.NAME)
        .color(WHITE)
        .append(space())
        .append(miniMessage("<gradient:#0047AB:#007FFF>" + Constants.PluginMetadata.VERSION))
        .build(),
      text()
        .content("By ")
        .color(GRAY)
        .append(text("jmp", WHITE))
        .build(),
      header
    ).forEach(audience::sendMessage);
  }

  public void reload(final Audience audience) {
    try {
      this.miniMOTD.reload();
    } catch (final Exception ex) {
      this.miniMOTD.logger().warn("Failed to reload MiniMOTD. Ensure there are no errors in your config files.", ex);
      audience.sendMessage(Components.ofChildren(
        Constants.COMMAND_PREFIX,
        space(),
        text("Failed to reload MiniMOTD. Ensure there are no errors in your config files. See console for more details.", RED)
      ));
      return;
    }

    audience.sendMessage(Components.ofChildren(
      Constants.COMMAND_PREFIX,
      space(),
      text("Done reloading configuration.", GREEN)
    ));
  }

  public void help(final Audience audience) {
    Stream.of(
      linear(Constants.COMMAND_PREFIX, space(), text(Constants.PluginMetadata.NAME + " command help", WHITE)),
      commandInfo("minimotd about", "Show information about MiniMOTD"),
      commandInfo("minimotd reload", "Reload MiniMOTD configuration files"),
      commandInfo("minimotd help", "Show this help menu")
    ).forEach(audience::sendMessage);
  }

  private static Component commandInfo(final String command, final String description) {
    return text()
      .content(" - ")
      .color(GRAY)
      .append(text('/', WHITE))
      .append(text(command, color(0x007FFF)))
      .append(text(':'))
      .append(space())
      .append(text(description, WHITE))
      .hoverEvent(text()
        .content("Click to execute '")
        .color(GRAY)
        .append(text("/" + command, WHITE))
        .append(text("'"))
        .build())
      .clickEvent(runCommand("/" + command))
      .build();
  }

  private static Component miniMessage(final String message) {
    return MiniMessage.miniMessage().deserialize(message);
  }

  @FunctionalInterface
  public interface Executor {
    void execute(Audience audience);
  }
}
