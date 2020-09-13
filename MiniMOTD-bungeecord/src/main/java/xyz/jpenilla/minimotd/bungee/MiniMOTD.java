package xyz.jpenilla.minimotd.bungee;

import lombok.Getter;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import xyz.jpenilla.minimotd.common.UpdateChecker;

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

        if (cfg.isUpdateChecker()) {
            new UpdateChecker(this.getDescription().getVersion()).checkVersion().whenCompleteAsync((messages, t) -> messages.forEach(message -> getLogger().info(message)));
        }
    }
}
