package io.github.cy3902.mcroguelike.abstracts;

import org.bukkit.entity.LivingEntity;

/**
 * 怪物類
 * 實現了AbstractsMob抽象類
 */
public class Mob extends AbstractsMob {
    /**
     * 建構子，初始化怪物屬性
     * @param mobId 怪物ID
     * @param healthMultiplier 血量倍率
     * @param damageMultiplier 傷害倍率
     * @param speedMultiplier 速度倍率
     * @param isKeyMob 是否為關鍵怪物
     * @param isGuardTarget 是否為守護目標
     */
    public Mob(String mobId, double healthMultiplier, double damageMultiplier, 
               double speedMultiplier, boolean isKeyMob, boolean isGuardTarget) {
        super(mobId, healthMultiplier, damageMultiplier, speedMultiplier, isKeyMob, isGuardTarget);
    }
} 