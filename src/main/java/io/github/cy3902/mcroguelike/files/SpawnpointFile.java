package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.SpawnpointConfig;
import io.github.cy3902.mcroguelike.spawnpoint.Spawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMob;
import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.Mob;
import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成點文件管理類
 * 用於讀取和保存生成點配置文件
 */
public class SpawnpointFile extends FileProvider<SpawnpointConfig> {
    private static final String SPAWNPOINT_DIRECTORY = "spawnpoints";
    private final FileProviderList<SpawnpointFile> fileList;
    private SpawnpointConfig configObj;
    private AbstractSpawnpoint spawnpoint;

    /**
     * 構造函數
     * @param plugin 插件實例
     * @param fileName 文件名稱
     */
    public SpawnpointFile(Plugin plugin, String fileName) {
        super(plugin, fileName, SPAWNPOINT_DIRECTORY);
        this.fileList = new FileProviderList<>(plugin, SPAWNPOINT_DIRECTORY, ".yml");
        this.fileList.addProvider(fileName, this);
    }

    @Override
    public SpawnpointConfig load() {
        SpawnpointConfig spawnpointConfig = new SpawnpointConfig();

        // 讀取基本屬性
        spawnpointConfig.setTimeWait(yml.getInt("time_wait", 20));
        spawnpointConfig.setMaxSpawnAmount(yml.getInt("max_spawn_amount", 10));

        // 讀取怪物配置
        ConfigurationSection mobsSection = yml.getConfigurationSection("mobs");
        if (mobsSection != null) {
            Map<String, SpawnpointConfig.MobConfig> mobs = new HashMap<>();
            for (String mobType : mobsSection.getKeys(false)) {
                ConfigurationSection mobSection = mobsSection.getConfigurationSection(mobType);
                if (mobSection != null) {
                    SpawnpointConfig.MobConfig mobConfig = new SpawnpointConfig.MobConfig(
                        mobSection.getDouble("health_multiplier", 0.4),
                        mobSection.getDouble("damage_multiplier", 0.4),
                        mobSection.getDouble("speed_multiplier", 0.4),
                        mobSection.getBoolean("is_boss", false)
                    );
                    mobs.put(mobType, mobConfig);
                }
            }
            spawnpointConfig.setMobs(mobs);
        }

        this.configObj = spawnpointConfig;
        // 將配置轉換成實際的生成點物件
        this.spawnpoint = convertToSpawnpoint(spawnpointConfig);
        
        // 將生成點物件存儲到MCRogueLike的全域變數中
        MCRogueLike.addSpawnPoint(fileName, this.spawnpoint);
        
        return spawnpointConfig;
    }

    /**
     * 將配置轉換成實際的生成點物件
     * @param config 生成點配置
     * @return 生成點物件
     */
    private AbstractSpawnpoint convertToSpawnpoint(SpawnpointConfig config) {
        List<AbstractsMob> mobs = new ArrayList<>();
        for (Map.Entry<String, SpawnpointConfig.MobConfig> entry : config.getMobs().entrySet()) {
            String mobType = entry.getKey();
            SpawnpointConfig.MobConfig mobConfig = entry.getValue();
            
            // 創建怪物實例
            mobs.add(new Mob(
                mobType,
                mobConfig.getHealthMultiplier(),
                mobConfig.getDamageMultiplier(),
                mobConfig.getSpeedMultiplier(),
                mobConfig.isBoss(),  // 使用isBoss作為isKeyMob
                false  // 暫時不使用isGuardTarget
            ));
        }

        // 創建生成點物件 - 簡化版本
        return new Spawnpoint(
            fileName,  // 使用文件名作為生成點名稱
            config.getTimeWait(),
            config.getMaxSpawnAmount(),
            mobs
        );
    }

    @Override
    public void save(SpawnpointConfig config) {
        // 保存基本屬性
        yml.set("time_wait", config.getTimeWait());
        yml.set("max_spawn_amount", config.getMaxSpawnAmount());

        // 保存怪物配置
        ConfigurationSection mobsSection = yml.createSection("mobs");
        for (Map.Entry<String, SpawnpointConfig.MobConfig> entry : config.getMobs().entrySet()) {
            ConfigurationSection mobSection = mobsSection.createSection(entry.getKey());
            SpawnpointConfig.MobConfig mobConfig = entry.getValue();
            mobSection.set("health_multiplier", mobConfig.getHealthMultiplier());
            mobSection.set("damage_multiplier", mobConfig.getDamageMultiplier());
            mobSection.set("speed_multiplier", mobConfig.getSpeedMultiplier());
            mobSection.set("is_boss", mobConfig.isBoss());
        }

        try {
            yml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save spawnpoint config to " + file, e);
        }
    }

    /**
     * 獲取文件列表
     * @return 文件列表
     */
    public FileProviderList<SpawnpointFile> getFileList() {
        return fileList;
    }

    /**
     * 獲取生成點配置
     * @return 生成點配置
     */
    public SpawnpointConfig getConfigObj() {
        return configObj;
    }

    /**
     * 設置生成點配置
     * @param config 生成點配置
     */
    public void setConfigObj(SpawnpointConfig configObj) {
        this.configObj = configObj;
        // 更新生成點物件
        this.spawnpoint = convertToSpawnpoint(configObj);
        // 更新MCRogueLike中的生成點物件
        MCRogueLike.addSpawnPoint(fileName, this.spawnpoint);
    }

    /**
     * 獲取生成點物件
     * @return 生成點物件
     */
    public AbstractSpawnpoint getSpawnpoint() {
        return spawnpoint;
    }
} 