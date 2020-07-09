package xyz.jpenilla.minimotd.bungee;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

public class MiniMOTD extends Plugin {
    @Getter private BungeeConfig cfg;

    @Override
    public void onEnable() {
        this.cfg = new BungeeConfig(this);
        getProxy().getPluginManager().registerListener(this, new PingListener(this));
        getProxy().getPluginManager().registerCommand(this, new BungeeCommand(this));
        Metrics metrics = new Metrics(this, 8137);
    }
}
