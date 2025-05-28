package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom.SpawnPoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.room.AnnihilationRoom;
import io.github.cy3902.mcroguelike.room.SurvivalRoom;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

/**
 * 房間文件管理類
 * 用於管理房間配置文件
 */
public class RoomFile extends FileProviderList<FileProvider<RoomConfig>> {
    private static final String ROOM_DIRECTORY = "Room";
    private final java.util.Map<String, RoomConfig> configs = new HashMap<>();
    private final java.util.Map<String, AbstractRoom> rooms = new HashMap<>();
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    /**
     * 構造函數
     */
    public RoomFile() {
        super(ROOM_DIRECTORY, ".yml");
        initializeProviders();
    }

    /**
     * 初始化所有providers
     */
    private void initializeProviders() {
        File roomDir = new File(mcroguelike.getDataFolder(), ROOM_DIRECTORY);
        if (!roomDir.exists()) {
            roomDir.mkdirs();
            return;
        }

        File[] files = roomDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String roomId = file.getName().replace(".yml", "");
                FileProvider<RoomConfig> provider = new FileProvider<RoomConfig>(file.getName(), ROOM_DIRECTORY) {
                    @Override
                    public RoomConfig load() {
                        RoomConfig config = new RoomConfig();
                        config.setRoomId(yml.getName());
                        config.setName(yml.getString("name", ""));
                        config.setType(yml.getString("type", "survival"));
                        config.setStructure(yml.getString("structure", ""));
                        config.setMinFloor(yml.getInt("floor.min", 1));
                        config.setMaxFloor(yml.getInt("floor.max", 1));
                        config.setTimeLimit(yml.getInt("time_limit", 300));
                        config.setBaseScore(yml.getInt("base_score", 100));
                        config.setEarlyCompletionMultiplier(yml.getDouble("early_completion_multiplier", 1.5));
                        config.setPlayerSpawn(yml.getString("player_spawn", "0,64,0"));
                        
                        // 讀取生成點
                        List<SpawnPoint> spawnpoints = new ArrayList<>();
                        if (yml.contains("spawn_points")) {
                            ConfigurationSection spawnpointSection = yml.getConfigurationSection("spawn_points");
                            if (spawnpointSection == null) {
                                return config;
                            }
                            for (String key : spawnpointSection.getKeys(false)) {
                                ConfigurationSection pointSection = spawnpointSection.getConfigurationSection(key);
                                if (pointSection != null) {
                                    if ( mcroguelike.getSpawnpointFile().getSpawnpoint(key) == null) {
                                        continue;
                                    }
                                    AbstractSpawnpoint abstractSpawnpoint = mcroguelike.getSpawnpointFile().getSpawnpoint(key);
                                    SpawnPoint spawnpoint = new SpawnPoint(abstractSpawnpoint, pointSection.getString("location"));
                                    spawnpoints.add(spawnpoint);
                                }
                            }
                        }

                        // 讀取敵人生成點


                        config.setSpawnpoints(spawnpoints);
                        return config;
                    }

                    @Override
                    public void save(RoomConfig config) {
                        yml.set("name", config.getName());
                        yml.set("type", config.getType());
                        yml.set("structure", config.getStructure());
                        yml.set("floor.min", config.getMinFloor());
                        yml.set("floor.max", config.getMaxFloor());
                        yml.set("time_limit", config.getTimeLimit());
                        yml.set("base_score", config.getBaseScore());
                        yml.set("early_completion_multiplier", config.getEarlyCompletionMultiplier());
                        yml.set("player_spawn", config.getPlayerSpawn());

                        yml.set("spawn_points", null);
                        for (SpawnPoint spawnpoint : config.getSpawnpoints()) {
                            yml.set("spawn_points." + spawnpoint.getSpawnpoint().getName() + ".location", spawnpoint.getLocation());
                        }
                        try {
                            yml.save(file);
                        } catch (Exception e) {
                            mcroguelike.getLogger().log(Level.SEVERE, "Error saving room file: " + file.getName(), e);
                        }
                    }
                };
                addProvider(roomId, provider);
                loadRoom(roomId);
            }
        }
    }

    /**
     * 加載指定ID的房間配置
     * @param roomId 房間ID
     * @return 房間配置
     */
    private RoomConfig loadRoom(String roomId) {
        FileProvider<RoomConfig> provider = getProvider(roomId);
        if (provider == null) {
            return null;
        }
        
        RoomConfig config = provider.load();
        configs.put(roomId, config);
        
        // 將配置轉換成實際的房間物件
        AbstractRoom room = convertToRoom(roomId, config);
        rooms.put(roomId, room);
        
        return config;
    }

    /**
     * 刪除指定ID的房間配置
     * @param roomId 房間ID
     */
    public void removeProvider(String roomId) {
        configs.remove(roomId);
        rooms.remove(roomId);
    }

    /**
     * 將配置轉換成實際的房間物件
     * @param roomId 房間ID
     * @param config 房間配置
     * @return 房間物件
     */
    private AbstractRoom convertToRoom(String roomId, RoomConfig config) {
        // 根據房間類型創建對應的房間物件
        switch (config.getType().toLowerCase()) {
            case "annihilation":
                return new AnnihilationRoom(
                    roomId,
                    config.getName(),
                    config.getStructure(),
                    new ArrayList<>(), // 生成點列表將在後續設置
                    config.getMinFloor(),
                    config.getMaxFloor(),
                    config.getTimeLimit(),
                    config.getBaseScore(),
                    config.getEarlyCompletionMultiplier(),
                    config.getPlayerSpawn()
                );
            case "survival":
            default:
                return new SurvivalRoom(
                    roomId,
                    config.getName(),
                    config.getStructure(),
                    config.getSpawnpoints(),
                    config.getMinFloor(),
                    config.getMaxFloor(),
                    config.getTimeLimit(),
                    config.getBaseScore(),
                    config.getPlayerSpawn()
                );
        }
    }

    /**
     * 保存指定ID的房間配置
     * @param roomId 房間ID
     * @param config 房間配置
     */
    public void saveRoom(String roomId, RoomConfig config) {
        FileProvider<RoomConfig> provider = getProvider(roomId);
        if (provider == null) {
            return;
        }
        
        provider.save(config);
        configs.put(roomId, config);
        
        // 更新房間物件
        AbstractRoom room = convertToRoom(roomId, config);
        rooms.put(roomId, room);
    }

    /**
     * 獲取指定ID的房間配置
     * @param roomId 房間ID
     * @return 房間配置
     */
    public RoomConfig getConfig(String roomId) {
        return configs.get(roomId);
    }

    /**
     * 獲取指定ID的房間物件
     * @param roomId 房間ID
     * @return 房間物件
     */
    public AbstractRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }
    
    /**
     * 獲取所有房間配置
     * @return 房間配置列表
     */
    public java.util.Map<String, RoomConfig> getAllConfigs() {
        return configs;
    }
    
    /**
     * 獲取所有房間物件
     * @return 房間物件列表
     */
    public java.util.Map<String, AbstractRoom> getAllRooms() {
        return rooms;
    }
}
