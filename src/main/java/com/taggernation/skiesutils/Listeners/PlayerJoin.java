package com.taggernation.skiesutils.Listeners;

import com.taggernation.skiesutils.IPJoinLimit;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final IPJoinLimit ipJoinLimit;

    public PlayerJoin(IPJoinLimit ipJoinLimit) {
        this.ipJoinLimit = ipJoinLimit;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (ipJoinLimit.canAllow(event.getPlayer()) != IPJoinLimit.ReturnType.SUCCESS) {
            event.getPlayer().kick(Component.text("You have reached the maximum amount of players allowed on this IP address."));
        }else if (ipJoinLimit.canAllow(event.getPlayer()) == IPJoinLimit.ReturnType.SUCCESS) {
            Bukkit.getLogger().info(event.getPlayer().getName() + " has joined the server.");
        }
    }
}
