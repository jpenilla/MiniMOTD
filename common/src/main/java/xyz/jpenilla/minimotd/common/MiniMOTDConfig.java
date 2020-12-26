package xyz.jpenilla.minimotd.common;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@FieldNameConstants
public abstract class MiniMOTDConfig<I> {
    public static final String MOTDS = "motd." + Fields.motds;
    public static final String MOTD_ENABLED = "motd." + Fields.motdEnabled;
    public static final String MAX_PLAYERS_ENABLED = Fields.maxPlayers + "." + Fields.maxPlayersEnabled;
    public static final String JUST_X_MORE_ENABLED = Fields.maxPlayers + "." + Fields.justXMoreEnabled;
    public static final String MAX_PLAYERS = Fields.maxPlayers + "." + Fields.maxPlayers;
    public static final String X_VALUE = Fields.maxPlayers + "." + Fields.xValue;
    public static final String FAKE_PLAYERS_ENABLED = "bungeeOnly." + Fields.fakePlayersEnabled;
    public static final String FAKE_PLAYERS = "bungeeOnly." + Fields.fakePlayers;
    public static final String UPDATE_CHECKER = Fields.updateChecker;
    public static final String DISABLE_PLAYER_LIST_HOVER = "bungeeOnly." + Fields.disablePlayerListHover;
    protected final List<String> motds = new ArrayList<>();
    protected final Map<String, I> icons = new HashMap<>();
    protected boolean motdEnabled;
    @Getter protected boolean maxPlayersEnabled;
    @Getter protected boolean justXMoreEnabled;
    @Getter protected boolean fakePlayersEnabled;
    @Getter protected int xValue;
    @Getter protected int maxPlayers;
    @Getter protected String fakePlayers;
    @Getter protected boolean updateChecker;
    @Getter protected boolean disablePlayerListHover;

    public abstract void reload();

    public final class MOTD {
        private final I icon;
        private final String motd;

        private MOTD(final @Nullable I icon, final @Nullable String motd) {
            this.icon = icon;
            this.motd = motd;
        }

        public @Nullable String motd() {
            return motd;
        }

        public @Nullable I icon() {
            return icon;
        }
    }

    protected List<String> loadIcons(File iconFolder) {
        final List<String> logOutput = new ArrayList<>();
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
                        final I newIcon = createIcon(bufferedImage);
                        this.icons.put(icon.getName().split("\\.")[0], newIcon);
                    } else {
                        logOutput.add("Could not load " + icon.getName() + ": image must be 64x64px");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logOutput.add("Could not load " + icon.getName() + ": invalid image file");
                }
            }
        }
        return logOutput;
    }

    protected abstract @NonNull I createIcon(final @NonNull BufferedImage bufferedImage) throws Exception;

    private @Nullable I icon(final int index) {
        if (icons.isEmpty()) {
            return null;
        }
        if (icons.containsKey(String.valueOf(index))) {
            return icons.get(String.valueOf(index));
        }
        final int randomIndex = ThreadLocalRandom.current().nextInt(icons.size());
        final Iterator<I> iterator = icons.values().iterator();
        for (int i = 0; i < randomIndex; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public @NonNull MOTD getMOTD(final int onlinePlayers, final int maxPlayers) {
        final int index;
        final String motd;
        if (motdEnabled) {
            index = motds.size() == 1 ? 0 : ThreadLocalRandom.current().nextInt(motds.size());
            motd = motds.get(index).replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                    .replace("{maxPlayers}", String.valueOf(maxPlayers))
                    .replace("{br}", System.lineSeparator());
        } else {
            motd = null;
            index = 0;
        }
        return new MOTD(icon(index), motd);
    }

    public int getAdjustedMaxPlayers(final int onlinePlayers, final int actualMaxPlayers) {
        if (maxPlayersEnabled) {
            return justXMoreEnabled ? onlinePlayers + xValue : maxPlayers;
        } else {
            return actualMaxPlayers;
        }
    }
}
