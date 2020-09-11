package xyz.jpenilla.minimotd.paper;

import lombok.NonNull;
import us.eunoians.prisma.ColorProvider;

public class PrismaHook {
    public String translate(@NonNull String message) {
        return ColorProvider.translatePrismaToHex(message);
    }
}
