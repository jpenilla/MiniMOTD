package xyz.jpenilla.minimotd.velocity;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;

public class VelocityCommand implements SimpleCommand {
    private final MiniMOTD miniMOTD;

    public VelocityCommand(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
    }

    @Override
    public void execute(Invocation invocation) {
        final String[] args = invocation.arguments();

        if (args.length == 0) {
            onInvalidUse(invocation);
            return;
        }

        switch (args[0]) {
            case "about":
                onAbout(invocation);
                return;
            case "help":
                onHelp(invocation);
                return;
            case "reload":
                onReload(invocation);
                return;
        }

        onInvalidUse(invocation);
    }

    private void onInvalidUse(Invocation invocation) {
        sendMessages(invocation, ImmutableList.of("<hover:show_text:'<green>Click for <yellow>/minimotdvelocity help'><click:run_command:/minimotdvelocity help><italic><gradient:red:gold>Invalid usage.</gradient> <blue>Try <yellow>/minimotdvelocity help</yellow> or click here"));
    }

    private void onReload(Invocation invocation) {
        miniMOTD.getCfg().reload();
        sendMessages(invocation, ImmutableList.of("<green>Done reloading."));
    }

    private void onHelp(Invocation invocation) {
        sendMessages(invocation, ImmutableList.of(
                "<gradient:blue:green>MiniMOTD Commands:",
                " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotdvelocity about'><click:run_command:/minimotd about><yellow>/minimotdvelocity about",
                " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotdvelocity reload'><click:run_command:/minimotd reload><yellow>/minimotdvelocity reload",
                " <gray>-</gray> <hover:show_text:'<green>Click for <yellow>/minimotdvelocity help'><click:run_command:/minimotd help><yellow>/minimotdvelocity help"
        ));
    }

    private void onAbout(Invocation invocation) {
        final String header = "<gradient:white:black>=============</gradient><gradient:black:white>=============";
        sendMessages(invocation, ImmutableList.of(
                header,
                "<hover:show_text:'<rainbow>click me!'><click:open_url:" + miniMOTD.getPluginDescription().getUrl().orElse("") + ">" + miniMOTD.getPluginDescription().getName().orElse("") + " <gradient:red:yellow>" + miniMOTD.getPluginDescription().getVersion().orElse(""),
                "<yellow>By</yellow><gray>:</gray> <gradient:blue:green>jmp",
                header
        ));
    }

    private void sendMessages(Invocation invocation, List<String> messages) {
        messages.forEach(message -> invocation.source().sendMessage(miniMOTD.getMiniMessage().parse(message)));
    }

    private static final List<String> COMMANDS = ImmutableList.of("about", "reload", "help");

    @Override
    public List<String> suggest(Invocation invocation) {
        return COMMANDS;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("minimotd.admin");
    }
}
