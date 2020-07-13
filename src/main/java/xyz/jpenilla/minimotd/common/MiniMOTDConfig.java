package xyz.jpenilla.minimotd.common;

import java.util.ArrayList;
import java.util.Random;

public abstract class MiniMOTDConfig {
    public static final class Fields {
        public static final String MOTDS = "MOTDS";

        public static final String MOTDSOLD = "MOTDSOLD";

        public static final String MOTD_ENABLED = "MOTD_ENABLED";

        public static final String MAX_PLAYERS_ENABLED = "MAX_PLAYERS_ENABLED";

        public static final String JUST_X_MORE_ENABLED = "JUST_X_MORE_ENABLED";

        public static final String MAX_PLAYERS = "MAX_PLAYERS";

        public static final String X_VALUE = "X_VALUE";

        public static final String FAKE_PLAYERS_ENABLED = "FAKE_PLAYERS_ENABLED";

        public static final String FAKE_PLAYERS = "FAKE_PLAYERS";

        public static final String motds = "motds";

        public static final String motdsOld = "motdsOld";

        public static final String motdEnabled = "motdEnabled";

        public static final String maxPlayersEnabled = "maxPlayersEnabled";

        public static final String justXMoreEnabled = "justXMoreEnabled";

        public static final String fakePlayersEnabled = "fakePlayersEnabled";

        public static final String xValue = "xValue";

        public static final String maxPlayers = "maxPlayers";

        public static final String fakePlayers = "fakePlayers";
    }

    public final String MOTDS = "motd.motds";

    public final String MOTDSOLD = "motd.motdsOld";

    public final String MOTD_ENABLED = "motd.motdEnabled";

    public final String MAX_PLAYERS_ENABLED = "maxPlayers.maxPlayersEnabled";

    public final String JUST_X_MORE_ENABLED = "maxPlayers.justXMoreEnabled";

    public final String MAX_PLAYERS = "maxPlayers.maxPlayers";

    public final String X_VALUE = "maxPlayers.xValue";

    public final String FAKE_PLAYERS_ENABLED = "bungeeOnly.fakePlayersEnabled";

    public final String FAKE_PLAYERS = "bungeeOnly.fakePlayers";

    private final ArrayList<String> motds = new ArrayList<>();

    private final ArrayList<String> motdsOld = new ArrayList<>();


    private boolean motdEnabled;

    private boolean maxPlayersEnabled;

    private boolean justXMoreEnabled;

    private boolean fakePlayersEnabled;

    private int xValue;

    private int maxPlayers;

    private String fakePlayers;

    public ArrayList<String> getMotds() {
        return this.motds;
    }
    public ArrayList<String> getMotdsOld() {
        return this.motdsOld;
    }

    public boolean isMotdEnabled() {
        return this.motdEnabled;
    }

    public void setMotdEnabled(boolean motdEnabled) {
        this.motdEnabled = motdEnabled;
    }

    public boolean isMaxPlayersEnabled() {
        return this.maxPlayersEnabled;
    }

    public void setMaxPlayersEnabled(boolean maxPlayersEnabled) {
        this.maxPlayersEnabled = maxPlayersEnabled;
    }

    public boolean isJustXMoreEnabled() {
        return this.justXMoreEnabled;
    }

    public void setJustXMoreEnabled(boolean justXMoreEnabled) {
        this.justXMoreEnabled = justXMoreEnabled;
    }

    public boolean isFakePlayersEnabled() {
        return this.fakePlayersEnabled;
    }

    public void setFakePlayersEnabled(boolean fakePlayersEnabled) {
        this.fakePlayersEnabled = fakePlayersEnabled;
    }

    public int getXValue() {
        return this.xValue;
    }

    public void setXValue(int xValue) {
        this.xValue = xValue;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getFakePlayers() {
        return this.fakePlayers;
    }

    public void setFakePlayers(String fakePlayers) {
        this.fakePlayers = fakePlayers;
    }

    public String getMOTD(int onlinePlayers, int maxPlayers) {
        if (this.motds.size() == 1)
            return this.motds.get(0)
                    .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                    .replace("{maxPlayers}", String.valueOf(maxPlayers));
        return ((String)this.motds.get((new Random()).nextInt(this.motds.size())))
                .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                .replace("{maxPlayers}", String.valueOf(maxPlayers));
    }
    public String getMOTDOld(int onlinePlayers, int maxPlayers) {
        if (this.motdsOld.size() == 1)
            return this.motdsOld.get(0)
                    .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                    .replace("{maxPlayers}", String.valueOf(maxPlayers));
        return ((String)this.motdsOld.get((new Random()).nextInt(this.motdsOld.size())))
                .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                .replace("{maxPlayers}", String.valueOf(maxPlayers));
    }

    public int getAdjustedMaxPlayers(int onlinePlayers, int actualMaxPlayers) {
        if (this.maxPlayersEnabled)
            return this.justXMoreEnabled ? (onlinePlayers + this.xValue) : this.maxPlayers;
        return actualMaxPlayers;
    }

    public abstract void reload();
}
