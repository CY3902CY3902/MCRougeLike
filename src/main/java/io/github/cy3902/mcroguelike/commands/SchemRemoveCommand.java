package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import io.github.cy3902.mcroguelike.config.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchemRemoveCommand extends AbstractCommand {
    public SchemRemoveCommand() {
        super("mcroguelike.schem.remove", "remove", 2); // 命令格式: /mcrougelike remove <filename>
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("player_only"));
            return;
        }

        Player player = (Player) sender;
        String schemName = args[1];

        // 刪除 .schem 文件
        File schemFile = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics", schemName + ".schem");
        if (schemFile.exists()) {
            if (!schemFile.delete()) {
                player.sendMessage(lang.getMessage("schem_remove_failed"));
                return;
            }
        }

        mcRogueLike.getSql().delete("DELETE FROM mcroguelike_schem WHERE name = ?", new String[] { schemName });

        player.sendMessage(lang.getMessage("schem_removed").replace("%filename%", schemName));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 2) {
            // 獲取 schematics 目錄下的所有 .schem 文件
            File schemDir = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics");
            if (schemDir.exists() && schemDir.isDirectory()) {
                File[] files = schemDir.listFiles((dir, name) -> name.endsWith(".schem"));
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName().replace(".schem", "");
                        if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(name);
                        }
                    }
                }
            }
        }
        
        return completions;
    }
} 