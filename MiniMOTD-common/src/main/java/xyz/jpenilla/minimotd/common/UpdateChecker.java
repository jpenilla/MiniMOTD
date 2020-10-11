package xyz.jpenilla.minimotd.common;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private final JsonParser parser = new JsonParser();
    private final String currentVersion;

    public UpdateChecker(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public CompletableFuture<List<String>> checkVersion() {
        return CompletableFuture.supplyAsync(() -> {
            final List<String> messages = new ArrayList<>();
            final JsonArray result;
            try {
                result = parser.parse(new InputStreamReader(new URL("https://api.github.com/repos/jmanpenilla/MiniMOTD/releases").openStream(), Charsets.UTF_8)).getAsJsonArray();
            } catch (IOException exception) {
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
        });
    }
}