package xyz.jpenilla.minimotd.spigot;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniMOTD extends JavaPlugin {
    @Getter private SpigotConfig cfg;
    @Getter private PrismaHook prisma;
    @Getter private static MiniMOTD instance;

    @Override
    public void onEnable() {
        instance = this;
        if (Bukkit.getPluginManager().isPluginEnabled("Prisma")) {
            prisma = new PrismaHook();
        }
        this.cfg = new SpigotConfig(this);
        getServer().getPluginManager().registerEvents(new PingListener(this), this);
        getCommand("minimotd").setExecutor(new SpigotCommand(this));
        Metrics metrics = new Metrics(this, 8132);
    }
}
