package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.PathConfig;
import io.github.cy3902.mcroguelike.path.Path;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * 路徑文件管理類
 * 用於管理路徑配置文件
 */
public class PathFile extends FileProviderList<FileProvider<PathConfig>> {
    private static final String PATH_DIRECTORY = "Path";
    private final java.util.Map<String, PathConfig> configs = new HashMap<>();
    private final java.util.Map<String, AbstractsPath> paths = new HashMap<>();
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    /**
     * 構造函數
     */
    public PathFile() {
        super(PATH_DIRECTORY, ".yml");
        initializeProviders();
    }

    /**
     * 初始化所有providers
     */
    private void initializeProviders() {
        File pathDir = new File(mcroguelike.getDataFolder(), PATH_DIRECTORY);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
            return;
        }

        File[] files = pathDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String pathId = file.getName().replace(".yml", "");
                FileProvider<PathConfig> provider = new FileProvider<PathConfig>(file.getName(), PATH_DIRECTORY) {
                    @Override
                    public PathConfig load() {
                        PathConfig config = new PathConfig();
                        config.setPathId(yml.getString("path_id", ""));
                        config.setName(yml.getString("name", ""));
                        config.setDescription(yml.getString("description", ""));
                        config.setMinFloor(yml.getInt("floor.min", 1));
                        config.setMaxFloor(yml.getInt("floor.max", 1));
                        
                        // 讀取新增的屬性
                        config.setMaxNodes(yml.getInt("max_nodes", 10));
                        config.setMaxBranches(yml.getInt("max_branches", 3));
                        config.setMaxHeight(yml.getInt("max_height", 5));
                        config.setSpecialNodeProbability(yml.getDouble("special_node_probability", 0.2));
                        
                        // 讀取房間名稱列表
                        List<String> roomNames = yml.getStringList("room_names");
                        config.setRoomNames(roomNames);
                        
                        // 讀取Boss房間名稱列表
                        List<String> bossRoomNames = yml.getStringList("boss_room_names");
                        config.setBossRoomNames(bossRoomNames);
                        
                        // 讀取地圖名稱
                        config.setMapName(yml.getString("map_name", ""));
                        
                        return config;
                    }

                    @Override
                    public void save(PathConfig config) {
                        yml.set("path_id", config.getPathId());
                        yml.set("name", config.getName());
                        yml.set("description", config.getDescription());
                        yml.set("floor.min", config.getMinFloor());
                        yml.set("floor.max", config.getMaxFloor());
                        
                        // 保存新增的屬性
                        yml.set("max_nodes", config.getMaxNodes());
                        yml.set("max_branches", config.getMaxBranches());
                        yml.set("max_height", config.getMaxHeight());
                        yml.set("special_node_probability", config.getSpecialNodeProbability());
                        
                        // 保存房間名稱列表
                        yml.set("room_names", config.getRoomNames());
                        
                        // 保存Boss房間名稱列表
                        yml.set("boss_room_names", config.getBossRoomNames());
                        
                        // 保存地圖名稱
                        yml.set("map_name", config.getMapName());
                        
                        try {
                            yml.save(file);
                        } catch (Exception e) {
                            mcroguelike.getLogger().log(Level.SEVERE, "Error saving path file: " + file.getName(), e);
                        }
                    }
                };
                addProvider(pathId, provider);
                loadPath(pathId);
            }
        }
    }

    /**
     * 加載指定ID的路徑配置
     * @param pathId 路徑ID
     * @return 路徑配置
     */
    public PathConfig loadPath(String pathId) {
        FileProvider<PathConfig> provider = getProvider(pathId);
        if (provider == null) {
            return null;
        }
        
        PathConfig config = provider.load();
        configs.put(pathId, config);
        
        // 將配置轉換成實際的路徑物件
        AbstractsPath path = convertToPath(pathId, config);
        paths.put(pathId, path);
        
        
        return config;
    }

    /**
     * 刪除指定ID的路徑配置
     * @param pathId 路徑ID
     */
    public void removeProvider(String pathId) {
        removeProvider(pathId);
        configs.remove(pathId);
        paths.remove(pathId);
    }
    
    /**
     * 將配置轉換成實際的路徑物件
     * @param pathId 路徑ID
     * @param config 路徑配置
     * @return 路徑物件
     */
    private AbstractsPath convertToPath(String pathId, PathConfig config) {
        // 設置地圖
        AbstractsMap map = MCRogueLike.getInstance().getMapFile().getMap(config.getMapName());
        if (map == null) {
            mcroguelike.getLogger().warning("Map not found: " + config.getMapName());
        }
        
        // 創建路徑物件
        return new Path(
            pathId,
            config.getName(),
            map,
            config.getMaxNodes(),
            config.getMaxBranches(),
            config.getMaxHeight(),
            config.getSpecialNodeProbability(),
            config.getRoomNames(),
            config.getBossRoomNames()
        );
    }

    /**
     * 保存指定ID的路徑配置
     * @param pathId 路徑ID
     * @param config 路徑配置
     */
    public void savePath(String pathId, PathConfig config) {
        FileProvider<PathConfig> provider = getProvider(pathId);
        if (provider == null) {
            return;
        }
        
        provider.save(config);
        configs.put(pathId, config);
        
        // 更新路徑物件
        AbstractsPath path = convertToPath(pathId, config);
        paths.put(pathId, path);
        
    }

    /**
     * 獲取指定ID的路徑配置
     * @param pathId 路徑ID
     * @return 路徑配置
     */
    public PathConfig getConfig(String pathId) {
        return configs.get(pathId);
    }

    /**
     * 獲取指定ID的路徑物件
     * @param pathId 路徑ID
     * @return 路徑物件
     */
    public AbstractsPath getPath(String pathId) {
        return paths.get(pathId);
    }
    
    /**
     * 獲取所有路徑配置
     * @return 路徑配置列表
     */
    public java.util.Map<String, PathConfig> getAllConfigs() {
        return configs;
    }
    
    /**
     * 獲取所有路徑物件
     * @return 路徑物件列表
     */
    public java.util.Map<String, AbstractsPath> getAllPaths() {
        return paths;
    }
} 