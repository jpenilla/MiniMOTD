package xyz.jpenilla.minimotd.spigot;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.ThreadLocalRandom;

public class PaperPingListener implements Listener {
    private final SpigotConfig cfg;
    private final MiniMOTD miniMOTD;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private final MiniMessage miniMessage = MiniMessage.get();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();

    public PaperPingListener(MiniMOTD miniMOTD) {
        this.cfg = miniMOTD.getCfg();
        this.miniMOTD = miniMOTD;
    }

    @EventHandler
    public void onPing(PaperServerListPingEvent e) {
        int onlinePlayers = e.getNumPlayers();
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
        e.setNumPlayers(onlinePlayers);

        int maxPlayers = cfg.getAdjustedMaxPlayers(onlinePlayers, e.getMaxPlayers());
        e.setMaxPlayers(maxPlayers);

        if (cfg.isMotdEnabled()) {
            final Component motd = miniMessage.parse(cfg.getMOTD(onlinePlayers, maxPlayers));
            if (e.getClient().getProtocolVersion() < 735 || miniMOTD.getMajorMinecraftVersion() < 16) {
                e.setMotd(legacySerializer.serialize(motd));
            } else {
                e.setMotd(serializer.serialize(motd));
            }
        }
    }
}
