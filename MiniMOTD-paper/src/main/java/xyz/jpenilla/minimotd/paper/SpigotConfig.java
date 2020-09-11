package xyz.jpenilla.minimotd.paper;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.CachedServerIcon;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpigotConfig extends MiniMOTDConfig {
    private final MiniMOTD miniMOTD;
    private final List<CachedServerIcon> icons = new ArrayList<>();

    public SpigotConfig(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        miniMOTD.saveDefaultConfig();
        reload();
    }

    public CachedServerIcon getRandomIcon() {
        if (icons.isEmpty()) {
            return null;
        }
        return icons.get(ThreadLocalRandom.current().nextInt(icons.size()));
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

        final File iconFolder = new File(miniMOTD.getDataFolder() + File.separator + "icons");
        if (!iconFolder.exists()) {
            iconFolder.mkdir();
        }
        this.icons.clear();
        final File[] icons = iconFolder.listFiles(i -> i.getName().endsWith(".png"));
        if (icons != null) {
            for (File icon : icons) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(icon);
                    if (bufferedImage.getHeight() == 64 && bufferedImage.getWidth() == 64) {
                        this.icons.add(Bukkit.loadServerIcon(bufferedImage));
                    } else {
                        miniMOTD.getLogger().info("Could not load " + icon.getName() + ": image must be 64x64px");
                    }
                } catch (Exception e) {
                    miniMOTD.getLogger().info("Could not load " + icon.getName() + ": invalid image file");
                    e.printStackTrace();
                }
            }
        }
    }
}
