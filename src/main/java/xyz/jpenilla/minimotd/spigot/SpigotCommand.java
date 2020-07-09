package xyz.jpenilla.minimotd.spigot;

import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpigotCommand implements CommandExecutor {
    private final MiniMOTD miniMOTD;
    private final BukkitAudiences audience;
    private final MiniMessage miniMessage;

    public SpigotCommand(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        audience = BukkitAudiences.create(miniMOTD);
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
        final ArrayList<String> messages = new ArrayList<>();
        messages.add("<gradient:blue:green>MiniMOTD Commands:");
        messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd about'><click:run_command:/minimotd about><yellow>/minimotd about");
        messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd reload'><click:run_command:/minimotd reload><yellow>/minimotd reload");
        messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><yellow>/minimotd help");
        send(sender, messages);
    }

    private void onReload(CommandSender sender, String[] args) {
        miniMOTD.getCfg().reload();
        send(sender, "<green>Done reloading.");
    }

    private void onAbout(CommandSender sender, String[] args) {
        final ArrayList<String> messages = new ArrayList<>();
        final String header = "<gradient:white:black>=============</gradient><gradient:black:white>=============";
        messages.add(header);
        messages.add("<hover:show_text:'<rainbow>click me!'><click:open_url:" + miniMOTD.getDescription().getWebsite() + ">" + miniMOTD.getName() + " <gradient:red:yellow>" + miniMOTD.getDescription().getVersion());
        messages.add("<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp");
        messages.add(header);
        send(sender, messages);
    }

    private void onInvalidUse(CommandSender sender, String[] args) {
        send(sender, "<hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotd help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotd help</yellow> or click here");
    }

    private void onNoPermission(CommandSender sender, String[] args) {
        send(sender, "<gradient:red:yellow>No permission.");
    }

    private void send(@NonNull CommandSender sender, @NonNull String message) {
        if (sender instanceof Player) {
            audience.player((Player) sender).sendMessage(miniMessage.parse(message));
        } else {
            audience.console().sendMessage(miniMessage.parse(miniMessage.stripTokens(message)));
        }
    }

    private void send(@NonNull CommandSender sender, @NonNull List<String> messages) {
        for (String message : messages) {
            send(sender, message);
        }
    }
}
