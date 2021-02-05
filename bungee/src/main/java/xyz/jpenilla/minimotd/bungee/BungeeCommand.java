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
package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BungeeCommand extends Command {
  private final MiniMOTDPlugin plugin;
  private final MiniMessage miniMessage;

  public BungeeCommand(final @NonNull MiniMOTDPlugin plugin) {
    super("minimotd");
    this.plugin = plugin;
    this.miniMessage = MiniMessage.get();
  }

  @Override
  public void execute(final @NonNull CommandSender sender, final @NonNull String @NonNull [] args) {
    if (!sender.hasPermission("minimotd.admin")) {
      this.onNoPermission(sender, args);
      return;
    }

    if (args.length == 0) {
      this.onInvalidUse(sender, args);
      return;
    }

    switch (args[0]) {
      case "about":
        this.onAbout(sender, args);
        return;
      case "help":
        this.onHelp(sender, args);
        return;
      case "reload":
        this.onReload(sender, args);
        return;
    }

    this.onInvalidUse(sender, args);
  }

  private void onHelp(final @NonNull CommandSender sender, final @NonNull String[] args) {
    final List<String> messages = new ArrayList<>();
    messages.add("<gradient:blue:green>MiniMOTD Commands:");
    messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd about'><click:run_command:/minimotd about><yellow>/minimotd about");
    messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd reload'><click:run_command:/minimotd reload><yellow>/minimotd reload");
    messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><yellow>/minimotd help");
    this.send(sender, messages);
  }

  private void onReload(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.plugin.miniMOTD().configManager().loadConfigs();
    this.plugin.miniMOTD().configManager().loadExtraConfigs();
    this.plugin.miniMOTD().iconManager().loadIcons();
    this.send(sender, "<green>Done reloading.");
  }

  private void onAbout(final @NonNull CommandSender sender, final @NonNull String[] args) {
    final ArrayList<String> messages = new ArrayList<>();
    final String header = "<gradient:white:black>=============</gradient><gradient:black:white>=============";
    messages.add(header);
    messages.add("<hover:show_text:'<rainbow>click me!'><click:open_url:https://github.com/jmanpenilla/MiniMOTD/>" + this.plugin.getDescription().getName() + " <gradient:red:yellow>" + this.plugin.getDescription().getVersion());
    messages.add("<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp");
    messages.add(header);
    this.send(sender, messages);
  }

  private void onInvalidUse(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.send(sender, "<hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotd help</yellow> or click here");
  }

  private void onNoPermission(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.send(sender, "<gradient:red:yellow>No permission.");
  }

  private void send(final @NonNull CommandSender sender, final @NonNull String message) {
    this.plugin.audiences().sender(sender).sendMessage(Identity.nil(), this.miniMessage.parse(message));
  }

  private void send(final @NonNull CommandSender sender, final @NonNull List<String> messages) {
    for (final String message : messages) {
      this.send(sender, message);
    }
  }
}
