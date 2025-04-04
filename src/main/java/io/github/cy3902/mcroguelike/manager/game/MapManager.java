package io.github.cy3902.mcroguelike.manager.game;

import io.github.cy3902.mcroguelike.manager.core.Manager;
import io.github.cy3902.mcroguelike.config.MapConfig;
import io.github.cy3902.mcroguelike.files.MapFile;
import io.github.cy3902.mcroguelike.map.Map;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * 地圖管理器
 * 用於管理所有地圖
 */
public class MapManager implements Manager {
    private final java.util.Map<String, Map> maps = new HashMap<>();
    private final java.util.Map<String, MapFile> mapFiles = new HashMap<>();
    private Plugin plugin;

    @Override
    public void initialize(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    @Override
    public void reload() {
        maps.clear();
        mapFiles.clear();
        // TODO: Load all map files from the maps directory
    }

    @Override
    public void save() {
        for (MapFile mapFile : mapFiles.values()) {
            try {
                mapFile.save(mapFile.getConfig());
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error saving map: " + mapFile.getFileName(), e);
            }
        }
    }

    @Override
    public void shutdown() {
        save();
        maps.clear();
        mapFiles.clear();
    }

    /**
     * 獲取地圖
     * @param mapId 地圖ID
     * @return 地圖實例
     */
    public Map getMap(String mapId) {
        return maps.get(mapId);
    }

    /**
     * 添加地圖
     * @param mapId 地圖ID
     * @param map 地圖實例
     */
    public void addMap(String mapId, Map map) {
        maps.put(mapId, map);
    }

    /**
     * 移除地圖
     * @param mapId 地圖ID
     */
    public void removeMap(String mapId) {
        maps.remove(mapId);
    }

    /**
     * 獲取所有地圖
     * @return 地圖映射
     */
    public java.util.Map<String, Map> getMaps() {
        return new HashMap<>(maps);
    }
} 