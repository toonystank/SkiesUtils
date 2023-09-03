package com.taggernation.skiesutils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import com.gamingmesh.jobs.Jobs;
import com.taggernation.libs.aikar.acf.BukkitCommandManager;
import com.taggernation.libs.aikar.acf.PaperCommandManager;
import com.taggernation.libs.paperlib.PaperLib;
import com.taggernation.skiesutils.Listeners.PlayerJoin;
import com.taggernation.skiesutils.commands.RTP;
import com.taggernation.skiesutils.general.CinematicCamera;
import com.taggernation.skiesutils.general.CustomJoinEvents;
import com.taggernation.skiesutils.general.RightClickWithRipTide;
import com.taggernation.skiesutils.jobs.JobsExpEvent;
import com.taggernation.skiesutils.jobs.JobsValuePlaceholder;
import com.taggernation.skiesutils.loot.LootCommand;
import com.taggernation.skiesutils.loot.LootTable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class SkiesUtils extends JavaPlugin {

    public LootTable lootTable;
    public static ProtocolManager protocolManager;
    public SkiesUtils plugin;
    public Essentials essentials;

    @Override
    public void onEnable() {
        this.plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        this.getServer().getPluginManager().registerEvents(new JobsExpEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new RightClickWithRipTide(350, this), this);
        try {
            this.getServer().getPluginManager().registerEvents(new CustomJoinEvents(this), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            lootTable = new LootTable(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (PaperLib.isPaper()) {
            Bukkit.getLogger().info("Paper detected, using PaperCommandManager");
            PaperCommandManager pcm = new PaperCommandManager(this);
            pcm.registerCommand(new RTP(this));
        } else {
            BukkitCommandManager bcm = new BukkitCommandManager(this);
            bcm.registerCommand(new RTP(this));
        }
        try {
            new CinematicCamera(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new JobsValuePlaceholder(this).register();
        }
        essentialsExist();
        try {
            IPJoinLimit ipJoinLimit = new IPJoinLimit(this);
            this.getServer().getPluginManager().registerEvents(new PlayerJoin(ipJoinLimit), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  this.getServer().getPluginManager().registerEvents(lootTable, this);
        // Plugin startup logic


    }
    public void essentialsExist() {
        if (this.getServer().getPluginManager().getPlugin("Essentials") != null) {
            essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        } else {
            essentials = null;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
