package xyz.jpenilla.minimotd.bungee;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.ThreadLocalRandom;

public class PingListener implements Listener {
    private final BungeeConfig cfg;

    public PingListener(MiniMOTD miniMOTD) {
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
                    System.out.println("[MiniMOTD] fakePlayers config incorrect");
                }
            }
            players.setOnline(onlinePlayers);

            int maxPlayers = cfg.getAdjustedMaxPlayers(onlinePlayers, players.getMax());
            players.setMax(maxPlayers);

            if (cfg.isMotdEnabled()) {
                if (e.getConnection().getVersion() >= 735) {
                    Component motd = MiniMessage.get().parse(cfg.getMOTD(onlinePlayers, maxPlayers));
                    BaseComponent component = ComponentSerializer.parse(GsonComponentSerializer.builder().build().serialize(motd))[0];
                    response.setDescriptionComponent(component);
                }

            }

            response.setPlayers(players);
            e.setResponse(response);
        }
    }
}
