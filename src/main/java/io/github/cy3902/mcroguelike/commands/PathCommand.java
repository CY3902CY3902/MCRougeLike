package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.abstracts.AbstractsCommand;
import io.github.cy3902.mcroguelike.gui.PathEditGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PathCommand extends AbstractsCommand {
    private final PathEditGUI pathEditGUI;

    public PathCommand() {
        super("mcroguelike.path", "path", 1);
        this.pathEditGUI = PathEditGUI.getInstance();
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家執行！");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(getPermission())) {
            player.sendMessage(ChatColor.RED + "你沒有權限使用此命令！");
            return;
        }

        // 打開路徑管理GUI
        pathEditGUI.openPathManagementGUI(player);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
} 