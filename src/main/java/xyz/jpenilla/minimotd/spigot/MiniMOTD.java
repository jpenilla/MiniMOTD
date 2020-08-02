package xyz.jpenilla.minimotd.spigot;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniMOTD extends JavaPlugin {
    @Getter private static MiniMOTD instance;
    @Getter private SpigotConfig cfg;
    @Getter private PrismaHook prisma;
    @Getter private boolean isPaperServer;

    @Override
    public void onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaperServer = true;
        } catch (ClassNotFoundException e) {
            isPaperServer = false;
        }
        instance = this;
        if (Bukkit.getPluginManager().isPluginEnabled("Prisma")) {
            prisma = new PrismaHook();
        }
        this.cfg = new SpigotConfig(this);
        if (isPaperServer) {
            getServer().getPluginManager().registerEvents(new PaperPingListener(this), this);
        } else {
            getServer().getPluginManager().registerEvents(new PingListener(this), this);
        }
        final PluginCommand command = getCommand("minimotd");
        if (command != null) {
            command.setExecutor(new SpigotCommand(this));
        }

        Metrics metrics = new Metrics(this, 8132);
    }
}
