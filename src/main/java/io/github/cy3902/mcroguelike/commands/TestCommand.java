package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.abstracts.AbstractsCommand;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import io.github.cy3902.mcroguelike.gui.PathGUI;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand extends AbstractsCommand {
    /**
     * 處理GUI相關指令。
     */
    public TestCommand() {
        super("mcroguelike.test", "test", 1);
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
        
        // 創建一個10x10的測試路徑
        AbstractsPath path = mcRogueLike.getPathManager().getPath("test");
        path.generateTree();
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
