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
package xyz.jpenilla.minimotd.common.util;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.minimotd.common.Constants;

public final class UpdateChecker {
  private final JsonParser parser = new JsonParser();

  public @NonNull List<String> checkVersion() {
    final JsonArray result;
    final String url = String.format("https://api.github.com/repos/%s/%s/releases", Constants.PluginMetadata.GITHUB_USER, Constants.PluginMetadata.GITHUB_REPO);
    try (final InputStream is = new URL(url).openStream(); InputStreamReader reader = new InputStreamReader(is, Charsets.UTF_8)) {
      result = this.parser.parse(reader).getAsJsonArray();
    } catch (final IOException ex) {
      return Collections.singletonList("Cannot look for updates: " + ex.getMessage());
    }
    final Map<String, String> versionMap = new LinkedHashMap<>();
    result.forEach(element -> versionMap.put(element.getAsJsonObject().get("tag_name").getAsString(), element.getAsJsonObject().get("html_url").getAsString()));
    final List<String> versionList = new LinkedList<>(versionMap.keySet());
    final String currentVersion = "v" + Constants.PluginMetadata.VERSION;
    if (versionList.get(0).equals(currentVersion)) {
      return Collections.emptyList(); // Up to date, do nothing
    }
    if (currentVersion.contains("SNAPSHOT")) {
      return ImmutableList.of(
        "This server is running a development build of " + Constants.PluginMetadata.NAME + "! (" + currentVersion + ")",
        "The latest official release is " + versionList.get(0)
      );
    }
    final int versionsBehind = versionList.indexOf(currentVersion);
    return ImmutableList.of(
      "There is an update available for " + Constants.PluginMetadata.NAME + "!",
      "This server is running version " + currentVersion + ", which is " + (versionsBehind == -1 ? "UNKNOWN" : versionsBehind) + " versions outdated.",
      "Download the latest version, " + versionList.get(0) + " from GitHub at the link below:",
      versionMap.get(versionList.get(0))
    );
  }
}
