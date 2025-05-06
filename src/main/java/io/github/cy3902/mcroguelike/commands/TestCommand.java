package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.gui.PathGUI;
import io.github.cy3902.mcroguelike.manager.PlayerPathManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand extends AbstractCommand {
    /**
     * 處理GUI相關指令。
     */
    public TestCommand() {
        super("mcroguelike.test", "test", 2);
    }

    /**
     * 處理GUI指令。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     */
    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("player_only"));
            return;
        }

        Player player = (Player) sender;
        
        PlayerPathManager playerPathManager = new PlayerPathManager(player);
        AbstractPath path = playerPathManager.getPath();
        if (path == null) {
            path = mcRogueLike.getPathFile().getPath(args[1]); 
            playerPathManager.setPath(path);
        }
        PathGUI gui = new PathGUI(path);
        gui.openGUI(player);
    }

    /**
     * 提供自動完成建議。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 自動完成選項列表
     */
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
