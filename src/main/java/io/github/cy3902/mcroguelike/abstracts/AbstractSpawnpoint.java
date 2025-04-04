package io.github.cy3902.mcroguelike.abstracts;

import io.github.cy3902.mcroguelike.manager.spawn.SpawnPointManager;
import org.bukkit.Location;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.entity.LivingEntity;

/**
 * 生成點抽象類別
 * 定義了怪物生成點的基本屬性和行為
 */
public abstract class AbstractSpawnpoint implements SpawnPointManager {

    protected List<AbstractsMob> mobs;
    protected int timeWait;
    protected int maxSpawnAmount; 
    protected int spawnCount;
    protected List<AbstractsMob> abstractsMobs;
    protected final String name;
    protected int currentSpawns;
    protected boolean isSpawning;
    
    public AbstractSpawnpoint(String name, int timeWait, int maxSpawnAmount, List<AbstractsMob> abstractsMobs) {
        this.name = name;
        this.mobs = new ArrayList<>();
        this.timeWait = timeWait;
        this.maxSpawnAmount = maxSpawnAmount;
        this.spawnCount = 0;
        this.abstractsMobs = abstractsMobs;
        this.currentSpawns = 0;
        this.isSpawning = true;
    }

    // Abstract method for spawning mobs
    public abstract void spawnMob(Location location);

    // Getters
    public List<AbstractsMob> getMobs() {
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

    public List<AbstractsMob> getAbstractsMobs() {
        return abstractsMobs;
    }

    // Setters
    public void setMobs(List<AbstractsMob> mobs) {
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

    public void setAbstractsMobs(List<AbstractsMob> abstractsMobs) {
        this.abstractsMobs = abstractsMobs;
    }

    public void incrementSpawnCount() {
        this.spawnCount++;
    }


    public abstract Object getName();

    /**
     * 生成敵人
     * @return 生成的敵人實體
     */
    @Override
    public LivingEntity spawn(Location location) {
        spawnMob(location);
        incrementSpawnCount();
        return mobs.get(mobs.size() - 1).getEntity();
    }


    @Override
    public void stopSpawning() {
        isSpawning = false;
    }

    @Override
    public void reset() {
        currentSpawns = 0;
        isSpawning = true;
    }


    public int getCurrentSpawns() {
        return currentSpawns;
    }

    public boolean isSpawning() {
        return isSpawning;
    }
}

