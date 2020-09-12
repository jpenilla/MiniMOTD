package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class BungeeCommand extends Command {
    private final MiniMOTD miniMOTD;
    private final MiniMessage miniMessage;

    public BungeeCommand(MiniMOTD miniMOTD) {
        super("minimotdbungee");
        this.miniMOTD = miniMOTD;
        this.miniMessage = MiniMessage.get();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("minimotd.admin")) {
            onNoPermission(sender, args);
            return;
        }

        if (args.length == 0) {
            onInvalidUse(sender, args);
            return;
        }

        switch (args[0]) {
            case "about":
                onAbout(sender, args);
                return;
            case "help":
                onHelp(sender, args);
                return;
            case "reload":
                onReload(sender, args);
                return;
        }

        onInvalidUse(sender, args);
    }

    private void onHelp(CommandSender sender, String[] args) {
        final List<String> messages = new ArrayList<>();
        messages.add("<gradient:blue:green>MiniMOTD Commands:");
        messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd about'><click:run_command:/minimotdbungee about><yellow>/minimotdbungee about");
        messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd reload'><click:run_command:/minimotdbungee reload><yellow>/minimotdbungee reload");
        messages.add(" <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotd help'><click:run_command:/minimotdbungee help><yellow>/minimotdbungee help");
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
        messages.add("<hover:show_text:'<rainbow>click me!'><click:open_url:https://github.com/jmanpenilla/MiniMOTD/>" + miniMOTD.getDescription().getName() + " <gradient:red:yellow>" + miniMOTD.getDescription().getVersion());
        messages.add("<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp");
        messages.add(header);
        send(sender, messages);
    }

    private void onInvalidUse(CommandSender sender, String[] args) {
        send(sender, "<hover:show_text:'<green>Click for <yellow>/minimotdbungee help'><click:run_command:/minimotdbungee help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotdbungee help</yellow> or click here");
    }

    private void onNoPermission(CommandSender sender, String[] args) {
        send(sender, "<gradient:red:yellow>No permission.");
    }

    private void send(CommandSender sender, String message) {
        final String finalMessage;
        if (sender instanceof ProxiedPlayer) {
            finalMessage = message;
        } else {
            finalMessage = miniMessage.stripTokens(message);
        }
        sender.sendMessage(BungeeCordComponentSerializer.get().serialize(miniMessage.parse(finalMessage)));
    }

    private void send(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            send(sender, message);
        }
    }
}
