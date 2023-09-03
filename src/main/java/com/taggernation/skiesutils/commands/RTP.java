package com.taggernation.skiesutils.commands;

import com.taggernation.libs.aikar.acf.BaseCommand;
import com.taggernation.libs.aikar.acf.annotation.CommandAlias;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.taggernation.libs.aikar.acf.annotation.CommandPermission;
import com.taggernation.libs.aikar.acf.annotation.Description;
import com.taggernation.libs.aikar.acf.annotation.Subcommand;
import com.taggernation.skiesutils.CameraHelper;
import com.taggernation.skiesutils.SkiesUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.taggernation.skiesutils.SkiesUtils.protocolManager;


@CommandAlias("rtp")
public class RTP extends BaseCommand implements Listener {

    private final SkiesUtils plugin;

    public RTP(SkiesUtils plugin) {
        this.plugin = plugin;
    }

    @Subcommand("start")
    @CommandPermission("rtp.use")
    @Description("rtp")
    public void onUse(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            player.sendMessage("§aTeleporting...");
            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            animation(player, stand);
        }
    }
    void animation(Player player, ArmorStand stand) {
        Location startingLocation = player.getLocation();
        Location endingLocation = new Location(player.getWorld(), startingLocation.getX(), player.getWorld().getMaxHeight(), startingLocation.getZ());
        int standId = CameraHelper.getID(stand);
        int playerId = CameraHelper.getID(player);
        PacketContainer camera = new PacketContainer(PacketType.Play.Server.CAMERA);
        camera.getIntegers().write(0, standId);
        try {
            protocolManager.sendServerPacket(player, camera);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Vector startingPoint = startingLocation.toVector();

        Vector endingPoint = endingLocation.toVector();

        var vecDirection = new CameraHelper.CameraVector();
        new CameraHelper().setVecDirection(startingPoint, endingPoint, vecDirection);
        final int[] pitch = {90};
        int yaw = 90;
        final boolean[] addOperator = {true};

        new BukkitRunnable() {
            @Override
            public void run() {
                if ((int) startingPoint.getX() >= (int) endingPoint.getX() && (int) startingPoint.getY() >= (int) endingPoint.getY() && (int) startingPoint.getZ() >= (int) endingPoint.getZ()) {
                    camera.getIntegers().write(0, playerId);
                    try {
                        protocolManager.sendServerPacket(player, camera);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stand.remove();
                    cancel();
                    player.sendMessage("§aTeleported!");
                    player.teleport(endingLocation);
                    return;
                }
                startingPoint.setX(startingPoint.getX() + vecDirection.x);
                startingPoint.setY(startingPoint.getY() + vecDirection.y);
                startingPoint.setZ(startingPoint.getZ() + vecDirection.z);
                if (pitch[0] == 120) addOperator[0] = false;
                if (pitch[0] == 60) addOperator[0] = true;
                if (addOperator[0]) pitch[0] += 1;
                else pitch[0] -= 1;
                player.sendMessage("§a" + startingPoint.getX() + " " + startingPoint.getY() + " " + startingPoint.getZ() + " " + pitch[0]);
                stand.teleport(new Location(player.getLocation().getWorld(), startingPoint.getX(), startingPoint.getY(), startingPoint.getZ(), yaw, pitch[0]));

            }
        }.runTaskTimer(plugin, 0, 1);
    }

}
