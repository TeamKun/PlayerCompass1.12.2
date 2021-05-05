package net.kunmc.lab.playercompass1_12_2;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompassClickListener implements Listener {
    private final PositionTaskManager manager = PositionTaskManager.getInstance();
    private final Map<UUID, PosStatus> statuses = new HashMap<>();
    private final Map<UUID, Boolean> isCoolDown = new HashMap<>();
    private final Map<UUID, BukkitTask> forceGlowTasks = new HashMap<>();
    private final ProtocolManager protocolManager = PlayerCompassPlugin.getProtocolManager();


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
            if (oldTarget != null) {
                if (forceGlowTasks.get(senderUUID) != null) forceGlowTasks.get(senderUUID).cancel();
                setGlowing(sender, oldTarget, false);
            }
            statuses.get(senderUUID).isShown = false;
        } else {
            if (oldTarget != null) {
                if (forceGlowTasks.get(senderUUID) != null) forceGlowTasks.get(senderUUID).cancel();
                setGlowing(sender, oldTarget, false);
            }
            Player newTarget = Bukkit.getPlayer(newTargetName);
            if (newTarget == null) {
                sender.sendMessage(ChatColor.RED + newTargetName + "はオフラインです.");
                return;
            }
            manager.register(sender, newTarget);
            BukkitTask task = new ForceGlowTask(sender.getName(), newTarget.getName()).runTaskTimerAsynchronously(PlayerCompassPlugin.getInstance(), 0, 4);
            forceGlowTasks.put(senderUUID, task);
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

    private void setGlowing(Player sender, Player target, boolean b) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packetContainer.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(target);
        watcher.setObject(0, serializer, (byte) (b ? 0x40 : 0x00));
        packetContainer.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            protocolManager.sendServerPacket(sender, packetContainer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class ForceGlowTask extends BukkitRunnable {
        String senderName;
        String targetName;

        ForceGlowTask(String senderName, String targetName) {
            this.senderName = senderName;
            this.targetName = targetName;
        }

        @Override
        public void run() {
            Player sender = Bukkit.getPlayer(senderName);
            Player target = Bukkit.getPlayer(targetName);
            if (sender == null || target == null) return;
            setGlowing(sender, target, true);
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
