/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Jason Penilla
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
package xyz.jpenilla.minimotd.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Constants {
  private Constants() {
  }

  public static final class PluginMetadata {
    private PluginMetadata() {
    }

    public static final String NAME = "${PLUGIN_NAME}";
    public static final String VERSION = "${PLUGIN_VERSION}";
    public static final String WEBSITE = "${PLUGIN_WEBSITE}";
    public static final String GITHUB_USER = "${GITHUB_USER}";
    public static final String GITHUB_REPO = "${GITHUB_REPO}";
    public static final String GITHUB_REPO_URL = "https://github.com/" + GITHUB_USER + "/" + GITHUB_REPO;
  }

  public static final int MINECRAFT_1_16_PROTOCOL_VERSION = 735;

  public static final Component COMMAND_PREFIX = MiniMessage.miniMessage().deserialize("<white>[</white><gradient:#0047AB:#ADD8E6>" + PluginMetadata.NAME + "</gradient><white>]</white>");
}
