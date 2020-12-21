package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

import java.util.concurrent.ThreadLocalRandom;

public class PingListener implements Listener {
    private final BungeeConfig cfg;
    private final MiniMessage miniMessage = MiniMessage.get();
    private final MiniMOTD miniMOTD;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

    public PingListener(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        this.cfg = miniMOTD.getCfg();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPing(ProxyPingEvent e) {
        final ServerPing response = e.getResponse();

        if (response != null) {
            final ServerPing.Players players = response.getPlayers();
            int onlinePlayers = players.getOnline();
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
                    miniMOTD.getLogger().warning("fakePlayers config invalid");
                }
            }
            players.setOnline(onlinePlayers);

            final int maxPlayers = cfg.getAdjustedMaxPlayers(onlinePlayers, players.getMax());
            players.setMax(maxPlayers);

            if (cfg.isDisablePlayerListHover()) {
                players.setSample(new ServerPing.PlayerInfo[]{});
            }

            final MiniMOTDConfig<Favicon>.MOTD motd = cfg.getMOTD(onlinePlayers, maxPlayers);
            final String motdString = motd.motd();
            if (motdString != null) {
                Component motdComponent = miniMessage.parse(motdString);
                if (e.getConnection().getVersion() < 735) {
                    motdComponent = legacySerializer.deserialize(legacySerializer.serialize(motdComponent));
                }
                response.setDescriptionComponent(BungeeComponentSerializer.get().serialize(motdComponent)[0]);
            }

            response.setPlayers(players);
            final Favicon favicon = motd.icon();
            if (favicon != null) {
                response.setFavicon(favicon);
            }
            e.setResponse(response);
        }
    }
}
