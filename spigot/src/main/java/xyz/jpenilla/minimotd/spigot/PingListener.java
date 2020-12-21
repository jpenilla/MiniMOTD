package xyz.jpenilla.minimotd.spigot;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

public class PingListener implements Listener {
    private final SpigotConfig cfg;
    private final MiniMOTD miniMOTD;
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().build();
    private final MiniMessage miniMessage = MiniMessage.get();

    public PingListener(MiniMOTD miniMOTD) {
        this.cfg = miniMOTD.getCfg();
        this.miniMOTD = miniMOTD;
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        final int onlinePlayers = e.getNumPlayers();
        final int actualMaxPlayers = e.getMaxPlayers();

        final int maxPlayers = cfg.getAdjustedMaxPlayers(onlinePlayers, actualMaxPlayers);
        e.setMaxPlayers(maxPlayers);

        final MiniMOTDConfig<CachedServerIcon>.MOTD motd = cfg.getMOTD(onlinePlayers, maxPlayers);
        final String motdString = motd.motd();
        if (motdString != null) {
            if (miniMOTD.getMajorMinecraftVersion() > 15) {
                e.setMotd(serializer.serialize(miniMessage.parse(motdString)));
            } else {
                e.setMotd(legacySerializer.serialize(miniMessage.parse(motdString)));
            }
        }
        final CachedServerIcon favicon = motd.icon();
        if (favicon != null) {
            e.setServerIcon(favicon);
        }
    }
}
