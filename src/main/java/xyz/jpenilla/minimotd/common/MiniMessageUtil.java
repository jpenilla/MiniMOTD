package xyz.jpenilla.minimotd.common;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class MiniMessageUtil {
    public static String miniMessageToLegacy(String message) {
        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(MiniMessage.get().parse(message));
    }

    public static List<String> miniMessageToLegacy(List<String> messages) {
        final List<String> l = new ArrayList<>();
        for (String message : messages) {
            l.add(miniMessageToLegacy(message));
        }
        return l;
    }
}
