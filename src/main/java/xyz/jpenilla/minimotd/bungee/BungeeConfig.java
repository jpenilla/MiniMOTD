package xyz.jpenilla.minimotd.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeeConfig extends MiniMOTDConfig {
    private final MiniMOTD miniMOTD;

    public BungeeConfig(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        reload();
    }

    public void reload() {
        final Configuration config = loadFromDisk();

        getMotds().clear();
        getMotds().addAll(config.getStringList(MOTDS));
        setMotdEnabled(config.getBoolean(MOTD_ENABLED));
        setMaxPlayersEnabled(config.getBoolean(MAX_PLAYERS_ENABLED));
        setJustXMoreEnabled(config.getBoolean(JUST_X_MORE_ENABLED));
        setMaxPlayers(config.getInt(MAX_PLAYERS));
        setXValue(config.getInt(X_VALUE));
        setFakePlayersEnabled(config.getBoolean(FAKE_PLAYERS_ENABLED));
        setFakePlayers(config.getString(FAKE_PLAYERS));
    }

    private Configuration loadFromDisk() {
        if (!miniMOTD.getDataFolder().exists()) {
            miniMOTD.getDataFolder().mkdir();
        }
        File file = new File(miniMOTD.getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = miniMOTD.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(miniMOTD.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
