package xyz.jpenilla.minimotd.spigot;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jpenilla.minimotd.common.UpdateChecker;

public final class MiniMOTD extends JavaPlugin {
    @Getter private static MiniMOTD instance;
    @Getter private SpigotConfig cfg;
    @Getter private PrismaHook prisma;
    @Getter private boolean isPaperServer;
    @Getter private String serverPackageName;
    @Getter private String serverApiVersion;
    @Getter private int majorMinecraftVersion;
    @Getter private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        serverPackageName = this.getServer().getClass().getPackage().getName();
        serverApiVersion = serverPackageName.substring(serverPackageName.lastIndexOf('.') + 1);
        majorMinecraftVersion = Integer.parseInt(serverApiVersion.split("_")[1]);

        try {
            Class.forName("com.destroystokyo.paper.event.server.PaperServerListPingEvent");
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
            if (majorMinecraftVersion > 11) {
                getLogger().info("This server is not using Paper, and therefore some features may be limited or disabled.");
                getLogger().info("Get Paper from https://papermc.io/downloads");
            }
        }
        this.audiences = BukkitAudiences.create(this);
        final PluginCommand command = getCommand("minimotd");
        if (command != null) {
            command.setExecutor(new SpigotCommand(this));
            command.setTabCompleter(new SpigotCommand(this));
        }

        Metrics metrics = new Metrics(this, 8132);

        if (cfg.isUpdateChecker()) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                    new UpdateChecker(this.getDescription().getVersion()).checkVersion().forEach(getLogger()::info));
        }
    }
}
