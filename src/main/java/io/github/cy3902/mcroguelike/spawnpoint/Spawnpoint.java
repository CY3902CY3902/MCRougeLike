package io.github.cy3902.mcroguelike.spawnpoint;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractMob;
import java.util.List;

/**
 * Spawnpoint類別負責處理怪物的生成邏輯
 * 繼承自AbstractSpawnpoint抽象類別
 */
public class Spawnpoint extends AbstractSpawnpoint {


    /**
     * 建構子
     * @param name 生成點名稱
     * @param timeWait 生成間隔時間(秒)
     * @param maxSpawnAmount 最大生成總數
     * @param abstractsMobs 可生成的怪物列表
     */
    public Spawnpoint(String name, int timeWait, int maxSpawnAmount, List<AbstractMob> abstractsMobs) {
        super(name, timeWait, maxSpawnAmount, abstractsMobs);
    }

}

