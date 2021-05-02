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
    private final Map<UUID, Boolean> isPositionShown = new HashMap<>();
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

        isPositionShown.putIfAbsent(senderUUID, false);
        if (isPositionShown.get(senderUUID)) {
            manager.unregister(sender);
            sender.sendMessage(ChatColor.GREEN + "座標を非表示にしました.");
            isPositionShown.put(senderUUID, false);
        } else {
            String targetName = item.getItemMeta().getDisplayName().split("\\(")[0];
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + targetName + "はオフラインです.");
                return;
            }
            manager.register(sender, target);
            sender.sendMessage(ChatColor.GREEN + targetName + "の座標をアクションバーに表示しました.");
            isPositionShown.put(senderUUID, true);
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
