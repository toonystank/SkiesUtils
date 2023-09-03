package com.taggernation.skiesutils;

import com.taggernation.taggernationlib.config.ConfigManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IPJoinLimit extends ConfigManager {

    private final int limit;
    private final String bypassPermission;
    @Getter
    private final String kickMessage;
    private final Map<InetSocketAddress, List<Player>> ipMap = new HashMap<>();

    public IPJoinLimit(Plugin plugin) throws IOException {
        super(plugin, "PlayerIPs.yml", false, true);
        this.limit = this.getInt("player-limit");
        this.bypassPermission = this.getString("bypass-permission");
        this.kickMessage = this.getString("kick-message");
        //loadIPMapFromConfig();
    }

    public ReturnType canAllow(Player player) {
        if (player.hasPermission(bypassPermission)) {
            return process(player);
        }
        return ReturnType.NO_PERMISSION;
    }

/*    public void loadIPMapFromConfig() {
        ConfigurationSection configurationSection = this.getConfig().getConfigurationSection(Type.PLAYER_IPS.getKey());
        if (configurationSection == null || configurationSection.getKeys(false).isEmpty()) return;

        configurationSection.getKeys(false).forEach(ip -> {
            List<Player> players = this.getConfig()
                    .getStringList(Type.PLAYER_IPS.getKey() + "." + ip + "." + Type.USERNAMES.getKey())
                    .stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull).toList();
            if (players.isEmpty()) return;
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, this.getInt(Type.PLAYER_IPS.getKey() + "." + ip + "." + Type.PORT.getKey()));
            if (!ipMap.containsKey(inetSocketAddress)) ipMap.put(inetSocketAddress, players);

        });
    }*/

    private ReturnType process(Player player) {
        InetSocketAddress address = player.getAddress();
        if (address == null) return ReturnType.NULL_ADDRESS;
        if (this.getConfig().contains(Type.PLAYER_IPS.getKey() + "." + address)) {
            List<Player> userNameList = this.getStringList(Type.PLAYER_IPS.getKey() + "." + address + "." + Type.USERNAMES.getKey())
                    .stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();
            if (userNameList.size() >= this.limit) return ReturnType.LIMIT_REACHED;
            userNameList.add(player);
            this.getConfig().set(Type.PLAYER_IPS.getKey() + "." + address + "." + Type.USERNAMES.getKey()
                    , userNameList.stream()
                    .map(Player::getName)
                    .toList()
            );
            this.set(Type.PLAYER_IPS.getKey() + "." + address + "." + Type.PORT.getKey(), address.getPort());
            ipMap.put(address, userNameList);
            this.getConfig().set(Type.PLAYER_IPS.getKey() + "." + address + "." + Type.PORT.getKey(), player.getAddress().getPort());
            this.save();
            return ReturnType.SUCCESS;
        }
        return ReturnType.DOES_NOT_EXIST;
    }

    public enum ReturnType {
        NULL_ADDRESS,
        LIMIT_REACHED,
        DOES_NOT_EXIST,
        NO_PERMISSION,
        SUCCESS
    }

    public enum Type {
        PLAYER_IPS("PlayerIPs"),
        USERNAMES("usernames"),
        PORT("port");
        private final String key;
        Type(String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }
}
