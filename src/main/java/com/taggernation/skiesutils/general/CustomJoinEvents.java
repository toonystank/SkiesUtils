package com.taggernation.skiesutils.general;

import com.taggernation.taggernationlib.config.ConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CustomJoinEvents extends ConfigManager implements Listener  {

    private final Plugin plugin;
    /**
     * Initializes the Config. in given path
     *
     * @param plugin   Instance of the plugin you want to initialize the config for
     */
    public CustomJoinEvents(Plugin plugin) throws IOException {
        super(plugin, "commands.yml", false, true);
        this.plugin = plugin;
    }
    public boolean exist() {
        return !getIds().isEmpty();
    }
    public Set<String> getIds() {
        return Objects.requireNonNull(this.getConfig().getConfigurationSection("data")).getKeys(false);
    }
    public List<String> getCommands(String id) {
        return this.getStringList("data." + id + ".commands");
    }
    public boolean getRunBefore(String id) {
        return this.getBoolean("data." + id + ".RunBefore");
    }
    public int getDelay(String id) {
        return this.getInt("data." + id + ".delay");
    }
    public String getPermission(String id) {
        return this.getString("data." + id + ".permission");
    }

    @EventHandler
    public void onPlayerResourcePack(PlayerResourcePackStatusEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) return;
        if (e.getStatus() != PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) return;
        if (!exist()) return;
        getIds().forEach(id -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!getPermission(id).equals("NONE")) {
                        if (!e.getPlayer().hasPermission(getPermission(id))) return;
                    }
                    getCommands(id).forEach(commands -> {
                        if (commands.contains("{player}")) {
                            commands = commands.replace("{player}", e.getPlayer().getName());
                        }
                        if (commands.contains("<delay")) {
                            commands = StringUtils.substringBetween(commands, "<", ">");
                            commands = commands.replace("delay ", "");
                            String finalCommands = commands;
                            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommands), Long.parseLong(finalCommands.replace("delay", "")));
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands);
                    });
                }
            }.runTaskLater(plugin, getDelay(id));
        });
    }
}

// Language: java
