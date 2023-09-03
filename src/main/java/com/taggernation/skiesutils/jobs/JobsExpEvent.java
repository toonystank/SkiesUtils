package com.taggernation.skiesutils.jobs;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.taggernation.taggernationlib.config.ConfigManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.exception.player.PlayerDataNotLoadedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;

public class JobsExpEvent implements Listener {

    private final String PREFIX = "&bJobPicker &c➜ &7";
    private final String expGain = ChatColor.translateAlternateColorCodes('&',PREFIX + "You cannot gain exp/points/money from your job while AFK");
    private final Map<Player, ConfigManager> playerConfig = new HashMap<>();
    private final Plugin plugin;
    public  JobsExpEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShopCommand(PlayerCommandPreprocessEvent event) throws PlayerDataNotLoadedException {
        if (event.getMessage().equals("/shop")) {
            event.getPlayer().sendMessage(ChatColor.RED + "shopping opening");
            ShopGuiPlusApi.openMainMenu(event.getPlayer().getPlayer());
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onExpGain(JobsExpGainEvent event) {
        String isAFK = PlaceholderAPI.setPlaceholders(event.getPlayer().getPlayer(), "%cmi_user_afk%");
        if (isAFK.contains("True")) {
            Objects.requireNonNull(event.getPlayer().getPlayer()).sendMessage(expGain);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPayment(JobsPrePaymentEvent event) {
        if (event.getBlock() != null) {
            createConfig(event.getPlayer().getPlayer());
            if (playerConfig.get(event.getPlayer().getPlayer()).getStringList("blocks." + event.getBlock().getType()).contains(event.getBlock().getLocation().toString())) {
                event.setCancelled(true);
                Bukkit.getLogger().info("Cancelled payment");
            }
        }
        String isAFK = PlaceholderAPI.setPlaceholders(event.getPlayer().getPlayer(), "%cmi_user_afk%");
        if (isAFK.contains("True")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onLevelUp(JobsLevelUpEvent event) {
        String isAFK = PlaceholderAPI.setPlaceholders(event.getPlayer().getPlayer(), "%cmi_user_afk%");
        if (isAFK.contains("True")) {
            event.setCancelled(true);
        }
    }
    public void createConfig(Player player) {
        if ((playerConfig.get(player) == null) || (!playerConfig.containsKey(player))) {
            try {
                playerConfig.put(player, new ConfigManager(plugin, player.getName() + ".yml", "PlayerData", false, false));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean contains(Player player, Block block) {
        if (playerConfig.get(player) == null) return false;
        return playerConfig.get(player).getConfig().getList("locations.place") != null;
    }


}
