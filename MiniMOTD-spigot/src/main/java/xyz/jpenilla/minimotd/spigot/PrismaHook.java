package xyz.jpenilla.minimotd.spigot;

import org.checkerframework.checker.nullness.qual.NonNull;
import us.eunoians.prisma.ColorProvider;

public class PrismaHook {
    public String translate(@NonNull String message) {
        return ColorProvider.translatePrismaToHex(message);
    }
}
