package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * 房間文件管理類
 * 用於管理房間配置文件
 */
public class RoomFile extends FileProvider<RoomConfig> {
    private static final String ROOM_DIRECTORY = "Room";
    private final FileProviderList<RoomFile> fileList;
    private RoomConfig config;
    private AbstractsRoom room;

    /**
     * 構造函數
     * @param plugin 插件實例
     * @param roomId 房間ID
     */
    public RoomFile(Plugin plugin, String roomId) {
        super(plugin, roomId + ".yml", ROOM_DIRECTORY);
        this.fileList = new FileProviderList<>(plugin, ROOM_DIRECTORY, ".yml");
        this.fileList.addProvider(roomId, this);
    }

    @Override
    public RoomConfig load() {
        config = new RoomConfig();
        config.setName(yml.getString("name", ""));
        config.setType(yml.getString("type", ""));
        config.setStructure(yml.getString("structure", ""));
        config.setDescription(yml.getString("description", ""));
        config.setTimeLimit(yml.getInt("time_limit", 600));
        config.setBaseScore(yml.getInt("baseScore", 200));
        config.setEarlyCompletionMultiplier(yml.getDouble("early_completion_multiplier", 1.5));
        config.setPlayerSpawn(yml.getString("player_spawn", "0,64,0"));
        
        // 讀取floor部分
        ConfigurationSection floorSection = yml.getConfigurationSection("floor");
        if (floorSection != null) {
            config.setMinFloor(floorSection.getInt("min", 1));
            config.setMaxFloor(floorSection.getInt("max", 10));
        }
        
        // 讀取spawn_points部分
        List<Map<String, String>> spawnPoints = new ArrayList<>();
        List<Map<?, ?>> spawnPointsList = yml.getMapList("spawn_points");
        for (Map<?, ?> spawnPoint : spawnPointsList) {
            Map<String, String> spawnPointMap = new HashMap<>();
            spawnPointMap.put("name", (String) spawnPoint.get("name"));
            spawnPointMap.put("location", (String) spawnPoint.get("location"));
            spawnPoints.add(spawnPointMap);
        }
        config.setSpawnPoints(spawnPoints);
        
        // 將配置轉換成實際的房間物件
        this.room = convertToRoom(config);
        
        // 將房間物件存儲到MCRogueLike的全域變數中
        MCRogueLike.getInstance().getRoomManager().addRoom(fileName.replace(".yml", ""), this.room);
        
        return config;
    }

    /**
     * 將配置轉換成實際的房間物件
     * @param config 房間配置
     * @return 房間物件
     */
    private AbstractsRoom convertToRoom(RoomConfig config) {
        // 創建房間物件 - 簡化版本
        return new Room(
            fileName.replace(".yml", ""),  // 使用文件名作為房間ID
            config.getName(),
            config.getType(),
            config.getStructure(),
            config.getDescription(),
            config.getTimeLimit(),
            config.getBaseScore(),
            config.getEarlyCompletionMultiplier(),
            config.getPlayerSpawn(),
            config.getMinFloor(),
            config.getMaxFloor(),
            config.getSpawnPoints()
        );
    }

    @Override
    public void save(RoomConfig config) {
        yml.set("name", config.getName());
        yml.set("type", config.getType());
        yml.set("structure", config.getStructure());
        yml.set("description", config.getDescription());
        yml.set("time_limit", config.getTimeLimit());
        yml.set("baseScore", config.getBaseScore());
        yml.set("early_completion_multiplier", config.getEarlyCompletionMultiplier());
        yml.set("player_spawn", config.getPlayerSpawn());
        
        // 保存floor部分
        yml.set("floor.min", config.getMinFloor());
        yml.set("floor.max", config.getMaxFloor());
        
        // 保存spawn_points部分
        yml.set("spawn_points", config.getSpawnPoints());
        
        try {
            yml.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving room file: " + file.getName(), e);
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
            plugin.saveResource(ROOM_DIRECTORY + "/" + fileName, false);
        }
    }

    /**
     * 獲取房間配置
     * @return 房間配置
     */
    public RoomConfig getConfig() {
        return config;
    }

    /**
     * 設置房間配置
     * @param config 房間配置
     */
    public void setConfig(RoomConfig config) {
        this.config = config;
    }

    /**
     * 獲取文件列表
     * @return 文件列表
     */
    public FileProviderList<RoomFile> getFileList() {
        return fileList;
    }
}
