package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.MapConfig;
import io.github.cy3902.mcroguelike.map.Map;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * 地圖文件管理類
 * 用於管理地圖配置文件
 */
public class MapFile extends FileProviderList<FileProvider<MapConfig>> {
    private static final String MAP_DIRECTORY = "Map";
    private final java.util.Map<String, MapConfig> configs = new HashMap<>();
    private final java.util.Map<String, AbstractMap> maps = new HashMap<>();
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    /**
     * 構造函數
     * 
     */
    public MapFile() {
        super( MAP_DIRECTORY, ".yml");
        initializeProviders();
    }

    /**
     * 初始化所有providers
     */
    private void initializeProviders() {
        File mapDir = new File(mcroguelike.getDataFolder(), MAP_DIRECTORY);
        if (!mapDir.exists()) {
            mapDir.mkdirs();
            return;
        }

        File[] files = mapDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String mapId = file.getName().replace(".yml", "");
                FileProvider<MapConfig> provider = new FileProvider<MapConfig>( file.getName(), MAP_DIRECTORY) {
                    @Override
                    public MapConfig load() {
                        MapConfig config = new MapConfig();
                        // 從YAML讀取配置
                        config.setStructureSpawnPoint(yml.getString("structure_spawn_point", "0,64,0"));
                        config.setStructureSpawnSeparation(yml.getInt("structure_spawn_separation", 100));
                        config.setMobGriefing(yml.getBoolean("mob_griefing", false));
                        config.setDoDaylightCycle(yml.getBoolean("do_daylight_cycle", false));
                        config.setDoWeatherCycle(yml.getBoolean("do_weather_cycle", false));
                        config.setKeepInventory(yml.getBoolean("keep_inventory", true));
                        config.setDoMobSpawning(yml.getBoolean("do_mob_spawning", false));
                        config.setPvp(yml.getBoolean("pvp", false));
                        config.setWeather(yml.getString("weather", "clear"));
                        config.setAllowExplosions(yml.getBoolean("allow_explosions", false));
                        return config;
                    }

                    @Override
                    public void save(MapConfig config) {
                        yml.set("structure_spawn_point", config.getStructureSpawnPoint());
                        yml.set("structure_spawn_separation", config.getStructureSpawnSeparation());
                        yml.set("mob_griefing", config.isMobGriefing());
                        yml.set("do_daylight_cycle", config.isDoDaylightCycle());
                        yml.set("do_weather_cycle", config.isDoWeatherCycle());
                        yml.set("keep_inventory", config.isKeepInventory());
                        yml.set("do_mob_spawning", config.isDoMobSpawning());
                        yml.set("pvp", config.isPvp());
                        yml.set("weather", config.getWeather());
                        yml.set("allow_explosions", config.isAllowExplosions());
                        try {
                            yml.save(file);
                        } catch (Exception e) {
                            mcroguelike.getLogger().log(Level.SEVERE, "Error saving map file: " + file.getName(), e);
                        }
                    }
                };
                addProvider(mapId, provider);
                loadMap(mapId);
            }
        }
    }

    /**
     * 加載指定ID的地圖配置
     * @param mapId 地圖ID
     * @return 地圖配置
     */
    private MapConfig loadMap(String mapId) {
        FileProvider<MapConfig> provider = getProvider(mapId);
        if (provider == null) {
            return null;
        }
        
        MapConfig config = provider.load();
        configs.put(mapId, config);
        
        // 將配置轉換成實際的地圖物件
        AbstractMap map = convertToMap(mapId, config);
        maps.put(mapId, map);
        
        
        return config;
    }

    /**
     * 刪除指定ID的地圖配置
     * @param mapId 地圖ID
     */
    public void removeProvider(String mapId) {
        removeProvider(mapId);
        configs.remove(mapId);
        maps.remove(mapId);
    }
    
    /**
     * 將配置轉換成實際的地圖物件
     * @param mapId 地圖ID
     * @param config 地圖配置
     * @return 地圖物件
     */
    private AbstractMap convertToMap(String mapId, MapConfig config) {
        // 創建地圖物件 - 簡化版本
        return new Map(
            mapId,  // 使用地圖ID
            config.getStructureSpawnPoint(),
            config.getStructureSpawnSeparation(),
            config.isMobGriefing(),
            config.isDoDaylightCycle(),
            config.isDoWeatherCycle(),
            config.isKeepInventory(),
            config.isDoMobSpawning(),
            config.isPvp(),
            config.getWeather(),
            config.isAllowExplosions()
        );
    }

    /**
     * 保存指定ID的地圖配置
     * @param mapId 地圖ID
     * @param config 地圖配置
     */
    public void saveMap(String mapId, MapConfig config) {
        FileProvider<MapConfig> provider = getProvider(mapId);
        if (provider == null) {
            return;
        }
        
        provider.save(config);
        configs.put(mapId, config);
        
        // 更新地圖物件
        AbstractMap map = convertToMap(mapId, config);
        maps.put(mapId, map);
        
    }

    /**
     * 獲取指定ID的地圖配置
     * @param mapId 地圖ID
     * @return 地圖配置
     */
    public MapConfig getConfig(String mapId) {
        return configs.get(mapId);
    }

    /**
     * 獲取指定ID的地圖物件
     * @param mapId 地圖ID
     * @return 地圖物件
     */
    public AbstractMap getMap(String mapId) {
        return maps.get(mapId);
    }
    
    /**
     * 獲取所有地圖配置
     * @return 地圖配置列表
     */
    public java.util.Map<String, MapConfig> getAllConfigs() {
        return configs;
    }
    
    /**
     * 獲取所有地圖物件
     * @return 地圖物件列表
     */
    public java.util.Map<String, AbstractMap> getAllMaps() {
        return maps;
    }
}
