package xyz.jpenilla.minimotd.spigot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.CachedServerIcon;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

import java.awt.image.BufferedImage;
import java.io.File;

public class SpigotConfig extends MiniMOTDConfig<CachedServerIcon> {
    private final MiniMOTD miniMOTD;

    public SpigotConfig(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        miniMOTD.saveDefaultConfig();
        reload();
    }

    public void reload() {
        miniMOTD.reloadConfig();
        final FileConfiguration config = miniMOTD.getConfig();

        motds.clear();
        for (String motd : config.getStringList(MOTDS)) {
            final String temp;
            if (miniMOTD.getPrisma() != null) {
                temp = miniMOTD.getPrisma().translate(motd);
            } else {
                temp = motd;
            }
            motds.add(temp);
        }
        motdEnabled = config.getBoolean(MOTD_ENABLED) && !motds.isEmpty();
        maxPlayersEnabled = config.getBoolean(MAX_PLAYERS_ENABLED);
        justXMoreEnabled = config.getBoolean(JUST_X_MORE_ENABLED);
        maxPlayers = config.getInt(MAX_PLAYERS);
        xValue = config.getInt(X_VALUE);
        fakePlayersEnabled = config.getBoolean(FAKE_PLAYERS_ENABLED);
        fakePlayers = config.getString(FAKE_PLAYERS);
        updateChecker = config.getBoolean(UPDATE_CHECKER);
        disablePlayerListHover = config.getBoolean(DISABLE_PLAYER_LIST_HOVER);

        final File iconFolder = new File(miniMOTD.getDataFolder() + File.separator + "icons");
        this.loadIcons(iconFolder).forEach(miniMOTD.getLogger()::info);
    }

    @Override
    protected @NonNull CachedServerIcon createIcon(final @NonNull BufferedImage bufferedImage) throws Exception {
        return Bukkit.loadServerIcon(bufferedImage);
    }
}
