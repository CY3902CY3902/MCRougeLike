package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * 處理重新加載插件的指令。
 */
public class ReloadCommand extends AbstractsCommand {

    /**
     * 初始化 ReloadCommand 實例。
     */
    public ReloadCommand() {
        super("mcroguelike.reload", "reload", 1);
    }

    /**
     * 處理重新加載指令。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     */
    @Override
    public void handle(CommandSender sender, String[] args) {
        try {
            // 重新載入插件
            MCRogueLike.getInstance().reloadConfig();
            
            // 重新載入語言文件
            MCRogueLike.getLang().reload();
            
            // 重新初始化插件
            MCRogueLike.getInstance().initEssential();
            
            sender.sendMessage(lang.getMessage("reload_success"));
        } catch (Exception e) {
            sender.sendMessage(lang.getMessage("reload_error"));
            e.printStackTrace();
        }
    }

    /**
     * 提供自動完成建議，此指令沒有自動完成建議。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 自動完成選項列表，為 null
     */
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
