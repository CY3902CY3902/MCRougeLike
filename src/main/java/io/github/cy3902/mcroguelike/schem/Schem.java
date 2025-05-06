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
import java.util.function.Consumer;

public class Schem {
    private final String name;
    private final File file;
    private Clipboard clipboard;
    private Consumer<Boolean> pasteCallback;

    public Schem(String name, File file, Location playerLocation) {
        this.name = name;
        this.file = file;
        loadSchematic();
    }

    public void setPasteCallback(Consumer<Boolean> callback) {
        this.pasteCallback = callback;
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

    /**
     * 計算結構中心點
     * @return 結構中心點
     */
    public BlockVector3 calculateCenterPoint() {
        if (clipboard == null) {
            return null;
        }

        BlockVector3 dimensions = clipboard.getDimensions();
        BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
        return dimensions.divide(2).add(clipboardOffset);
    }

    public void paste(Location location) {
        if (clipboard == null) {
            if (pasteCallback != null) {
                pasteCallback.accept(false);
            }
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugins()[0], () -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(location.getWorld().getName()))) {
                BlockVector3 centerPoint = calculateCenterPoint();
                if (centerPoint == null) {
                    if (pasteCallback != null) {
                        pasteCallback.accept(false);
                    }
                    return;
                }

                BlockVector3 pasteLocation = BlockVector3.at(
                    location.getBlockX() - centerPoint.getBlockX(),
                    location.getBlockY() - centerPoint.getBlockY(),
                    location.getBlockZ() - centerPoint.getBlockZ()
                );
                
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(pasteLocation)
                        .build();
                
                Operations.complete(operation);
                
                if (pasteCallback != null) {
                    Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugins()[0], () -> {
                        pasteCallback.accept(true);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (pasteCallback != null) {
                    Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugins()[0], () -> {
                        pasteCallback.accept(false);
                    });
                }
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

