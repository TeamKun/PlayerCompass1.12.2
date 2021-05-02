package net.kunmc.lab.playercompass1_12_2.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ForKunCommand implements CommandExecutor {
    private final PositionTaskManager manager = PositionTaskManager.getInstance();
    HashMap<UUID, Boolean> isKunPositionShown = new HashMap<>();
    String kunName = "roadhog_kun";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーから実行してください.");
            return true;
        }

        switch (command.getName()) {
            case "kun": {
                CommandExecutor executor = Bukkit.getPluginCommand("compass").getExecutor();
                command.setName("compass");
                executor.onCommand(sender, command, "compass", new String[]{kunName});
                break;
            }
            case "kunxyz": {
                Player p = ((Player) sender);
                UUID senderUUID = p.getUniqueId();

                isKunPositionShown.putIfAbsent(senderUUID, false);
                if (isKunPositionShown.get(senderUUID)) {
                    manager.unregister(p);
                    sender.sendMessage(ChatColor.GREEN + "座標を非表示にしました.");
                    isKunPositionShown.put(senderUUID, false);
                } else {
                    Player kun = Bukkit.getPlayer(kunName);
                    if (kun == null) {
                        sender.sendMessage(ChatColor.RED + kunName + "はオフラインです.");
                        return true;
                    }

                    manager.register(p, kun);
                    sender.sendMessage(ChatColor.GREEN + kunName + "の座標をアクションバーに表示しました.");
                    sender.sendMessage(ChatColor.GREEN + "非表示にするにはもう一度コマンドを実行してください.");
                    isKunPositionShown.put(senderUUID, true);
                }
                break;
            }
        }
        return true;
    }
}
