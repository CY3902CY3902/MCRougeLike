package io.github.cy3902.mcroguelike.commands;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchemCommand extends AbstractCommand {
    public SchemCommand() {
        super("mcroguelike.schem", "save", 2); // 命令格式: /mcrougelike save <filename>
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("player_only"));
            return;
        }

        Player player = (Player) sender;
        String command = args[0].toLowerCase();
        String schemName = args[1];

        if (command.equals("save")) {
            handleSave(player, schemName);
        } else {
            player.sendMessage(lang.getMessage("unknown_command"));
        }
    }

    private void handleSave(Player player, String schemName) {
        try {
            // 獲取玩家的選擇區域
            Region region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player)).getSelection();
            if (region == null) {
                player.sendMessage(lang.getMessage("schem_no_selection"));
                return;
            }

            // 創建 schematics 目錄（如果不存在）
            File schemDir = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics");
            if (!schemDir.exists()) {
                schemDir.mkdirs();
            }

            // 創建 .schem 文件
            File schemFile = new File(schemDir, schemName + ".schem");
            ClipboardFormat format = ClipboardFormats.findByAlias("schem");
            
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(player.getWorld().getName()))) {
                // 複製選中的區域
                BlockVector3 min = region.getMinimumPoint();
                BlockVector3 max = region.getMaximumPoint();
                CuboidRegion cuboidRegion = new CuboidRegion(min, max);
                BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);
                
                // 複製區域內容到剪貼簿
                for (BlockVector3 pos : cuboidRegion) {
                    clipboard.setBlock(pos, editSession.getBlock(pos));
                }
                
                // 保存到文件
                try (ClipboardWriter writer = format.getWriter(new FileOutputStream(schemFile))) {
                    writer.write(clipboard);
                }

                // 紀錄中心點位置但Y軸是結構物的最低點至SQL
                double minY = region.getMinimumPoint().getY();
                BlockVector3 center = BlockVector3.at(
                    (region.getMinimumPoint().getX() + region.getMaximumPoint().getX()) / 2,
                    (region.getMinimumPoint().getY() + region.getMaximumPoint().getY()) / 2,
                    (region.getMinimumPoint().getZ() + region.getMaximumPoint().getZ()) / 2
                );
                double centerX = center.getX();
                double centerY =  minY;
                double centerZ = center.getZ();
                

                String existingRecord = mcRogueLike.getSql().select(
                    "SELECT name FROM `schem` WHERE name = ?",
                    new String[]{schemName}
                );
                
                if (existingRecord != null) {
                    mcRogueLike.getSql().update(
                        "UPDATE `mcroguelike_schem` SET `center_x` = ?, `center_y` = ?, `center_z` = ? WHERE `name` = ?",
                        new String[]{String.valueOf(centerX), String.valueOf(centerY), String.valueOf(centerZ), schemName}
                    );
                } else {
                    mcRogueLike.getSql().insert(
                        "INSERT INTO `mcroguelike_schem` (`name`, `center_x`, `center_y`, `center_z`) VALUES (?, ?, ?, ?)",
                        new String[]{schemName, String.valueOf(centerX), String.valueOf(centerY), String.valueOf(centerZ)}
                    );
                }

                player.sendMessage(lang.getMessage("schem_saved").replace("%filename%", schemName));
            }
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(lang.getMessage("schem_save_failed"));
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("save");
        } else if (args.length == 2) {
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
 