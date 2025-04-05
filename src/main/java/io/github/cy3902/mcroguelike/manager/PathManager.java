package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import io.github.cy3902.mcroguelike.files.PathFile;
import io.github.cy3902.mcroguelike.manager.core.Manager;
import io.github.cy3902.mcroguelike.config.PathConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 路徑管理器
 * 用於管理所有路徑
 */
public class PathManager implements Manager {
    private final Map<String, AbstractsPath> paths = new HashMap<>();
    private final Map<String, PathFile> pathFiles = new HashMap<>();
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();

    /**
     * 構造函數
     */
    public PathManager() {
        loadPaths();
    }


    @Override
    public void reload() {
        paths.clear();
        pathFiles.clear();
        loadPaths();
    }

    /**
     * 加載所有路徑
     */
    private void loadPaths() {
        PathFile pathFile = new PathFile();
        for (String pathId : pathFile.getAllConfigs().keySet()) {
            pathFiles.put(pathId, pathFile);
            paths.put(pathId, pathFile.getPath(pathId));
        }
    }

    /**
     * 獲取指定ID的路徑
     * @param pathId 路徑ID
     * @return 路徑物件
     */
    public AbstractsPath getPath(String pathId) {
        return paths.get(pathId);
    }

    /**
     * 獲取所有路徑
     * @return 路徑列表
     */
    public Map<String, AbstractsPath> getPaths() {
        return paths;
    }

    /**
     * 保存所有路徑
     */
    @Override
    public void save() {
        for (Map.Entry<String, PathFile> entry : pathFiles.entrySet()) {
            String pathId = entry.getKey();
            PathFile pathFile = entry.getValue();
            PathConfig config = pathFile.getConfig(pathId);
            if (config != null) {
                try {
                    pathFile.savePath(pathId, config);
                } catch (Exception e) {
                    mcRogueLike.getLogger().log(Level.SEVERE, "Error saving path: " + pathId, e);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        save();
        paths.clear();
        pathFiles.clear();
    }

    /**
     * 添加路徑
     * @param pathId 路徑ID
     * @param path 路徑實例
     */
    public void addPath(String pathId, AbstractsPath path) {
        paths.put(pathId, path);
        PathFile pathFile = new PathFile();
        pathFiles.put(pathId, pathFile);
    }

    /**
     * 移除路徑
     * @param pathId 路徑ID
     */
    public void removePath(String pathId) {
        if (paths.containsKey(pathId)) {
            paths.remove(pathId);
            pathFiles.remove(pathId);
        }
    }

    /**
     * 清除所有路徑
     */
    public void clear() {
        paths.clear();
        pathFiles.clear();
    }
} 