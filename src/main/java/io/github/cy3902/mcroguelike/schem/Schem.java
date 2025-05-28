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
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

import io.github.cy3902.mcroguelike.MCRogueLike;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class Schem {
    private final String name;
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private final File file;
    private Clipboard clipboard;
    private Consumer<Boolean> pasteCallback;
    private World world;
    private Location centerPoint;

    public Schem(String name, File file, World world) {
        this.name = name;
        this.file = file;
        this.world = world;
        loadSchematic(world);
    }

    public void setPasteCallback(Consumer<Boolean> callback) {
        this.pasteCallback = callback;
    }

    private void loadSchematic(World world) {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format != null) {
                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    clipboard = reader.read();
                }
            }
            String location = mcroguelike.getSql().select("SELECT center_x, center_y, center_z FROM mcroguelike_schem WHERE name = ?", new String[] { name });
            if (location != null) {
                String[] parts = location.split(",");
                if (parts.length == 3) {
                    centerPoint = new Location(
                        world,
                        Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2])
                    );
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
    
        Region region = clipboard.getRegion();
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
    
        // 計算幾何中心點（注意：取整數）
        int centerX = (min.getBlockX() + max.getBlockX()) / 2;
        int centerY = min.getBlockY(); // 根據你的需求，通常以最低Y為基準
        int centerZ = (min.getBlockZ() + max.getBlockZ()) / 2;
    
        return BlockVector3.at(centerX, centerY, centerZ);
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

    public Location getCenterPoint() {
        return centerPoint;
    }
    
}

