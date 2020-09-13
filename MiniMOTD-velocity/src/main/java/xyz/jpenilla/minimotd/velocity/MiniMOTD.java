package xyz.jpenilla.minimotd.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.UpdateChecker;

import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

@Plugin(
        id = "minimotd-velocity",
        name = "MiniMOTD",
        version = "1.2.1",
        description = "Set the server list MOTD using MiniMessage!",
        url = "https://github.com/jmanpenilla/MiniMOTD/",
        authors = {"jmp"}
)
public class MiniMOTD {
    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    @Getter private final MiniMessage miniMessage = MiniMessage.get();
    @Getter private PluginDescription pluginDescription;
    @Getter private VelocityConfig cfg;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

    @Inject private CommandManager commandManager;

    @Getter
    @Inject
    @DataDirectory
    private Path dataDirectory;

    @Inject
    public MiniMOTD(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.server.getPluginManager().fromInstance(this).ifPresent(container -> this.pluginDescription = container.getDescription());
        this.cfg = new VelocityConfig(this);
        this.commandManager.register(this.commandManager.metaBuilder("minimotdvelocity").build(), new VelocityCommand(this));
        this.cfg.reload();

        if (cfg.isUpdateChecker()) {
            new UpdateChecker(this.getPluginDescription().getVersion().orElse("")).checkVersion().whenCompleteAsync((messages, t) -> messages.forEach(this.logger::info));
        }
    }


    @Subscribe
    public void onServerListPing(ProxyPingEvent ping) {
        final ServerPing.Builder pong = ping.getPing().asBuilder();

        int onlinePlayers = pong.getOnlinePlayers();
        if (cfg.isFakePlayersEnabled()) {
            try {
                if (cfg.getFakePlayers().contains(":")) {
                    final String[] fakePlayers = cfg.getFakePlayers().split(":");
                    final int start = Integer.parseInt(fakePlayers[0]);
                    final int end = Integer.parseInt(fakePlayers[1]);
                    onlinePlayers = onlinePlayers + ThreadLocalRandom.current().nextInt(start, end);
                } else if (cfg.getFakePlayers().contains("%")) {
                    final double factor = 1 + (Double.parseDouble(cfg.getFakePlayers().replace("%", "")) / 100);
                    onlinePlayers = (int) Math.ceil(factor * onlinePlayers);
                } else {
                    final int addedPlayers = Integer.parseInt(cfg.getFakePlayers());
                    onlinePlayers = onlinePlayers + addedPlayers;
                }
            } catch (NumberFormatException ex) {
                logger.info("fakePlayers config incorrect");
            }
        }
        pong.onlinePlayers(onlinePlayers);

        int maxPlayers = cfg.getAdjustedMaxPlayers(onlinePlayers, pong.getMaximumPlayers());
        pong.maximumPlayers(maxPlayers);

        if (cfg.isMotdEnabled()) {
            Component motd = miniMessage.parse(cfg.getMOTD(onlinePlayers, maxPlayers));
            if (pong.getVersion().getProtocol() < 735) {
                motd = legacySerializer.deserialize(legacySerializer.serialize(motd));
            }
            pong.description(motd);
        }

        final Favicon favicon = cfg.getRandomIcon();
        if (favicon != null) {
            pong.favicon(favicon);
        }

        ping.setPing(pong.build());
    }
}
