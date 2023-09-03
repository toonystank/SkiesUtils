package com.taggernation.skiesutils.general;

import com.taggernation.skiesutils.SkiesUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RainEvent implements Listener {

    SkiesUtils plugin;
    public RainEvent(SkiesUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRainEvent(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getWorld().setStorm(false);
                    event.getWorld().setThundering(false);
                }
            }.runTaskLater(plugin, 6000L);
        }
    }
}
