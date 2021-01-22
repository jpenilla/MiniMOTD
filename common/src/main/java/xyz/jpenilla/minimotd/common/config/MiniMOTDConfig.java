/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2021 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.minimotd.common.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigSerializable
public final class MiniMOTDConfig {

  public MiniMOTDConfig() {
    this(Arrays.asList(
      new MOTD(),
      new MOTD("<blue>Another <bold><red>MOTD", "<italic><underlined><gradient:red:green>much wow")
    ));
  }

  public MiniMOTDConfig(final @NonNull List<MOTD> defaultMOTDs) {
    this.motds.addAll(defaultMOTDs);
  }

  @Comment("The list of MOTDs to display\n"
    + "\n"
    + " - Supported placeholders: {onlinePlayers}, {maxPlayers}\n"
    + " - Putting more than one will cause one to be randomly chosen each refresh\n"
    + "\n"
    + " Tip: If you want to set a certain icon for each MOTD, check out this page: https://github.com/jmanpenilla/MiniMOTD/wiki/Assigning-specific-icons-per-MOTD")
  private final List<MOTD> motds = new ArrayList<>();

  @Comment("Enable MOTD-related features")
  private boolean motdEnabled = true;

  @Comment("Enable server list icon related features")
  private boolean iconEnabled = true;

  private PlayerCountSettings playerCountSettings = new PlayerCountSettings();

  public int adjustedMaxPlayers(final int onlinePlayers, final int actualMaxPlayers) {
    if (this.playerCountSettings.maxPlayersEnabled) {
      return this.playerCountSettings.justXMoreSettings.justXMoreEnabled ? onlinePlayers + this.playerCountSettings.justXMoreSettings.xValue : this.playerCountSettings.maxPlayers;
    } else {
      return actualMaxPlayers;
    }
  }

  @ConfigSerializable
  public static final class MOTD {

    public MOTD() {
    }

    public MOTD(final @NonNull String line1, final @NonNull String line2) {
      this.line1 = line1;
      this.line2 = line2;
    }

    private String line1 = "<rainbow>MiniMOTD Default";

    private String line2 = "MiniMessage <gradient:blue:red>Gradients";

    @Comment("Set the icon to use with this MOTD\n"
      + "  Either use 'random' to randomly choose an icon, or use the name\n"
      + "  of a file in the icons folder (excluding the '.png' extension)\n"
      + "    ex: icon=\"myIconFile\"")
    private String icon = "random";

    public @NonNull String line1() {
      return this.line1;
    }

    public @NonNull String line2() {
      return this.line2;
    }

    public @NonNull String icon() {
      return this.icon;
    }

  }

  @ConfigSerializable
  public static final class PlayerCountSettings {

    @Comment("Enable modification of the max player count")
    private boolean maxPlayersEnabled = true;

    @Comment("Changes the Max Players value")
    private int maxPlayers = 69;

    @Comment("Setting this to true will disable the hover text showing online player usernames")
    private boolean disablePlayerListHover = false;

    @Comment("Settings for the fake player count feature")
    private FakePlayers fakePlayers = new FakePlayers();

    @Comment("Changes the Max Players to be X more than the online players\n"
      + "ex: x=3 -> 16/19 players online.")
    private JustXMore justXMoreSettings = new JustXMore();

    @ConfigSerializable
    public static final class JustXMore {

      @Comment("Enable this feature")
      private boolean justXMoreEnabled = false;

      private int xValue = 3;

    }

    @ConfigSerializable
    public static final class FakePlayers {

      @Comment("Enable fake player count feature")
      private boolean fakePlayersEnabled = false;

      @Comment("Modes: static, random, percent\n"
        + "\n"
        + " - static: This many fake players will be added\n"
        + "     ex: fakePlayers: \"3\"\n"
        + " - random: A random number of fake players in this range will be added\n"
        + "     ex: fakePlayers: \"3:6\"\n"
        + " - percent: The player count will be inflated by this much, rounding up\n"
        + "     ex: fakePlayers: \"25%\"")
      private String fakePlayers = "25%";

    }

  }

  public boolean iconEnabled() {
    return this.iconEnabled;
  }

  public @NonNull List<MOTD> motds() {
    return this.motds;
  }

  public boolean motdEnabled() {
    return this.motdEnabled;
  }

  public boolean fakePlayersEnabled() {
    return this.playerCountSettings.fakePlayers.fakePlayersEnabled;
  }

  public @NonNull String fakePlayers() {
    return this.playerCountSettings.fakePlayers.fakePlayers;
  }

  public boolean disablePlayerListHover() {
    return this.playerCountSettings.disablePlayerListHover;
  }
}
