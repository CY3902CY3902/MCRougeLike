package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.manager.core.Manager;
import io.github.cy3902.mcroguelike.files.MapFile;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import io.github.cy3902.mcroguelike.config.MapConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 地圖管理器
 * 用於管理所有地圖
 */
public class MapManager implements Manager {
    private final Map<String, AbstractsMap> maps = new HashMap<>();
    private final Map<String, MapFile> mapFiles = new HashMap<>();
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();

    /**
     * 構造函數
     * @param plugin 插件實例
     */
    public MapManager() {
        loadMaps();
    }


    @Override
    public void reload() {
        maps.clear();
        mapFiles.clear();
        loadMaps();
    }

    /**
     * 加載所有地圖
     */
    private void loadMaps() {
        MapFile mapFile = new MapFile();
        for (String mapId : mapFile.getAllConfigs().keySet()) {
            mapFiles.put(mapId, mapFile);
            maps.put(mapId, mapFile.getMap(mapId));
        }
    }

    /**
     * 獲取指定ID的地圖
     * @param mapId 地圖ID
     * @return 地圖物件
     */
    public AbstractsMap getMap(String mapId) {
        return maps.get(mapId);
    }

    /**
     * 獲取所有地圖
     * @return 地圖列表
     */
    public Map<String, AbstractsMap> getMaps() {
        return maps;
    }

    /**
     * 保存所有地圖
     */
    @Override
    public void save() {
        for (Map.Entry<String, MapFile> entry : mapFiles.entrySet()) {
            String mapId = entry.getKey();
            MapFile mapFile = entry.getValue();
            MapConfig config = mapFile.getConfig(mapId);
            if (config != null) {
                try {
                    mapFile.saveMap(mapId, config);
                } catch (Exception e) {
                    mcRogueLike.getLogger().log(Level.SEVERE, "Error saving map: " + mapId, e);
                }
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
     * 添加地圖
     * @param mapId 地圖ID
     * @param map 地圖實例
     */
    public void addMap(String mapId, AbstractsMap map) {
        maps.put(mapId, map);
    }

    /**
     * 移除地圖
     * @param mapId 地圖ID
     */
    public void removeMap(String mapId) {
        if (maps.containsKey(mapId)) {
            maps.remove(mapId);
            mapFiles.remove(mapId);
        }
    }

    /**
     * 清除所有地圖
     */
    public void clear() {
        maps.clear();
        mapFiles.clear();
    }
} 