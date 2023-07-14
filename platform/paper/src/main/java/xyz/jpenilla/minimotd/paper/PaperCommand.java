/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2023 Jason Penilla
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
package xyz.jpenilla.minimotd.paper;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.minimotd.common.CommandHandler;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class PaperCommand extends Command implements PluginIdentifiableCommand {
  private final MiniMOTDPlugin plugin;
  private final CommandHandler handler;

  PaperCommand(final MiniMOTDPlugin plugin) {
    super("minimotd", "MiniMOTD Command", "/minimotd help", Collections.emptyList());
    this.plugin = plugin;
    this.handler = new CommandHandler(plugin.miniMOTD());
  }

  @Override
  public boolean execute(final @NotNull CommandSender sender, final @NotNull String commandLabel, final @NotNull String[] args) {
    if (!sender.hasPermission("minimotd.admin")) {
      sender.sendMessage(text("No permission.", RED));
      return true;
    }

    if (args.length == 0) {
      this.onInvalidUse(sender);
      return true;
    }

    switch (args[0]) {
      case "about":
        this.handler.about(sender);
        return true;
      case "help":
        this.handler.help(sender);
        return true;
      case "reload":
        this.handler.reload(sender);
        return true;
    }

    this.onInvalidUse(sender);
    return true;
  }

  @Override
  public @NotNull Plugin getPlugin() {
    return this.plugin;
  }

  private void onInvalidUse(final @NonNull Audience audience) {
    audience.sendMessage(text("Invalid command usage. Use '/minimotd help' for a list of command provided by MiniMOTD.", RED)
      .hoverEvent(text("Click to execute '/minimotd help'"))
      .clickEvent(runCommand("/minimotd help")));
  }

  private static final List<String> COMMANDS = ImmutableList.of("about", "reload", "help");

  @Override
  public @NotNull List<String> tabComplete(final @NonNull CommandSender sender, final @NonNull String alias, final @NonNull String[] args) {
    if (args.length < 2 && sender.hasPermission("minimotd.admin")) {
      return COMMANDS;
    }
    return Collections.emptyList();
  }

}
