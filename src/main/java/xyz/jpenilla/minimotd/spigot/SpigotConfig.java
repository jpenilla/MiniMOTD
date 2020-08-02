package xyz.jpenilla.minimotd.spigot;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

public class SpigotConfig extends MiniMOTDConfig {
    private final MiniMOTD miniMOTD;

    public SpigotConfig(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        miniMOTD.saveDefaultConfig();
        reload();
    }

    public void reload() {
        miniMOTD.reloadConfig();
        final FileConfiguration config = miniMOTD.getConfig();

        getMotds().clear();
        for (String motd : config.getStringList(MOTDS)) {
            final String temp;
            if (miniMOTD.getPrisma() != null) {
                temp = miniMOTD.getPrisma().translate(motd);
            } else {
                temp = motd;
            }
            getMotds().add(temp);
        }
        setMotdEnabled(config.getBoolean(MOTD_ENABLED));
        setMaxPlayersEnabled(config.getBoolean(MAX_PLAYERS_ENABLED));
        setJustXMoreEnabled(config.getBoolean(JUST_X_MORE_ENABLED));
        setMaxPlayers(config.getInt(MAX_PLAYERS));
        setXValue(config.getInt(X_VALUE));
        setFakePlayersEnabled(config.getBoolean(FAKE_PLAYERS_ENABLED));
        setFakePlayers(config.getString(FAKE_PLAYERS));
    }
}
