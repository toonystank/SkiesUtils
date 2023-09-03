package com.taggernation.skiesutils.loot;

import com.taggernation.libs.aikar.acf.BaseCommand;
import com.taggernation.libs.aikar.acf.annotation.CommandAlias;
import com.taggernation.libs.aikar.acf.annotation.CommandPermission;
import com.taggernation.libs.aikar.acf.annotation.Description;
import com.taggernation.libs.aikar.acf.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@CommandAlias("SkiesMcLoot|sml|loottable")
public class LootCommand extends BaseCommand {

    private final LootTable lootTable;

    public LootCommand(LootTable lootTable) {
        this.lootTable = lootTable;
    }


    @Subcommand("create")
    @CommandPermission("skiesutils.loottable.create")
    @Description("create loottable")
    public void onCreate(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 1) return;
            if (lootTable.getIds().contains(args[0])) {
                sender.sendMessage("§cID already exists");
                return;
            }
            lootTable.setId(args[0]);
            sender.sendMessage("§aID created");

        }
    }
    @Subcommand("setitem")
    @CommandPermission("skiesutils.loottable.setitem")
    @Description("set Item/")
    public void onSetItem(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 1) return;
            if (!lootTable.getIds().contains(args[0])) {
                player.sendMessage("§cID does not exist");
                return;
            }
            lootTable.setItemStack(args[0], player.getInventory().getItemInMainHand());
            player.sendMessage("§aItem set");
        }
    }
    @Subcommand("setchance")
    @CommandPermission("skiesutils.loottable.setchance")
    @Description("set chance")
    public void onSetChance(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 2) return;
            if (!lootTable.getIds().contains(args[0])) {
                player.sendMessage("§cID does not exist");
                return;
            }
            lootTable.setChance(args[0], Double.parseDouble(args[1]));
            player.sendMessage("§aChance set");
        }
    }
    @Subcommand("remove")
    @CommandPermission("skiesutils.loottable.remove")
    @Description("remove loottable")
    public void onRemove(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 1) return;
            if (!lootTable.getIds().contains(args[0])) {
                player.sendMessage("§cID does not exist");
                return;
            }
            lootTable.remove(args[0]);
            player.sendMessage("§aID removed");
        }
    }

}
