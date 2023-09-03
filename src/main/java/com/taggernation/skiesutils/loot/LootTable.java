package com.taggernation.skiesutils.loot;

import com.taggernation.libs.aikar.acf.BukkitCommandManager;
import com.taggernation.libs.aikar.acf.PaperCommandManager;
import com.taggernation.libs.paperlib.PaperLib;
import com.taggernation.taggernationlib.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LootTable extends ConfigManager implements Listener {



    public LootTable(Plugin plugin) throws IOException {
        super(plugin, "loot.yml", false, false);
        this.setId("default");


        if (PaperLib.isPaper()) {
            Bukkit.getLogger().info("Paper detected, using PaperCommandManager");
            PaperCommandManager pcm = new PaperCommandManager(plugin);
            pcm.registerCommand(new LootCommand(this));
        } else {
            BukkitCommandManager bcm = new BukkitCommandManager(plugin);
            bcm.registerCommand(new LootCommand(this));
        }
    }





    public Set<String> getIds() {
        return this.getConfig().getConfigurationSection("data").getKeys(false);
    }
    public Double getChance(String id) {
        return this.getDouble("data." + id + ".chance");
    }
    public ItemStack getItemStack(String id) {
        return (ItemStack) this.get("data." + id + ".item");
    }
    public void setItemStack(String id, ItemStack itemStack) {
        this.set("data." + id + ".item", itemStack);
    }
    public void setChance(String id, Double chance) {
        this.set("data." + id + ".chance", chance);
    }
    public void setId(String id) {
        this.set("data." + id + ".id", id);
    }
    public void remove(String id) {
        this.getConfig().set("data." + id, null);
    }

    public List<ItemStack> getChancedItems() {
        if (getIds().isEmpty()) return null;
        var randomNumber = Math.random() * 100;
        Bukkit.getLogger().info(randomNumber + " ");
        List<ItemStack> itemStacks = new ArrayList<>();
        getIds().forEach(id -> {
            if (id.equals("default")) return;
            if(randomNumber < getChance(id)) {
                itemStacks.add(getItemStack(id));
            }
        });
        if (itemStacks.isEmpty()) return null;
        return itemStacks;
    }

    @EventHandler
    public void generateLootChest(LootGenerateEvent event) {
        if (event.getInventoryHolder() instanceof Chest) {
            var itemStacks = getChancedItems();
            if (itemStacks == null) return;
            Random random = new Random();
            var randomItem = itemStacks.get(random.nextInt(itemStacks.size()));
            Bukkit.getLogger().info("Generated Item: " + randomItem.getType() + " At: " + ((Chest) event.getInventoryHolder()).getLocation());
            event.getInventoryHolder().getInventory().addItem(randomItem);
        }
    }

/*    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args[0].equals("create")) {
                if (args.length < 2) return false;
                if (getIds().contains(args[1])) {
                    sender.sendMessage("§cID already exists");
                    return true;
                }
                setId(args[1]);
                sender.sendMessage("§aID created");
                return true;

            }
            if (args[0].equals("set")) {
                if (args.length < 3) return false;
                if (!getIds().contains(args[1])) {
                    sender.sendMessage("§cID does not exist");
                    return true;
                }
                if (args[2].equals("chance")) {
                    if (args.length < 4) return false;
                    setChance(args[1], Double.parseDouble(args[3]));
                    sender.sendMessage("§aChance set");
                    return true;
                }
                if (args[2].equals("item")) {
                    setItemStack(args[1], player.getInventory().getItemInMainHand());
                    sender.sendMessage("§aItem set");
                    return true;
                }
                return false;
            }
            if (args[0].equals("remove")) {
                if (args.length < 2) return false;
                if (!getIds().contains(args[1])) {
                    sender.sendMessage("§cID does not exist");
                    return true;
                }
                remove(args[1]);
                sender.sendMessage("§aID removed");
                return true;
            }
            try {
                this.reload();
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        return false;
    }*/
}
