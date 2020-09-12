package xyz.jpenilla.minimotd.bungee;

import lombok.Getter;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import xyz.jpenilla.minimotd.common.UpdateChecker;

import java.util.concurrent.ExecutionException;

public class MiniMOTD extends Plugin {
    @Getter private BungeeConfig cfg;
    @Getter private BungeeAudiences audiences;

    @Override
    public void onEnable() {
        this.audiences = BungeeAudiences.create(this);
        this.cfg = new BungeeConfig(this);
        getProxy().getPluginManager().registerListener(this, new PingListener(this));
        getProxy().getPluginManager().registerCommand(this, new BungeeCommand(this));
        Metrics metrics = new Metrics(this, 8137);

        try {
            new UpdateChecker(this.getDescription().getVersion()).checkVersion().get().forEach(message -> getLogger().info(message));
        } catch (InterruptedException | ExecutionException e) {
            getLogger().info("failed to check for update: " + e.getMessage());
        }
    }
}
