package xyz.jpenilla.minimotd.bungee;

import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import xyz.jpenilla.minimotd.common.MiniMOTDConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BungeeConfig extends MiniMOTDConfig {
    private final MiniMOTD miniMOTD;
    private final List<Favicon> icons = new ArrayList<>();

    public BungeeConfig(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
        reload();
    }

    public Favicon getRandomIcon() {
        if (icons.isEmpty()) {
            return null;
        }
        return icons.get(ThreadLocalRandom.current().nextInt(icons.size()));
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
                        this.icons.add(Favicon.create(bufferedImage));
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
