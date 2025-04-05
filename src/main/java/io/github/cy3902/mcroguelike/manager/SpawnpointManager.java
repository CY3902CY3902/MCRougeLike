package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.files.SpawnpointFile;
import io.github.cy3902.mcroguelike.manager.core.Manager;
import io.github.cy3902.mcroguelike.config.SpawnpointConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 重生點管理器
 * 用於管理所有重生點
 */
public class SpawnpointManager implements Manager {
    private final Map<String, AbstractSpawnpoint> spawnpoints = new HashMap<>();
    private final Map<String, SpawnpointFile> spawnpointFiles = new HashMap<>();
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();

    /**
     * 構造函數
     */
    public SpawnpointManager() {
        loadSpawnpoints();
    }


    @Override
    public void reload() {
        spawnpoints.clear();
        spawnpointFiles.clear();
        
        loadSpawnpoints();
    }

    /**
     * 加載所有重生點
     */
    private void loadSpawnpoints() {
        SpawnpointFile spawnpointFile = new SpawnpointFile();
        for (String spawnpointId : spawnpointFile.getAllConfigs().keySet()) {
            spawnpointFiles.put(spawnpointId, spawnpointFile);
            spawnpoints.put(spawnpointId, spawnpointFile.getSpawnpoint(spawnpointId));
        }
    }

    /**
     * 獲取指定ID的重生點
     * @param spawnpointId 重生點ID
     * @return 重生點物件
     */
    public AbstractSpawnpoint getSpawnpoint(String spawnpointId) {
        return spawnpoints.get(spawnpointId);
    }

    /**
     * 獲取所有重生點
     * @return 重生點列表
     */
    public Map<String, AbstractSpawnpoint> getSpawnpoints() {
        return spawnpoints;
    }

    /**
     * 保存所有重生點
     */
    @Override
    public void save() {
        for (Map.Entry<String, SpawnpointFile> entry : spawnpointFiles.entrySet()) {
            String spawnpointId = entry.getKey();
            SpawnpointFile spawnpointFile = entry.getValue();
            SpawnpointConfig config = spawnpointFile.getConfig(spawnpointId);
            if (config != null) {
                try {
                    spawnpointFile.saveSpawnpoint(spawnpointId, config);
                } catch (Exception e) {
                    mcRogueLike.getLogger().log(Level.SEVERE, "Error saving spawnpoint: " + spawnpointId, e);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        save();
        spawnpoints.clear();
        spawnpointFiles.clear();
    }

    /**
     * 添加重生點
     * @param spawnpointId 重生點ID
     * @param spawnpoint 重生點實例
     */
    public void addSpawnpoint(String spawnpointId, AbstractSpawnpoint spawnpoint) {
        spawnpoints.put(spawnpointId, spawnpoint);
        SpawnpointFile spawnpointFile = new SpawnpointFile();
        spawnpointFiles.put(spawnpointId, spawnpointFile);
    }

    /**
     * 移除重生點
     * @param spawnpointId 重生點ID
     */
    public void removeSpawnpoint(String spawnpointId) {
        if (spawnpoints.containsKey(spawnpointId)) {
            spawnpoints.remove(spawnpointId);
            spawnpointFiles.remove(spawnpointId);
        }
    }

    /**
     * 清除所有重生點
     */
    public void clear() {
        spawnpoints.clear();
        spawnpointFiles.clear();
    }
} 