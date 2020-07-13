package xyz.jpenilla.minimotd.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@FieldNameConstants
public abstract class MiniMOTDConfig {
    public final String MOTDS = "motd." + Fields.motds;
    public final String MOTDS_LEGACY = "motd." + Fields.motdsLegacy;
    public final String MOTD_ENABLED = "motd." + Fields.motdEnabled;
    public final String MAX_PLAYERS_ENABLED = Fields.maxPlayers + "." + Fields.maxPlayersEnabled;
    public final String JUST_X_MORE_ENABLED = Fields.maxPlayers + "." + Fields.justXMoreEnabled;
    public final String MAX_PLAYERS = Fields.maxPlayers + "." + Fields.maxPlayers;
    public final String X_VALUE = Fields.maxPlayers + "." + Fields.xValue;
    public final String FAKE_PLAYERS_ENABLED = "bungeeOnly." + Fields.fakePlayersEnabled;
    public final String FAKE_PLAYERS = "bungeeOnly." + Fields.fakePlayers;
    @Getter private final ArrayList<String> motds = new ArrayList<>();
    @Getter private final ArrayList<String> motdsLegacy = new ArrayList<>();
    @Getter @Setter private boolean motdEnabled;
    @Getter @Setter private boolean maxPlayersEnabled;
    @Getter @Setter private boolean justXMoreEnabled;
    @Getter @Setter private boolean fakePlayersEnabled;
    @Getter @Setter private int xValue;
    @Getter @Setter private int maxPlayers;
    @Getter @Setter private String fakePlayers;

    public abstract void reload();

    private String getMOTD(int onlinePlayers, int maxPlayers, List<String> strings) {
        String motd;
        if (strings.size() == 1) {
            motd = strings.get(0);
        } else {
            motd = strings.get(new Random().nextInt(strings.size()));
        }
        return motd
                .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                .replace("{maxPlayers}", String.valueOf(maxPlayers))
                .replace("{br}", "\n");
    }

    public String getMOTD(int onlinePlayers, int maxPlayers) {
        return getMOTD(onlinePlayers, maxPlayers, motds);
    }

    public String getLegacyMOTD(int onlinePlayers, int maxPlayers) {
        return getMOTD(onlinePlayers, maxPlayers, motdsLegacy);
    }

    public int getAdjustedMaxPlayers(int onlinePlayers, int actualMaxPlayers) {
        if (maxPlayersEnabled) {
            return justXMoreEnabled ? onlinePlayers + xValue : maxPlayers;
        } else {
            return actualMaxPlayers;
        }
    }
}
