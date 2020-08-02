package xyz.jpenilla.minimotd.spigot;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {
    private final SpigotConfig cfg;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private final MiniMessage miniMessage = MiniMessage.get();

    public PingListener(MiniMOTD miniMOTD) {
        this.cfg = miniMOTD.getCfg();
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        final int onlinePlayers = e.getNumPlayers();
        final int actualMaxPlayers = e.getMaxPlayers();

        final int maxPlayers = cfg.getAdjustedMaxPlayers(onlinePlayers, actualMaxPlayers);
        e.setMaxPlayers(maxPlayers);

        if (cfg.isMotdEnabled()) {
            e.setMotd(serializer.serialize(miniMessage.parse(cfg.getMOTD(onlinePlayers, maxPlayers))));
        }
    }
}
