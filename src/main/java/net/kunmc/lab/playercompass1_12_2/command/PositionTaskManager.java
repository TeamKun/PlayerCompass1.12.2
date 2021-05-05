package net.kunmc.lab.playercompass1_12_2.command;

import net.kunmc.lab.playercompass1_12_2.PlayerCompassPlugin;
import net.kunmc.lab.playercompass1_12_2.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PositionTaskManager {
    private static final PositionTaskManager singleton = new PositionTaskManager();
    private final HashMap<UUID, Integer> taskIDs = new HashMap<>();

    private PositionTaskManager() {
    }

    public static PositionTaskManager getInstance() {
        return singleton;
    }

    public void register(Player sender, Player target) {
        Integer oldTaskID = taskIDs.get(sender.getUniqueId());
        if (oldTaskID != null) Bukkit.getScheduler().cancelTask(oldTaskID);

        BukkitTask newTask = new ShowPosTask(sender.getName(), target.getName()).runTaskTimerAsynchronously(PlayerCompassPlugin.getInstance(), 0, 8);
        taskIDs.put(sender.getUniqueId(), newTask.getTaskId());
    }

    public void unregister(Player sender) {
        Integer oldTaskID = taskIDs.get(sender.getUniqueId());
        if (oldTaskID != null) Bukkit.getScheduler().cancelTask(oldTaskID);
        sender.sendActionBar(" ");
    }

    private class ShowPosTask extends BukkitRunnable {
        String senderName;
        String targetName;
        Location lastLoc;

        ShowPosTask(String senderName, String targetName) {
            this.senderName = senderName;
            this.targetName = targetName;
        }

        @Override
        public void run() {
            Player sender = Bukkit.getPlayer(senderName);
            if (sender == null) return;
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) this.lastLoc = target.getLocation();

            try {
                double distance = Utils.calcPlaneDistance(sender.getLocation(), lastLoc);
                sender.sendActionBar(String.format("%sの座標 X:%.0f Y:%.0f Z:%.0f 距離:%.0fm", targetName, lastLoc.getX(), lastLoc.getY(), lastLoc.getZ(), distance));
            } catch (IllegalArgumentException ignore) {
                String worldName = Utils.convertWorldName(lastLoc.getWorld().getName());
                sender.sendActionBar(String.format("%sの座標 X:%.0f Y:%.0f Z:%.0f %sに居ます", targetName, lastLoc.getX(), lastLoc.getY(), lastLoc.getZ(), worldName));
            }
        }
    }
}