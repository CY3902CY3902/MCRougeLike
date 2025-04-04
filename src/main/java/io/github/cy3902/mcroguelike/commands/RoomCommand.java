package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.abstracts.AbstractsCommand;
import io.github.cy3902.mcroguelike.gui.RoomGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RoomCommand extends AbstractsCommand {
    private final RoomGUI roomGUI;

    public RoomCommand() {
        super("mcroguelike.room.admin", "room", 1);
        this.roomGUI = new RoomGUI();
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

        // 打開GUI
        roomGUI.openRoomGUI(player);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
} 