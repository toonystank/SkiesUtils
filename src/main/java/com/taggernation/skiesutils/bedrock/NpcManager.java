package com.taggernation.skiesutils.bedrock;

import com.taggernation.taggernationlib.config.ConfigManager;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Set;

public class NpcManager extends ConfigManager {
    /**
     * Initializes the Config.
     *
     * @param plugin   Instance of the plugin you want to initialize the config for
     */
    public NpcManager(Plugin plugin) throws IOException {
        super(plugin, "data.yml", false, false);
    }
    public Set<String> getNPCIds() {
        return this.getConfig().getConfigurationSection("data").getKeys(false);
    }
    public boolean addNPC(String npcID) {
        if (!isAlreadyThere(npcID)) return false;
        this.set("data.", npcID);
        return true;
    }
    public boolean isAlreadyThere(String id) {
        return getNPCIds().contains(id);
    }
}
