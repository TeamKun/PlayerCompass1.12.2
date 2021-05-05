package net.kunmc.lab.playercompass1_12_2.command;

import net.kunmc.lab.playercompass1_12_2.PlayerCompassPlugin;
import net.kunmc.lab.playercompass1_12_2.PlayerCompassPluginData;
import net.kunmc.lab.playercompass1_12_2.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class CompassTaskManager {
    HashMap<String, CompassUpdateTask> tasks = new HashMap<>();
    private static final CompassTaskManager singleton = new CompassTaskManager();
    private final PlayerCompassPluginData data = PlayerCompassPlugin.getData();
    private long updatePeriod = data.getUpdatePointPeriod();

    public static CompassTaskManager getInstance() {
        return singleton;
    }

    private CompassTaskManager() {
    }

    public void register(String senderName, String targetName) {
        if (tasks.get(senderName) != null) tasks.get(senderName).cancel();
        CompassUpdateTask task = new CompassUpdateTask(senderName, targetName);
        task.runTaskTimerAsynchronously(PlayerCompassPlugin.getInstance(), 0, updatePeriod);
        tasks.put(senderName, task);
        data.setApplicant(senderName, targetName);
    }

    public void changeUpdatePeriod(long period) {
        this.updatePeriod = period;
        for (Map.Entry<String, CompassUpdateTask> entry : tasks.entrySet()) {
            CompassUpdateTask oldTask = entry.getValue();
            oldTask.cancel();
            CompassUpdateTask newTask = new CompassUpdateTask(oldTask.senderName, oldTask.targetName);
            newTask.runTaskTimerAsynchronously(PlayerCompassPlugin.getInstance(), 0, updatePeriod);
            tasks.put(entry.getKey(), newTask);
        }
        PlayerCompassPlugin.getData().setUpdatePointPeriod(period);
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

            String displayName = generateCompassName(target.getName(), loc, sender.getLocation());

            PlayerInventory inventory = sender.getInventory();
            inventory.forEach(item -> {
                if (item.getType().equals(Material.COMPASS)) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(displayName);
                    item.setItemMeta(meta);
                }
            });
            ItemStack offHand = inventory.getItemInOffHand();
            if (offHand.getType().equals(Material.COMPASS)) {
                ItemMeta meta = offHand.getItemMeta();
                meta.setDisplayName(displayName);
                offHand.setItemMeta(meta);
                inventory.setItemInOffHand(offHand);
            }
        }

        private String generateCompassName(String targetName, Location dstLoc, Location srcLoc) {
            try {
                double distance = Utils.calcPlaneDistance(dstLoc, srcLoc);
                return String.format("%s%s( X:%.0f Y:%.0f Z:%.0f 距離:%.0fm )", ChatColor.WHITE, targetName, dstLoc.getX(), dstLoc.getY(), dstLoc.getZ(), distance);
            } catch (IllegalArgumentException e) {
                String worldName = Utils.convertWorldName(dstLoc.getWorld().getName());
                return String.format("%s%s( X:%.0f Y:%.0f Z:%.0f %sに居ます )", ChatColor.WHITE, targetName, dstLoc.getX(), dstLoc.getY(), dstLoc.getZ(), worldName);
            }
        }
    }
}