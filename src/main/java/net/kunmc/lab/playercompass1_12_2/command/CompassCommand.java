package net.kunmc.lab.playercompass1_12_2.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CompassCommand implements CommandExecutor {
    CompassTaskManager manager = CompassTaskManager.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 1) return false;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーから実行してください.");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            commandSender.sendMessage(ChatColor.RED + targetName + "はオフラインです.");
            return true;
        }

        Player sender = ((Player) commandSender);
        manager.register(sender.getName(), target.getName());

        //Compassを持っていない場合は配布する
        PlayerInventory inventory = sender.getInventory();
        boolean hasCompass = false;
        for (ItemStack item : inventory) {
            if (item != null && item.getType().equals(Material.COMPASS)) hasCompass = true;
        }
        hasCompass |= inventory.getItemInOffHand().getType().equals(Material.COMPASS);
        if (!hasCompass) {
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta meta = compass.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "右クリックをすることで対象の座標が");
            lore.add(ChatColor.WHITE + "アクションバーに表示され,また対象が発光します.");
            lore.add(ChatColor.WHITE + "もう一度右クリックをすると");
            lore.add(ChatColor.WHITE + "それらを非表示にすることが出来ます.");
            meta.setLore(lore);
            compass.setItemMeta(meta);

            inventory.addItem(compass);
        }

        sender.sendMessage(ChatColor.GREEN + "コンパスが" + targetName + "を指すようになりました.");
        return true;
    }
}
