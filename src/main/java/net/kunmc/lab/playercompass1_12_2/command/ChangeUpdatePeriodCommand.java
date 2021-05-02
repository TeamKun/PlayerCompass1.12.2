package net.kunmc.lab.playercompass1_12_2.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChangeUpdatePeriodCommand implements CommandExecutor {
    private final CompassTaskManager manager = CompassTaskManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;
        long period;
        try {
            period = Long.parseLong(args[0]);
            if (period < 0) {
                sender.sendMessage(ChatColor.RED + "<period>は0以上の数値を指定してください.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "<period>は0以上の数値を指定してください.");
            return true;
        }
        manager.changeUpdatePeriod(period);
        sender.sendMessage(String.format(ChatColor.GREEN + "PlayerCompassのアップデート間隔を%dtickに変更しました.", period));
        return true;
    }
}
