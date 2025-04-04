package io.github.cy3902.mcroguelike.manager.spawn;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * 生成點管理器介面
 * 負責管理怪物的生成
 */
public interface SpawnPointManager {
    /**
     * 生成怪物
     * @param location 生成位置
     * @return 生成的怪物實體
     */
    LivingEntity spawn(Location location);

    /**
     * 停止生成
     */
    void stopSpawning();


    /**
     * 重置生成點
     */
    void reset();
} 