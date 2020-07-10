package xyz.jpenilla.minimotd.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@FieldNameConstants
public abstract class MiniMOTDConfig {
    @Getter private final ArrayList<String> motds = new ArrayList<>();
    @Getter @Setter private boolean motdEnabled;
    @Getter @Setter private boolean maxPlayersEnabled;
    @Getter @Setter private boolean justXMoreEnabled;
    @Getter @Setter private boolean fakePlayersEnabled;
    @Getter @Setter private int xValue;
    @Getter @Setter private int maxPlayers;
    @Getter @Setter private String fakePlayers;

    public final String MOTDS = "motd." + Fields.motds;
    public final String MOTD_ENABLED = "motd." + Fields.motdEnabled;
    public final String MAX_PLAYERS_ENABLED = Fields.maxPlayers + "." + Fields.maxPlayersEnabled;
    public final String JUST_X_MORE_ENABLED = Fields.maxPlayers + "." + Fields.justXMoreEnabled;
    public final String MAX_PLAYERS = Fields.maxPlayers + "." + Fields.maxPlayers;
    public final String X_VALUE = Fields.maxPlayers + "." + Fields.xValue;
    public final String FAKE_PLAYERS_ENABLED = "bungeeOnly." + Fields.fakePlayersEnabled;
    public final String FAKE_PLAYERS = "bungeeOnly." + Fields.fakePlayers;

    public abstract void reload();

    public String getMOTD(int onlinePlayers, int maxPlayers) {
        if (motds.size() == 1) {
            return motds.get(0);
        } else {
            return motds.get(ThreadLocalRandom.current().nextInt(motds.size()))
                    .replace("{onlinePlayers}", String.valueOf(onlinePlayers))
                    .replace("{maxPlayers}", String.valueOf(maxPlayers));
        }
    }

    public int getAdjustedMaxPlayers(int onlinePlayers, int actualMaxPlayers) {
        if (maxPlayersEnabled) {
            return justXMoreEnabled ? onlinePlayers + xValue : maxPlayers;
        } else {
            return actualMaxPlayers;
        }
    }
}
