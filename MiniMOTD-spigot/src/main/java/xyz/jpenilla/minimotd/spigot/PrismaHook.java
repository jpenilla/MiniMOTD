package xyz.jpenilla.minimotd.spigot;

import lombok.NonNull;
import us.eunoians.prisma.ColorProvider;

public class PrismaHook {
    public String translate(@NonNull String message) {
        return ColorProvider.translatePrismaToHex(message);
    }
}
