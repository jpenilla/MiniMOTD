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
package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.CommandHandler;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

final class BungeeCommand extends Command {
  private final MiniMOTDPlugin plugin;
  private final CommandHandler handler;

  BungeeCommand(final @NonNull MiniMOTDPlugin plugin) {
    super("minimotd");
    this.plugin = plugin;
    this.handler = new CommandHandler(plugin.miniMOTD());
  }

  @Override
  public void execute(final @NonNull CommandSender sender, final @NonNull String @NonNull [] args) {
    final Audience audience = this.plugin.audiences().sender(sender);
    if (!sender.hasPermission("minimotd.admin")) {
      audience.sendMessage(text("No permission.", RED));
      return;
    }

    if (args.length == 0) {
      this.onInvalidUse(audience);
      return;
    }

    switch (args[0]) {
      case "about":
        this.handler.about(audience);
        return;
      case "help":
        this.handler.help(audience);
        return;
      case "reload":
        this.handler.reload(audience);
        return;
    }

    this.onInvalidUse(audience);
  }

  private void onInvalidUse(final @NonNull Audience audience) {
    audience.sendMessage(text("Invalid command usage. Use '/minimotd help' for a list of command provided by MiniMOTD.", RED)
      .hoverEvent(text("Click to execute '/minimotd help'"))
      .clickEvent(runCommand("/minimotd help")));
  }
}
