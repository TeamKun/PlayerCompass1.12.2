package net.kunmc.lab.playercompass1_12_2.command;

import net.kunmc.lab.playercompass1_12_2.PlayerCompassPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class CompassTaskManager {
    HashMap<String, BukkitTask> tasks = new HashMap<>();
    private static final CompassTaskManager singleton = new CompassTaskManager();

    public static CompassTaskManager getInstance() {
        return singleton;
    }

    private CompassTaskManager() {
    }

    public void register(String senderName, String targetName) {
        if (tasks.get(senderName) != null) tasks.get(senderName).cancel();
        BukkitTask task = new CompassUpdateTask(senderName, targetName).runTaskTimerAsynchronously(PlayerCompassPlugin.getInstance(), 0, 8);
        tasks.put(senderName, task);
    }

    private class CompassUpdateTask extends BukkitRunnable {
        String senderName;
        String targetName;

        CompassUpdateTask(String senderName, String targetName) {
            this.senderName = senderName;
            this.targetName = targetName;
        }

        @Override
        public void run() {
            Player sender = Bukkit.getPlayer(senderName);
            if (sender == null) return;

            Player target = Bukkit.getPlayer(targetName);
            if (target == null) return;

            Location loc = target.getLocation();
            sender.setCompassTarget(loc);

            PlayerInventory inventory = sender.getInventory();
            inventory.forEach(item -> {
                if (item.getType().equals(Material.COMPASS)) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(String.format("%s( X:%.0f Y:%.0f Z:%.0f )", target.getName(), loc.getX(), loc.getY(), loc.getZ()));
                    item.setItemMeta(meta);
                }
            });
            ItemStack offHand = inventory.getItemInOffHand();
            if (offHand.getType().equals(Material.COMPASS)) {
                ItemMeta meta = offHand.getItemMeta();
                meta.setDisplayName(String.format("%s( X:%.0f Y:%.0f Z:%.0f )", target.getName(), loc.getX(), loc.getY(), loc.getZ()));
                offHand.setItemMeta(meta);
                inventory.setItemInOffHand(offHand);
            }
        }
    }
}