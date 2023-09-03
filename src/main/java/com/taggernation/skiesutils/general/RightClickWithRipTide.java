package com.taggernation.skiesutils.general;

import com.taggernation.skiesutils.SkiesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RightClickWithRipTide implements Listener {

    private final int coolDown;
    private final SkiesUtils plugin;
    private final List<Player> playersOnCoolDown = new ArrayList<>();

    public RightClickWithRipTide(int coolDown, SkiesUtils plugin) {
        this.coolDown = coolDown;
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        if (event.getItem() == null) return;
        if (!event.getItem().getType().equals(Material.TRIDENT)) return;
        if (event.getItem().getEnchantments().containsKey(Enchantment.RIPTIDE)) {
            if (playersOnCoolDown.contains(event.getPlayer())) {
                event.setCancelled(true);
                return;
            }else {
                playersOnCoolDown.add(event.getPlayer());
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    playersOnCoolDown.remove(event.getPlayer());
                }
            }.runTaskLater(plugin, coolDown);
        }
    }

}
