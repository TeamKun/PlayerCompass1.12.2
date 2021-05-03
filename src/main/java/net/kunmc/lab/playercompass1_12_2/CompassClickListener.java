package net.kunmc.lab.playercompass1_12_2;

import net.kunmc.lab.playercompass1_12_2.command.PositionTaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompassClickListener implements Listener {
    private final PositionTaskManager manager = PositionTaskManager.getInstance();
    private final Map<UUID, PosStatus> statuses = new HashMap<>();
    private final Map<UUID, Boolean> isCoolDown = new HashMap<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) return;

        ItemStack item = e.getItem();
        if (item == null || !item.getType().equals(Material.COMPASS)) return;

        //トグルクールダウンの機能
        Player sender = e.getPlayer();
        UUID senderUUID = sender.getUniqueId();
        isCoolDown.putIfAbsent(senderUUID, false);
        if (isCoolDown.get(senderUUID)) return;
        isCoolDown.put(senderUUID, true);
        new CoolDownTask(senderUUID).runTaskLater(PlayerCompassPlugin.getInstance(), 8);

        String newTargetName = item.getItemMeta().getDisplayName().split("\\(")[0].replace(ChatColor.WHITE.toString(), "");
        statuses.putIfAbsent(senderUUID, new PosStatus(newTargetName, false));
        String oldTargetName = statuses.get(senderUUID).targetName;
        Player oldTarget = Bukkit.getPlayer(oldTargetName);

        if (statuses.get(senderUUID).isShown && oldTargetName.equals(newTargetName)) {
            manager.unregister(sender);
            sender.sendMessage(ChatColor.GREEN + "座標を非表示にしました.");
            sender.sendMessage(ChatColor.GREEN + newTargetName + "の発光をオフにしました.");

            if (oldTarget != null) oldTarget.setGlowing(false);
            statuses.get(senderUUID).isShown = false;
        } else {
            if (oldTarget != null) {
                oldTarget.setGlowing(false);
                sender.sendMessage(ChatColor.GREEN + oldTargetName + "の発光をオフにしました.");
            }

            Player newTarget = Bukkit.getPlayer(newTargetName);
            if (newTarget == null) {
                sender.sendMessage(ChatColor.RED + newTargetName + "はオフラインです.");
                return;
            }
            manager.register(sender, newTarget);
            newTarget.setGlowing(true);
            sender.sendMessage(ChatColor.GREEN + newTargetName + "の座標をアクションバーに表示しました.");
            sender.sendMessage(ChatColor.GREEN + newTargetName + "を光らせました.");
            sender.sendMessage(ChatColor.GREEN + "もう一度右クリックすると非表示に出来ます.");
            statuses.put(senderUUID, new PosStatus(newTargetName, true));
        }
    }

    private class PosStatus {
        public String targetName;
        public boolean isShown;

        public PosStatus(String targetName, boolean isShown) {
            this.targetName = targetName;
            this.isShown = isShown;
        }
    }

    private class CoolDownTask extends BukkitRunnable {
        UUID uuid;

        CoolDownTask(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public void run() {
            isCoolDown.put(uuid, false);
        }
    }
}
