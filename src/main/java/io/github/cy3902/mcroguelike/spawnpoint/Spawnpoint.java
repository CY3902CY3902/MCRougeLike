package io.github.cy3902.mcroguelike.spawnpoint;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawnpoint類別負責處理怪物的生成邏輯
 * 繼承自AbstractSpawnpoint抽象類別
 */
public class Spawnpoint extends AbstractSpawnpoint {

    private int spawnAmount;
    private int taskId = -1;
    private boolean isPaused = false;
    private List<AbstractsMob> mobs = new ArrayList<>();

    /**
     * 建構子
     * @param name 生成點名稱
     * @param timeWait 生成間隔時間(秒)
     * @param maxSpawnAmount 最大生成總數
     * @param abstractsMobs 可生成的怪物列表
     */
    public Spawnpoint(String name, int timeWait, int maxSpawnAmount, List<AbstractsMob> abstractsMobs) {
        super(name, timeWait, maxSpawnAmount, abstractsMobs);
    }

    /**
     * 在指定位置生成一個隨機怪物
     * @param location 生成位置
     */
    @Override
    public void spawnMob(Location location) {
        if (abstractsMobs.isEmpty()) {
            return;
        }
        
        int randomIndex = (int)(Math.random() * abstractsMobs.size());
        AbstractsMob mobToSpawn = abstractsMobs.get(randomIndex);
        mobs.add(mobToSpawn);
        incrementSpawnCount();
    }

    /**
     * 開始定期生成怪物
     * @param location 生成位置
     * @param amount 每次生成的數量
     */
    public void startSpawning(Location location, int amount) {
        this.spawnAmount = amount;
        this.isPaused = false;
        
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
            MCRogueLike.getInstance(),
            () -> {
                if (isPaused) {
                    return;
                }
                
                int remainingSpawns = maxSpawnAmount - spawnCount;
                if (remainingSpawns <= 0) {
                    stopSpawning();
                    return;
                }
                
                int spawnThisTime = Math.min(spawnAmount, remainingSpawns);
                for (int i = 0; i < spawnThisTime; i++) {
                    spawnMob(location);
                }
            },
            0L,
            timeWait * 20L
        );
    }

    /**
     * 暫停生成怪物
     */
    public void pauseSpawning() {
        this.isPaused = true;
    }

    /**
     * 恢復生成怪物
     */
    public void resumeSpawning() {
        this.isPaused = false;
    }

    /**
     * 取得生成點名稱
     * @return 生成點名稱
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * 停止生成怪物
     */
    public void stopSpawning() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    

}

