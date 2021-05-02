package net.kunmc.lab.playercompass1_12_2.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowPositionCommand implements CommandExecutor {
    private final PositionTaskManager manager = PositionTaskManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーから実行してください.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /playerpos <player>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + targetName + "はオフラインです.");
            return true;
        }

        manager.register(((Player) sender), target);
        sender.sendMessage(ChatColor.GREEN + targetName + "の座標をアクションバーに表示しました.");
        return true;
    }
}
