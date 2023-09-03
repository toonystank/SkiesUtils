package com.taggernation.skiesutils.general;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.taggernation.skiesutils.CameraHelper;
import com.taggernation.skiesutils.SkiesUtils;
import com.taggernation.taggernationlib.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static com.taggernation.skiesutils.SkiesUtils.protocolManager;

public class CinematicCamera extends ConfigManager implements CommandExecutor {

    private final SkiesUtils plugin;
    /**
     * Initializes the Config. in given path
     *
     * @param plugin   Instance of the plugin you want to initialize the config for
     */
    public CinematicCamera(SkiesUtils plugin) throws IOException {
        super(plugin, "camera.yml", false, false);
        Objects.requireNonNull(plugin.getCommand("camera"), "Command is null in plugin.yml").setExecutor(this);
        this.plugin = plugin;
    }

    /**
     * Gets the Ids of the cameras
     * @return set of ids
     */
    public Set<String> getCameras() {
        try {
            return this.getConfig().getConfigurationSection("data").getKeys(false);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Add a new camera
     * @param cameraID id of the camera
     */
    public void addCamera(String cameraID) {
        if (!isAlreadyThere(cameraID)) return;
        this.set("data.", cameraID);
        this.save();
    }

    /**
     * Checks if the camera is already there
     * @param id id of the camera
     * @return true if the camera is already there
     */
    public boolean isAlreadyThere(String id) {
        if (getCameras() == null) return false;
        return getCameras().contains(id);
    }

    /**
     * Sets the starting location of the camera
     * @param cameraID id of the camera
     * @param location location of the camera
     */
    public void setStartingLocation(String cameraID, Location location) {
        this.set("data." + cameraID + ".startingLocation", location.add(0, 1, 0));
        this.save();
    }

    /**
     * Gets the starting location of the camera
     * @param id id of the camera
     * @return location of the camera
     */
    public Location getStartingLocation(String id) {
        return this.getConfig().getLocation("data." + id + ".startingLocation");

    }

    /**
     * Sets the ending location of the camera
     * @param cameraID id of the camera
     * @param location location of the camera
     */
    public void setEndingLocation(String cameraID, Location location) {
        this.set("data." + cameraID + ".endingLocation", location);
        this.save();
    }

    /**
     * Gets the ending location of the camera
     * @param id id of the camera
     * @return location of the camera
     */
    public Location getEndingLocation(String id) {
        return this.getConfig().getLocation("data." + id + ".endingLocation");
    }

    /**
     * Sets the time it takes to move from the starting location to the ending location
     * @param cameraID id of the camera
     * @param delay time it takes to move from the starting location to the ending location
     */
    public void setDelay(String cameraID, int delay) {
        this.set("data." + cameraID + ".delay", delay);
        this.save();
    }
    public String getCommand(String cameraID) {
        return this.getConfig().getString("data." + cameraID + ".command");
    }

    public void setCommand(String cameraID, String command) {
        this.set("data." + cameraID + ".command", command);
        this.save();
    }

    /**
     * Gets the time it takes to move from the starting location to the ending location
     * @param id id of the camera
     * @return time it takes to move from the starting location to the ending location
     */
    public int getDelay(String id) {
        return this.getInt("data." + id + ".delay");
    }

    public void initialize(String id, Player player) {
        ArmorStand armorStand = (ArmorStand) getStartingLocation(id).getWorld().spawnEntity(getStartingLocation(id), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        int entityId = armorStand.getEntityId();
        int playerId = player.getEntityId();
        PacketContainer camera = new PacketContainer(PacketType.Play.Server.CAMERA);
        camera.getIntegers().write(0, entityId);
        try {
            protocolManager.sendServerPacket(player, camera);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Vector startingPoint = getStartingLocation(id).toVector();

        Vector endingPoint = getEndingLocation(id).toVector();

        var direction = new CameraHelper.CameraVector();
        new CameraHelper().setVecDirection(startingPoint, endingPoint, direction);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (startingPoint.getBlockX() <= endingPoint.getBlockX() && startingPoint.getBlockY() <= endingPoint.getBlockY() && startingPoint.getBlockZ() <= endingPoint.getBlockZ()) {
                    Bukkit.getLogger().info("Camera at z Location and x y");
                    camera.getIntegers().write(0, playerId);
                    try {
                        protocolManager.sendServerPacket(player, camera);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    armorStand.remove();
                    player.teleport(player.getLocation().add(0, 1, 0));
                    player.sendMessage("Camera " + id + " has been initialized");
                    cancel();
                    return;
                }
                startingPoint.setX(startingPoint.getX() + direction.x);
                startingPoint.setY(startingPoint.getY() + direction.y);
                startingPoint.setZ(startingPoint.getZ() + direction.z);
                player.sendMessage(ChatColor.RED + "" + startingPoint.getBlockX() + " " + ChatColor.GREEN + endingPoint.getBlockX() + " " + ChatColor.RED + startingPoint.getBlockY() + " " + ChatColor.GREEN + endingPoint.getBlockY() + " " + ChatColor.RED + startingPoint.getBlockZ() + " " + ChatColor.GREEN + endingPoint.getBlockZ());
                armorStand.teleport(new Location(getStartingLocation(id).getWorld(), startingPoint.getX(), startingPoint.getY(), startingPoint.getZ(), getStartingLocation(id).getYaw(), getStartingLocation(id).getPitch()));
            }

        }.runTaskTimer(plugin, 0, getDelay(id));
    }
    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args[0].equals("create")) {
                addCamera(args[1]);
                player.sendMessage("Camera created");
            }
            if (args[0].equals("setStartingLocation")) {
                setStartingLocation(args[1], player.getLocation());
                player.sendMessage("Starting location set");
            }
            if (args[0].equals("setEndingLocation")) {
                setEndingLocation(args[1], player.getLocation());
                player.sendMessage("Ending location set");
            }
            if (args[0].equals("setDelay")) {
                setDelay(args[1], Integer.parseInt(args[2]));
            }
            if (args[0].equals("start")) {
                if (args.length == 3) {
                    initialize(args[1], Objects.requireNonNull(Bukkit.getPlayer(args[2]), "Player not found"));
                } else {
                    initialize(args[1], player);
                }
                initialize(args[1], player);
                player.sendMessage("Camera started");
            }

        }
        return false;
    }
}
