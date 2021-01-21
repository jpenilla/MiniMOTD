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
package xyz.jpenilla.minimotd.spigot;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;

public class SpigotCommand implements CommandExecutor, TabCompleter {
  private final MiniMOTDPlugin plugin;
  private final MiniMessage miniMessage;

  public SpigotCommand(final @NonNull MiniMOTDPlugin plugin) {
    this.plugin = plugin;
    this.miniMessage = MiniMessage.get();
  }

  @Override
  public boolean onCommand(final @NonNull CommandSender sender,
                           final @NonNull Command command,
                           final @NonNull String label,
                           final @NonNull String[] args) {
    if (!sender.hasPermission("minimotd.admin")) {
      this.onNoPermission(sender, args);
      return true;
    }

    if (args.length == 0) {
      this.onInvalidUse(sender, args);
      return true;
    }

    switch (args[0]) {
      case "about":
        this.onAbout(sender, args);
        return true;
      case "help":
        this.onHelp(sender, args);
        return true;
      case "reload":
        this.onReload(sender, args);
        return true;
    }

    this.onInvalidUse(sender, args);
    return true;
  }

  private void onHelp(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.send(sender, ImmutableList.of(
      "<gradient:blue:green>MiniMOTD Commands:",
      " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd about'><click:run_command:/minimotd about><yellow>/minimotd about",
      " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd reload'><click:run_command:/minimotd reload><yellow>/minimotd reload",
      " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><yellow>/minimotd help"
    ));
  }

  private void onReload(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.plugin.miniMOTD().iconManager().loadIcons();
    this.plugin.miniMOTD().configManager().loadConfigs();
    this.send(sender, "<green>Done reloading.");
  }

  private void onAbout(final @NonNull CommandSender sender, final @NonNull String[] args) {
    final String header = "<gradient:white:black>=============</gradient><gradient:black:white>=============";
    this.send(sender, ImmutableList.of(
      header,
      "<hover:show_text:'<rainbow>click me!'><click:open_url:" + this.plugin.getDescription().getWebsite() + ">" + this.plugin.getName() + " <gradient:red:yellow>" + this.plugin.getDescription().getVersion(),
      "<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp",
      header
    ));
  }

  private void onInvalidUse(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.send(sender, "<hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotd help</yellow> or click here");
  }

  private void onNoPermission(final @NonNull CommandSender sender, final @NonNull String[] args) {
    this.send(sender, "<gradient:red:yellow>No permission.");
  }

  private void send(final @NonNull CommandSender sender, final @NonNull String message) {
    if (sender instanceof Player) {
      this.plugin.getAudiences().player((Player) sender).sendMessage(Identity.nil(), this.miniMessage.parse(message));
    } else {
      this.plugin.getAudiences().console().sendMessage(Identity.nil(), this.miniMessage.parse(message));
    }
  }

  private void send(final @NonNull CommandSender sender, final @NonNull List<String> messages) {
    for (final String message : messages) {
      this.send(sender, message);
    }
  }

  private static final List<String> COMMANDS = ImmutableList.of("about", "reload", "help");

  @Override
  public List<String> onTabComplete(final @NonNull CommandSender sender,
                                    final @NonNull Command command,
                                    final @NonNull String alias,
                                    final @NonNull String[] args) {
    if (args.length < 2 && sender.hasPermission("minimotd.admin")) {
      return COMMANDS;
    }
    return Collections.emptyList();
  }
}
