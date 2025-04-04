package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.PathConfig;
import io.github.cy3902.mcroguelike.path.Path;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * 路徑文件管理類
 * 用於管理路徑配置文件
 */
public class PathFile extends FileProvider<PathConfig> {
    private static final String PATH_DIRECTORY = "Path";
    private final FileProviderList<PathFile> fileList;
    private PathConfig config;
    private AbstractsPath path;

    /**
     * 構造函數
     * @param plugin 插件實例
     * @param pathId 路徑ID
     */
    public PathFile(Plugin plugin, String pathId) {
        super(plugin, pathId + ".yml", PATH_DIRECTORY);
        this.fileList = new FileProviderList<>(plugin, PATH_DIRECTORY, ".yml");
        this.fileList.addProvider(pathId, this);
    }

    @Override
    public PathConfig load() {
        config = new PathConfig();
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
        
        // 將配置轉換成實際的路徑物件
        this.path = convertToPath(config);
        
        // 將路徑物件存儲到MCRogueLike的全域變數中
        MCRogueLike.getInstance().getPathManager().addPath(this.path);
        
        return config;
    }

    /**
     * 將配置轉換成實際的路徑物件
     * @param config 路徑配置
     * @return 路徑物件
     */
    private AbstractsPath convertToPath(PathConfig config) {
        // 創建路徑物件 - 簡化版本
        return new Path(
            fileName.replace(".yml", ""),  // 使用文件名作為路徑ID
            config.getName(),
            config.getMaxNodes(),
            config.getMaxBranches(),
            config.getMaxHeight(),
            config.getSpecialNodeProbability(),
            config.getRoomNames(),
            config.getBossRoomNames()
        );
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
        
        try {
            yml.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving path file: " + file.getName(), e);
        }
    }

    @Override
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(file);
        load();
    }

    @Override
    public void readDefault() {
        if (!file.exists()) {
            plugin.saveResource(PATH_DIRECTORY + "/" + fileName, false);
        }
    }

    /**
     * 獲取路徑配置
     * @return 路徑配置
     */
    public PathConfig getConfig() {
        return config;
    }

    /**
     * 設置路徑配置
     * @param config 路徑配置
     */
    public void setConfig(PathConfig config) {
        this.config = config;
        // 更新路徑物件
        this.path = convertToPath(config);
        // 更新MCRogueLike中的路徑物件
        MCRogueLike.getInstance().getPathManager().addPath(this.path);
    }

    /**
     * 獲取文件列表
     * @return 文件列表
     */
    public FileProviderList<PathFile> getFileList() {
        return fileList;
    }
    
    /**
     * 獲取路徑物件
     * @return 路徑物件
     */
    public AbstractsPath getPath() {
        return path;
    }
} 