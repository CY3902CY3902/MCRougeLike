package io.github.cy3902.mcroguelike.schem;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Schem {
    private final String name;
    private final File file;
    private Clipboard clipboard;
    public Schem(String name, File file, Location playerLocation) {
        this.name = name;
        this.file = file;
        loadSchematic();
    }

    private void loadSchematic() {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format != null) {
                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    clipboard = reader.read();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paste(Location location) {
        if (clipboard == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugins()[0], () -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(location.getWorld().getName()))) {
                // 計算結構的尺寸
                // 獲取剪貼板的尺寸
                BlockVector3 dimensions = clipboard.getDimensions();
                // 計算剪貼板的偏移量
                BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());

                // 計算剪貼板的中心點
                BlockVector3 clipboardCenter = dimensions.divide(2).add(clipboardOffset);

                // 計算貼上位置，使X軸Z軸中心與玩家位置對齊
                BlockVector3 pasteLocation = BlockVector3.at(
                    location.getBlockX() - clipboardCenter.getBlockX(),
                    location.getBlockY() - clipboardOffset.getBlockY(),
                    location.getBlockZ() - clipboardCenter.getBlockZ()
                );
                
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(pasteLocation)
                        .build();
                
                Operations.complete(operation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }
}

