package xyz.jpenilla.minimotd.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {
    private final SpigotConfig cfg;

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
            e.setMotd(cfg.getMOTD(onlinePlayers, maxPlayers));
        }
    }
}
