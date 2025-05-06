package io.github.cy3902.mcroguelike.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成點配置類
 * 用於存儲生成點的配置信息
 */
public class SpawnpointConfig {
    private int timeWait;
    private int maxSpawnAmount;
    private Map<String, MobConfig> mobs;

    /**
     * 默認構造函數
     */
    public SpawnpointConfig() {
        this.timeWait = 20;
        this.maxSpawnAmount = 10;
        this.mobs = new HashMap<>();
    }

    /**
     * 完整構造函數
     * @param timeWait 等待時間
     * @param maxSpawnAmount 最大生成數量
     * @param mobs 怪物配置
     */
    public SpawnpointConfig(int timeWait, int maxSpawnAmount, Map<String, MobConfig> mobs) {
        this.timeWait = timeWait;
        this.maxSpawnAmount = maxSpawnAmount;
        this.mobs = mobs;
    }

    // Getters and Setters
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

    public Map<String, MobConfig> getMobs() {
        return mobs;
    }

    public void setMobs(Map<String, MobConfig> mobs) {
        this.mobs = mobs;
    }

} 