package io.github.cy3902.mcroguelike.mob;

import io.github.cy3902.mcroguelike.config.SpawnpointConfig;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * 生成點抽象類
 * 用於管理生成點的基類
 */
public abstract class AbstractSpawnpoint {
    protected String id;
    protected String name;
    protected Location location;
    protected int timeWait;
    protected int maxSpawnAmount;
    protected List<Entity> spawnedEntities;

    public AbstractSpawnpoint(String id, String name, Location location, int timeWait, int maxSpawnAmount) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.timeWait = timeWait;
        this.maxSpawnAmount = maxSpawnAmount;
    }

    /**
     * 從配置加載生成點
     * @param config 生成點配置
     */
    public abstract void loadFromConfig(SpawnpointConfig config);

    /**
     * 保存生成點到配置
     * @return 生成點配置
     */
    public abstract SpawnpointConfig saveToConfig();

    /**
     * 生成實體
     * @return 生成的實體列表
     */
    public abstract List<Entity> spawn();

    /**
     * 清理生成的實體
     */
    public void cleanup() {
        if (spawnedEntities != null) {
            for (Entity entity : spawnedEntities) {
                entity.remove();
            }
            spawnedEntities.clear();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getTimeWait() {
        return timeWait;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    public int getMaxSpawnAmount() {
        return maxSpawnAmount;
    }

    public void setMaxSpawnAmount(int maxSpawnAmount) {
        this.maxSpawnAmount = maxSpawnAmount;
    }

    public List<Entity> getSpawnedEntities() {
        return spawnedEntities;
    }
} 