package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.gui.RoomGUIHandler;
import io.github.cy3902.mcroguelike.gui.SpawnpointGUIHandler;
import io.github.cy3902.mcroguelike.spawnpoint.Spawnpoint;
import io.github.cy3902.mcroguelike.gui.PathGUIHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 處理插件指令的類別。
 * 實現了 CommandExecutor 和 TabCompleter 介面，以處理指令執行和自動完成。
 */
public class Commands implements CommandExecutor, TabCompleter {

    protected final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    protected final Lang lang = mcroguelike.getLang();
    protected static LinkedHashMap<String, AbstractCommand> commands = new LinkedHashMap<>();

    /**
     * 註冊所有支持的指令及其處理類別。
     */
    public static void register() {
        commands.put("reload", new ReloadCommand());
        commands.put("gui", new TestCommand());
        commands.put("save", new SchemCommand());
        commands.put("remove", new SchemRemoveCommand());
        commands.put("room", new RoomCommand());
        commands.put("party", new PartyCommand());

        // 註冊GUI處理器    
        RoomGUIHandler.getInstance();
        PathGUIHandler.getInstance();
        SpawnpointGUIHandler.getInstance();
    }

    /**
     * 註冊單個命令
     * @param command 要註冊的命令
     */
    public static void registerCommand(AbstractCommand command) {
        commands.put(command.getCommand(), command);
    }

    /**
     * 處理指令執行。
     * 根據指令名稱和參數調用對應的處理類別，並檢查權限。
     *
     * @param commandSender 指令發送者
     * @param command 指令
     * @param s 指令名稱
     * @param strings 指令參數
     * @return 是否成功處理指令
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            // 顯示幫助信息
            commandSender.sendMessage(lang.getMessage("help_player"));
            return true;
        }

        String subCommand = strings[0].toLowerCase();
        AbstractCommand cmd = commands.get(subCommand);

        if (cmd == null) {
            // 未知指令
            commandSender.sendMessage(lang.getMessage("unknown_command"));
            return true;
        }

        // 檢查權限
        if (!commandSender.hasPermission(cmd.getPermission())) {
            commandSender.sendMessage(lang.getMessage("no_permission"));
            return true;
        }

        // 檢查指令長度
        if (!cmd.getLength().contains(strings.length)) {
            // 指令長度不匹配，顯示幫助信息
            commandSender.sendMessage(lang.getMessage("help_player"));
            return true;
        }

        // 執行指令
        cmd.handle(commandSender, strings);
        return true;
    }

    /**
     * 提供指令自動完成建議。
     * 根據已輸入的指令參數提供可能的完成選項。
     *
     * @param commandSender 指令發送者
     * @param command 指令
     * @param s 指令名稱
     * @param strings 已輸入的指令參數
     * @return 自動完成選項列表
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            // 第一個參數，提供所有子命令
            for (Map.Entry<String, AbstractCommand> entry : commands.entrySet()) {
                if (commandSender.hasPermission(entry.getValue().getPermission())) {
                    if (entry.getKey().toLowerCase().startsWith(strings[0].toLowerCase())) {
                        completions.add(entry.getKey());
                    }
                }
            }
        } else if (strings.length > 1) {
            // 後續參數，提供對應子命令的自動完成
            String subCommand = strings[0].toLowerCase();
            AbstractCommand cmd = commands.get(subCommand);
            if (cmd != null && commandSender.hasPermission(cmd.getPermission())) {
                List<String> cmdCompletions = cmd.complete(commandSender, strings);
                if (cmdCompletions != null) {
                    for (String completion : cmdCompletions) {
                        if (completion.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase())) {
                            completions.add(completion);
                        }
                    }
                }
            }
        }

        return completions;
    }
}