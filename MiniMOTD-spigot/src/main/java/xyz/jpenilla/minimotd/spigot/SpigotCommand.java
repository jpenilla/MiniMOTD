package xyz.jpenilla.minimotd.spigot;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpigotCommand implements CommandExecutor, TabCompleter {
    private final MiniMOTD miniMOTD;
    private final MiniMessage miniMessage;

    public SpigotCommand(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        miniMessage = MiniMessage.get();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("minimotd.admin")) {
            onNoPermission(sender, args);
            return true;
        }

        if (args.length == 0) {
            onInvalidUse(sender, args);
            return true;
        }

        switch (args[0]) {
            case "about":
                onAbout(sender, args);
                return true;
            case "help":
                onHelp(sender, args);
                return true;
            case "reload":
                onReload(sender, args);
                return true;
        }

        onInvalidUse(sender, args);
        return true;
    }

    private void onHelp(CommandSender sender, String[] args) {
        send(sender, ImmutableList.of(
                "<gradient:blue:green>MiniMOTD Commands:",
                " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd about'><click:run_command:/minimotd about><yellow>/minimotd about",
                " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd reload'><click:run_command:/minimotd reload><yellow>/minimotd reload",
                " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><yellow>/minimotd help"
        ));
    }

    private void onReload(CommandSender sender, String[] args) {
        miniMOTD.getCfg().reload();
        send(sender, "<green>Done reloading.");
    }

    private void onAbout(CommandSender sender, String[] args) {
        final String header = "<gradient:white:black>=============</gradient><gradient:black:white>=============";
        send(sender, ImmutableList.of(
                header,
                "<hover:show_text:'<rainbow>click me!'><click:open_url:" + miniMOTD.getDescription().getWebsite() + ">" + miniMOTD.getName() + " <gradient:red:yellow>" + miniMOTD.getDescription().getVersion(),
                "<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp",
                header
        ));
    }

    private void onInvalidUse(CommandSender sender, String[] args) {
        send(sender, "<hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotd help</yellow> or click here");
    }

    private void onNoPermission(CommandSender sender, String[] args) {
        send(sender, "<gradient:red:yellow>No permission.");
    }

    private void send(@NonNull CommandSender sender, @NonNull String message) {
        if (sender instanceof Player) {
            miniMOTD.getAudiences().player((Player) sender).sendMessage(miniMessage.parse(message));
        } else {
            miniMOTD.getAudiences().console().sendMessage(miniMessage.parse(message));
        }
    }

    private void send(@NonNull CommandSender sender, @NonNull List<String> messages) {
        for (String message : messages) {
            send(sender, message);
        }
    }

    private static final List<String> COMMANDS = ImmutableList.of("about", "reload", "help");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 2 && sender.hasPermission("minimotd.admin")) {
            return COMMANDS;
        }
        return Collections.emptyList();
    }
}
