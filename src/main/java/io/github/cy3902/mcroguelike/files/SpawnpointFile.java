package io.github.cy3902.mcroguelike.files;

import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.FileProviderList;
import io.github.cy3902.mcroguelike.config.MobConfig;
import io.github.cy3902.mcroguelike.config.SpawnpointConfig;
import io.github.cy3902.mcroguelike.spawnpoint.Spawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractMob;
import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.mobs.Mob;
import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.configuration.ConfigurationSection;

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
public class SpawnpointFile extends FileProviderList<FileProvider<SpawnpointConfig>> {
    private static final String SPAWNPOINT_DIRECTORY = "Spawnpoint";
    private final java.util.Map<String, SpawnpointConfig> configs = new HashMap<>();
    private java.util.Map<String, AbstractSpawnpoint> spawnpoints = new HashMap<>();
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    /**
     * 構造函數
     * @param plugin 插件實例
     */
    public SpawnpointFile() {
        super(SPAWNPOINT_DIRECTORY, ".yml");
        initializeProviders();
    }

    /**
     * 初始化所有providers
     */
    private void initializeProviders() {
        File spawnpointDir = new File(mcroguelike.getDataFolder(), SPAWNPOINT_DIRECTORY);
        if (!spawnpointDir.exists()) {
            spawnpointDir.mkdirs();
            return;
        }

        File[] files = spawnpointDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String spawnpointId = file.getName().replace(".yml", "");
                FileProvider<SpawnpointConfig> provider = new FileProvider<SpawnpointConfig>(file.getName(), SPAWNPOINT_DIRECTORY) {
                    @Override
                    public SpawnpointConfig load() {
                        SpawnpointConfig spawnpointConfig = new SpawnpointConfig();

                        // 讀取基本屬性
                        spawnpointConfig.setTimeWait(yml.getInt("time_wait", 20));
                        spawnpointConfig.setMaxSpawnAmount(yml.getInt("max_spawn_amount", 10));

                        // 讀取怪物配置
                        ConfigurationSection mobsSection = yml.getConfigurationSection("mobs");
                        if (mobsSection != null) {
                            Map<String, MobConfig> mobs = new HashMap<>();
                            for (String mobType : mobsSection.getKeys(false)) {
                                ConfigurationSection mobSection = mobsSection.getConfigurationSection(mobType);
                                if (mobSection != null) {
                                    MobConfig mobConfig = new MobConfig(
                                        mobSection.getDouble("health_multiplier", 0.4),
                                        mobSection.getDouble("damage_multiplier", 0.4),
                                        mobSection.getDouble("speed_multiplier", 0.4),
                                        mobSection.getBoolean("is_boss", false),
                                        mobSection.getBoolean("is_guard_target", false),
                                        mobSection.getInt("count", 1)
                                    );
                                    mobs.put(mobType, mobConfig);
                                }
                            }
                            spawnpointConfig.setMobs(mobs);
                        }

                        return spawnpointConfig;
                    }

                    @Override
                    public void save(SpawnpointConfig config) {
                        // 保存基本屬性
                        yml.set("time_wait", config.getTimeWait());
                        yml.set("max_spawn_amount", config.getMaxSpawnAmount());

                        // 保存怪物配置
                        yml.set("mobs", null); // 清空現有的怪物配置
                        ConfigurationSection mobsSection = yml.createSection("mobs");
                        for (Map.Entry<String, MobConfig> entry : config.getMobs().entrySet()) {
                            ConfigurationSection mobSection = mobsSection.createSection(entry.getKey());
                            MobConfig mobConfig = entry.getValue();
                            mobSection.set("health_multiplier", mobConfig.getHealthMultiplier());
                            mobSection.set("damage_multiplier", mobConfig.getDamageMultiplier());
                            mobSection.set("speed_multiplier", mobConfig.getSpeedMultiplier());
                            mobSection.set("is_boss", mobConfig.isBoss());
                            mobSection.set("is_guard_target", mobConfig.isGuardTarget());
                            mobSection.set("count", mobConfig.getCount());
                        }

                        try {
                            yml.save(file);
                        } catch (IOException e) {
                            mcroguelike.getLogger().log(Level.SEVERE, "Could not save spawnpoint config to " + file, e);
                        }
                    }
                };
                addProvider(spawnpointId, provider);
                loadSpawnpoint(spawnpointId);
                mcroguelike.getSpawnpointRegister().put(spawnpointId, convertToSpawnpoint(spawnpointId, configs.get(spawnpointId)).getClass());
                setSpawnpoint(convertToSpawnpoint(spawnpointId, configs.get(spawnpointId)));
            }
        }
    }

    /**
     * 加載指定ID的生成點配置
     * @param spawnpointId 生成點ID
     * @return 生成點配置
     */
    private SpawnpointConfig loadSpawnpoint(String spawnpointId) {
        FileProvider<SpawnpointConfig> provider = getProvider(spawnpointId);
        if (provider == null) {
            return null;
        }
        
        SpawnpointConfig config = provider.load();
        configs.put(spawnpointId, config);
        
        
        return config;
    }

    /**
     * 刪除指定ID的生成點配置
     * @param spawnpointId 生成點ID
     */
    public void removeProvider(String spawnpointId) {
        removeProvider(spawnpointId);
        configs.remove(spawnpointId);
    }
    
    /**
     * 將配置轉換成實際的生成點物件
     * @param spawnpointId 生成點ID
     * @param config 生成點配置
     * @return 生成點物件
     */
    private AbstractSpawnpoint convertToSpawnpoint(String spawnpointId, SpawnpointConfig config) {
        List<AbstractMob> mobs = new ArrayList<>();
        for (Map.Entry<String, MobConfig> entry : config.getMobs().entrySet()) {
            String mobType = entry.getKey();
            MobConfig mobConfig = entry.getValue();
            
            // 創建怪物實例
            mobs.add(new Mob(
                mobType,
                mobConfig.getHealthMultiplier(),
                mobConfig.getDamageMultiplier(),
                mobConfig.getSpeedMultiplier(),
                mobConfig.isBoss(),  // 使用isBoss作為isKeyMob
                mobConfig.isGuardTarget(),
                mobConfig.getCount()
            ));
            
        }

        // 創建生成點物件
        return new Spawnpoint(
            spawnpointId,
            config.getTimeWait(),
            config.getMaxSpawnAmount(),
            mobs
        );
    }

    /**
     * 保存指定ID的生成點配置
     * @param spawnpointId 生成點ID
     * @param config 生成點配置
     */
    public void saveSpawnpoint(String spawnpointId, SpawnpointConfig config) {
        FileProvider<SpawnpointConfig> provider = getProvider(spawnpointId);
        if (provider == null) {
            return;
        }
        
        provider.save(config);
        configs.put(spawnpointId, config);
        
    }

    /**
     * 獲取指定ID的生成點配置
     * @param spawnpointId 生成點ID
     * @return 生成點配置
     */
    public SpawnpointConfig getConfig(String spawnpointId) {
        return configs.get(spawnpointId);
    }

    /**
     * 獲取指定ID的生成點物件
     * @param spawnpointId 生成點ID
     * @return 生成點物件
     */
    public AbstractSpawnpoint getSpawnpoint(String spawnpointId) {
        return spawnpoints.get(spawnpointId);
    }

    

    /**
     * 設置生成點
     * @param spawnpoint 生成點
     * @return 生成點
     */
    public AbstractSpawnpoint setSpawnpoint(AbstractSpawnpoint spawnpoint) {
        spawnpoints.put(spawnpoint.getName(), spawnpoint);
        return spawnpoint;
    }



    /**
     * 獲取所有生成點配置
     * @return 生成點配置列表
     */
    public java.util.Map<String, SpawnpointConfig> getAllConfigs() {
        return configs;
    }
    
} 