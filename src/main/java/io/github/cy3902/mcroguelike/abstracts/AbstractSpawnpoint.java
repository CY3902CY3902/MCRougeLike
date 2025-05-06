package io.github.cy3902.mcroguelike.abstracts;

import org.bukkit.Location;
import java.util.List;

/**
 * 生成點抽象類別
 * 定義了怪物生成點的基本屬性和行為
 */
public abstract class AbstractSpawnpoint {

    protected List<AbstractMob> mobs;
    protected int timeWait;
    protected int maxSpawnAmount; 
    protected int spawnCount;
    protected final String name;
    protected int currentSpawns;
    protected boolean isSpawning;
    
    public AbstractSpawnpoint(String name, int timeWait, int maxSpawnAmount, List<AbstractMob> abstractsMobs) {
        this.name = name;
        this.mobs = abstractsMobs;
        this.timeWait = timeWait;
        this.maxSpawnAmount = maxSpawnAmount;
        this.spawnCount = 0;
        this.currentSpawns = 0;
        this.isSpawning = true;
    }

    /**
     * 檢查是否可以在指定位置生成怪物
     * @param location 生成位置
     * @return 是否可以生成
     */
    public boolean canSpawn(Location location) {
        return isSpawning && currentSpawns < maxSpawnAmount;
    }

    /**
     * 停止生成怪物
     */
    public void stopSpawning() {
        isSpawning = false;
    }

    /**
     * 重置生成點狀態
     */
    public void reset() {
        isSpawning = true;
        currentSpawns = 0;
        spawnCount = 0;
    }

    // Getters
    public List<AbstractMob> getMobs() {
        return mobs;
    }

    public int getTimeWait() {
        return timeWait;
    }

    public int getMaxSpawnAmount() {
        return maxSpawnAmount;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public int getCurrentSpawns() {
        return currentSpawns;
    }

    public void incrementCurrentSpawns() {
        currentSpawns++;
        spawnCount++;
    }

    // Setters
    public void setMobs(List<AbstractMob> mobs) {
        this.mobs = mobs;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    public void setMaxSpawnAmount(int maxSpawnAmount) {
        this.maxSpawnAmount = maxSpawnAmount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }
}

