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

    /**
     * 怪物配置內部類
     */
    public static class MobConfig {
        private double healthMultiplier;
        private double damageMultiplier;
        private double speedMultiplier;
        private boolean isBoss;

        /**
         * 默認構造函數
         */
        public MobConfig() {
            this.healthMultiplier = 0.4;
            this.damageMultiplier = 0.4;
            this.speedMultiplier = 0.4;
            this.isBoss = false;
        }

        /**
         * 完整構造函數
         * @param healthMultiplier 生命值倍數
         * @param damageMultiplier 傷害倍數
         * @param speedMultiplier 速度倍數
         * @param isBoss 是否為Boss
         */
        public MobConfig(double healthMultiplier, double damageMultiplier, double speedMultiplier, boolean isBoss) {
            this.healthMultiplier = healthMultiplier;
            this.damageMultiplier = damageMultiplier;
            this.speedMultiplier = speedMultiplier;
            this.isBoss = isBoss;
        }

        // Getters and Setters
        public double getHealthMultiplier() {
            return healthMultiplier;
        }

        public void setHealthMultiplier(double healthMultiplier) {
            this.healthMultiplier = healthMultiplier;
        }

        public double getDamageMultiplier() {
            return damageMultiplier;
        }

        public void setDamageMultiplier(double damageMultiplier) {
            this.damageMultiplier = damageMultiplier;
        }

        public double getSpeedMultiplier() {
            return speedMultiplier;
        }

        public void setSpeedMultiplier(double speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
        }

        public boolean isBoss() {
            return isBoss;
        }

        public void setBoss(boolean boss) {
            isBoss = boss;
        }
    }
} 