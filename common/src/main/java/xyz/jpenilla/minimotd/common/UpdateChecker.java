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
package xyz.jpenilla.minimotd.common;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class UpdateChecker {
  private final JsonParser parser = new JsonParser();
  private final String currentVersion;

  public UpdateChecker(final @NonNull String currentVersion) {
    this.currentVersion = currentVersion;
  }

  public List<String> checkVersion() {
    final List<String> messages = new ArrayList<>();
    final JsonArray result;
    try {
      result = this.parser.parse(new InputStreamReader(new URL("https://api.github.com/repos/jmanpenilla/MiniMOTD/releases").openStream(), Charsets.UTF_8)).getAsJsonArray();
    } catch (final IOException exception) {
      messages.add("Cannot look for updates: " + exception.getMessage());
      return messages;
    }
    final Map<String, String> versionMap = new LinkedHashMap<>();
    result.forEach(element -> versionMap.put(element.getAsJsonObject().get("tag_name").getAsString(), element.getAsJsonObject().get("html_url").getAsString()));
    final List<String> versionList = new LinkedList<>(versionMap.keySet());
    final String currentVersion = "v" + this.currentVersion;
    if (versionList.get(0).equals(currentVersion)) {
      messages.add("You are running the latest version of MiniMOTD! :)");
      return messages;
    }
    if (currentVersion.contains("SNAPSHOT")) {
      messages.add("You are running a development build of MiniMOTD! (" + currentVersion + ")");
      messages.add("The latest official release is " + versionList.get(0));
      return messages;
    }
    final int versionsBehind = versionList.indexOf(currentVersion);
    messages.add("There is an update available for MiniMOTD!");
    messages.add("You are running version " + currentVersion + ", which is " + (versionsBehind == -1 ? "many" : versionsBehind) + " versions outdated.");
    messages.add("Download the latest version, " + versionList.get(0) + " from GitHub at the link below:");
    messages.add(versionMap.get(versionList.get(0)));
    return messages;
  }
}
