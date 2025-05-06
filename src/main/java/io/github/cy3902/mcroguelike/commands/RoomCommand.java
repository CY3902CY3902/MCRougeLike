package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.gui.RoomGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RoomCommand extends AbstractCommand implements CommandExecutor {
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private final Lang lang;
    private final RoomGUI roomGUI;

    public RoomCommand() {
        super("mcroguelike.room", "room", 1);
        this.lang = mcRogueLike.getLang();
        this.roomGUI = new RoomGUI();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("command.player_only"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("mcroguelike.room")) {
            player.sendMessage(lang.getMessage("command.no_permission"));
            return true;
        }

        roomGUI.openRoomGUI(player, 0);
        return true;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        onCommand(sender, null, null, args);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
} 