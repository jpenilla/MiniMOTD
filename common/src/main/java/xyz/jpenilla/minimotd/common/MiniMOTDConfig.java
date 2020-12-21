package xyz.jpenilla.minimotd.common;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.Random;

@FieldNameConstants
public abstract class MiniMOTDConfig {
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
    @Getter protected final ArrayList<String> motds = new ArrayList<>();
    @Getter protected boolean motdEnabled;
    @Getter protected boolean maxPlayersEnabled;
    @Getter protected boolean justXMoreEnabled;
    @Getter protected boolean fakePlayersEnabled;
    @Getter protected int xValue;
    @Getter protected int maxPlayers;
    @Getter protected String fakePlayers;
    @Getter protected boolean updateChecker;
    @Getter protected boolean disablePlayerListHover;

    public abstract void reload();

    public String getMOTD(int onlinePlayers, int maxPlayers) {
        String motd;
        if (motds.size() == 1) {
            motd = motds.get(0);
        } else {
            motd = motds.get(new Random().nextInt(motds.size()));
        }
        return motd
                .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                .replace("{maxPlayers}", String.valueOf(maxPlayers))
                .replace("{br}", System.lineSeparator());
    }

    public int getAdjustedMaxPlayers(int onlinePlayers, int actualMaxPlayers) {
        if (maxPlayersEnabled) {
            return justXMoreEnabled ? onlinePlayers + xValue : maxPlayers;
        } else {
            return actualMaxPlayers;
        }
    }
}
