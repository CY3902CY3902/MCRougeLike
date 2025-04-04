package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsCommand;
import io.github.cy3902.mcroguelike.schem.Schem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchemCommand extends AbstractsCommand {
    
    public SchemCommand() {
        super("mcroguelike.schem", "paste", 2); // 命令格式: /mcrougelike paste <filename>
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("player_only"));
            return;
        }

        Player player = (Player) sender;
        String schemName = args[1]; // args[0]是"paste"，args[1]是文件名
        File schemFile = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics/" + schemName + ".schem");

        if (!schemFile.exists()) {
            player.sendMessage(lang.getMessage("schem_file_not_found").replace("%filename%", schemName + ".schem"));
            return;
        }

        // 創建Schem實例並貼上
        Schem schem = new Schem(schemName, schemFile, player.getLocation());
        schem.paste(player.getLocation());
        player.sendMessage(lang.getMessage("schem_pasted").replace("%filename%", schemName));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 2 && args[0].equalsIgnoreCase("paste")) {
            // 獲取schematics目錄下的所有.schem文件
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
 