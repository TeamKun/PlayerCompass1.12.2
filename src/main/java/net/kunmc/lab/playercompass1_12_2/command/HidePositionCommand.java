package net.kunmc.lab.playercompass1_12_2.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HidePositionCommand implements CommandExecutor {
    private final PositionTaskManager manager = PositionTaskManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーから実行してください.");
            return true;
        }

        manager.unregister(((Player) sender));
        sender.sendMessage(ChatColor.GREEN + "座標を非表示にしました.");
        return true;
    }
}
