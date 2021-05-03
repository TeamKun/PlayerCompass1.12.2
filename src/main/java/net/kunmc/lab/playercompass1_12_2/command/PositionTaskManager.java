package net.kunmc.lab.playercompass1_12_2.command;

import net.kunmc.lab.playercompass1_12_2.PlayerCompassPlugin;
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

        BukkitTask newTask = new ShowPosTask(sender, target).runTaskTimerAsynchronously(PlayerCompassPlugin.getInstance(), 0, 8);
        taskIDs.put(sender.getUniqueId(), newTask.getTaskId());
    }

    public void unregister(Player sender) {
        Integer oldTaskID = taskIDs.get(sender.getUniqueId());
        if (oldTaskID != null) Bukkit.getScheduler().cancelTask(oldTaskID);
        sender.sendActionBar(" ");
    }

    private class ShowPosTask extends BukkitRunnable {
        Player sender;
        Player target;
        Location lastLoc;

        ShowPosTask(Player sender, Player target) {
            this.sender = sender;
            this.target = target;
            this.lastLoc = target.getLocation();
        }

        @Override
        public void run() {
            if (target.isOnline()) {
                this.lastLoc = target.getLocation();
            }

            Location loc1 = this.lastLoc.clone();
            loc1.setY(0);
            Location loc2 = sender.getLocation().clone();
            loc2.setY(0);
            double distance = loc1.distance(loc2);

            sender.sendActionBar(String.format("%sの座標 X:%.0f Y:%.0f Z:%.0f 距離:%.0f", target.getName(), lastLoc.getX(), lastLoc.getY(), lastLoc.getZ(), distance));
        }

        public String getTargetName() {
            return target.getName();
        }
    }
}
