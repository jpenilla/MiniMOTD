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
package xyz.jpenilla.minimotd.velocity;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.identity.Identity;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class VelocityCommand implements SimpleCommand {
  private final MiniMOTDPlugin plugin;

  public VelocityCommand(final @NonNull MiniMOTDPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void execute(final @NonNull Invocation invocation) {
    final String[] args = invocation.arguments();

    if (args.length == 0) {
      this.onInvalidUse(invocation);
      return;
    }

    switch (args[0]) {
      case "about":
        this.onAbout(invocation);
        return;
      case "help":
        this.onHelp(invocation);
        return;
      case "reload":
        this.onReload(invocation);
        return;
    }

    this.onInvalidUse(invocation);
  }

  private void onInvalidUse(final @NonNull Invocation invocation) {
    this.sendMessages(invocation, ImmutableList.of("<hover:show_text:'<green>Click for <yellow>/minimotdvelocity help'><click:run_command:/minimotdvelocity help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotdvelocity help</yellow> or click here"));
  }

  private void onReload(final @NonNull Invocation invocation) {
    this.plugin.miniMOTD().iconManager().loadIcons();
    this.plugin.miniMOTD().configManager().loadExtraConfigs();
    this.plugin.miniMOTD().configManager().loadConfigs();
    this.sendMessages(invocation, ImmutableList.of("<green>Done reloading."));
  }

  private void onHelp(final @NonNull Invocation invocation) {
    this.sendMessages(invocation, ImmutableList.of(
      "<gradient:blue:green>MiniMOTD Commands:",
      " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotdvelocity about'><click:run_command:/minimotd about><yellow>/minimotdvelocity about",
      " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotdvelocity reload'><click:run_command:/minimotd reload><yellow>/minimotdvelocity reload",
      " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotdvelocity help'><click:run_command:/minimotd help><yellow>/minimotdvelocity help"
    ));
  }

  private void onAbout(final @NonNull Invocation invocation) {
    final String header = "<gradient:white:black>=============</gradient><gradient:black:white>=============";
    this.sendMessages(invocation, ImmutableList.of(
      header,
      "<hover:show_text:'<rainbow>click me!'><click:open_url:" + this.plugin.getPluginDescription().getUrl().orElse("") + ">" + this.plugin.getPluginDescription().getName().orElse("") + " <gradient:red:yellow>" + this.plugin.getPluginDescription().getVersion().orElse(""),
      "<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp",
      header
    ));
  }

  private void sendMessages(final @NonNull Invocation invocation, final @NonNull List<String> messages) {
    messages.forEach(message -> invocation.source().sendMessage(Identity.nil(), this.plugin.getMiniMessage().parse(message)));
  }

  private static final List<String> COMMANDS = ImmutableList.of("about", "reload", "help");

  @Override
  public List<String> suggest(final @NonNull Invocation invocation) {
    return COMMANDS;
  }

  @Override
  public boolean hasPermission(final @NonNull Invocation invocation) {
    return invocation.source().hasPermission("minimotd.admin");
  }
}
