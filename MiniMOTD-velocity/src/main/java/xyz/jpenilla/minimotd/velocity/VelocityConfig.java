package xyz.jpenilla.minimotd.velocity;

import com.google.common.reflect.TypeToken;
import com.velocitypowered.api.util.Favicon;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
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

public class VelocityConfig extends MiniMOTDConfig {
    private final MiniMOTD miniMOTD;
    private final List<Favicon> icons = new ArrayList<>();

    public VelocityConfig(MiniMOTD miniMOTD) {
        this.miniMOTD = miniMOTD;
    }

    @Override
    public void reload() {
        try {
            Files.createDirectories(miniMOTD.getDataDirectory());
        } catch (IOException e) {
            miniMOTD.getLogger().warn("unable to create config directory");
        }

        final File file = new File(miniMOTD.getDataDirectory().toFile().getPath() + File.separator + "config.yml");
        if (!file.exists()) {
            try (InputStream in = miniMOTD.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setFile(file).build();

        final ConfigurationNode node;
        try {
            node = loader.load();
        } catch (IOException e) {
            miniMOTD.getLogger().warn("unable to load config file.");
            return;
        }

        final ConfigurationNode motd = node.getNode("motd");
        final ConfigurationNode maxPlayers = node.getNode("maxPlayers");
        final ConfigurationNode bungeeOnly = node.getNode("bungeeOnly");
        final ConfigurationNode updateChecker = node.getNode(UPDATE_CHECKER);

        try {
            getMotds().clear();
            getMotds().addAll(motd.getNode("motds").getList(TypeToken.of(String.class)));
            setMotdEnabled(motd.getNode("motdEnabled").getBoolean());
            setMaxPlayersEnabled(maxPlayers.getNode("maxPlayersEnabled").getBoolean());
            setJustXMoreEnabled(maxPlayers.getNode("justXMoreEnabled").getBoolean());
            setMaxPlayers(maxPlayers.getNode("maxPlayers").getInt());
            setXValue(maxPlayers.getNode("xValue").getInt());
            setFakePlayersEnabled(bungeeOnly.getNode("fakePlayersEnabled").getBoolean());
            setFakePlayers(bungeeOnly.getNode("fakePlayers").getString());
            setUpdateChecker(updateChecker.getBoolean());
        } catch (ObjectMappingException e) {
            miniMOTD.getLogger().warn("unable to load config.");
        }

        final File iconFolder = new File(miniMOTD.getDataDirectory().toFile().getPath() + File.separator + "icons");
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

    public Favicon getRandomIcon() {
        if (icons.isEmpty()) {
            return null;
        }
        return icons.get(ThreadLocalRandom.current().nextInt(icons.size()));
    }
}
