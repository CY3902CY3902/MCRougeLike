package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.MapConfig;
import io.github.cy3902.mcroguelike.map.Map;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Level;

/**
 * 地圖文件管理類
 * 用於管理地圖配置文件
 */
public class MapFile extends FileProvider<MapConfig> {
    private static final String MAP_DIRECTORY = "Map";
    private final FileProviderList<MapFile> fileList;
    private MapConfig config;
    private AbstractsMap map;

    /**
     * 構造函數
     * @param plugin 插件實例
     * @param mapId 地圖ID
     */
    public MapFile(Plugin plugin, String mapId) {
        super(plugin, mapId + ".yml", MAP_DIRECTORY);
        this.fileList = new FileProviderList<>(plugin, MAP_DIRECTORY, ".yml");
        this.fileList.addProvider(mapId, this);
    }

    @Override
    public MapConfig load() {
        config = new MapConfig();
        config.setStructureSpawnPoint(yml.getString("structure_spawn_point", "0,64,0"));
        config.setStructureSpawnSeparation(yml.getInt("structure_spawn_separation", 100));
        config.setMobGriefing(yml.getBoolean("game_rules.mob_griefing", false));
        config.setDoDaylightCycle(yml.getBoolean("game_rules.do_daylight_cycle", false));
        config.setDoWeatherCycle(yml.getBoolean("game_rules.do_weather_cycle", false));
        config.setKeepInventory(yml.getBoolean("game_rules.keep_inventory", true));
        config.setDoMobSpawning(yml.getBoolean("game_rules.do_mob_spawning", false));
        config.setPvp(yml.getBoolean("game_rules.pvp", false));
        config.setWeather(yml.getString("environment.weather", "clear"));
        config.setAllowExplosions(yml.getBoolean("environment.allow_explosions", false));
        
        // 將配置轉換成實際的地圖物件
        this.map = convertToMap(config);
        
        // 將地圖物件存儲到MCRogueLike的全域變數中
        MCRogueLike.getInstance().getMapManager().addMap(this.map);
        
        return config;
    }

    /**
     * 將配置轉換成實際的地圖物件
     * @param config 地圖配置
     * @return 地圖物件
     */
    private AbstractsMap convertToMap(MapConfig config) {
        // 創建地圖物件 - 簡化版本
        return new Map(
            fileName.replace(".yml", ""),  // 使用文件名作為地圖ID
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

    @Override
    public void save(MapConfig config) {
        yml.set("structure_spawn_point", config.getStructureSpawnPoint());
        yml.set("structure_spawn_separation", config.getStructureSpawnSeparation());
        yml.set("game_rules.mob_griefing", config.isMobGriefing());
        yml.set("game_rules.do_daylight_cycle", config.isDoDaylightCycle());
        yml.set("game_rules.do_weather_cycle", config.isDoWeatherCycle());
        yml.set("game_rules.keep_inventory", config.isKeepInventory());
        yml.set("game_rules.do_mob_spawning", config.isDoMobSpawning());
        yml.set("game_rules.pvp", config.isPvp());
        yml.set("environment.weather", config.getWeather());
        yml.set("environment.allow_explosions", config.isAllowExplosions());
        try {
            yml.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving map file: " + file.getName(), e);
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
            plugin.saveResource(MAP_DIRECTORY + "/" + fileName, false);
        }
    }

    /**
     * 獲取地圖配置
     * @return 地圖配置
     */
    public MapConfig getConfig() {
        return config;
    }

    /**
     * 設置地圖配置
     * @param config 地圖配置
     */
    public void setConfig(MapConfig config) {
        this.config = config;
    }

    /**
     * 獲取文件列表
     * @return 文件列表
     */
    public FileProviderList<MapFile> getFileList() {
        return fileList;
    }
}
