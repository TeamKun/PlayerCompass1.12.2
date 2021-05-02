package net.kunmc.lab.playercompass1_12_2.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        sender.sendMessage(ChatColor.GREEN + "コンパスが" + targetName + "を指すようになりました.");
        return true;
    }
}
