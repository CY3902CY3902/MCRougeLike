package io.github.cy3902.mcroguelike.abstracts;

import org.bukkit.entity.LivingEntity;

/**
 * 抽象怪物類別，定義了怪物的基本屬性和行為
 */
public abstract class AbstractsMob {
    
    protected String mobId;                 // 怪物唯一識別ID
    protected double healthMultiplier;      // 血量加成倍率 (0.0-1.0)
    protected double damageMultiplier;      // 傷害加成倍率 (0.0-1.0)
    protected double speedMultiplier;       // 移動速度加成倍率 (0.0-1.0)
    protected boolean isKeyMob;             // 是否為關鍵怪物(狙殺任務中的目標)
    protected boolean isGuardTarget;        // 是否為需要守護的目標(防守任務中的VIP)
    protected LivingEntity entity;          // 怪物實體

    /**
     * 建構子，初始化怪物屬性
     * @param mobId 怪物ID
     * @param healthMultiplier 血量倍率
     * @param damageMultiplier 傷害倍率
     * @param speedMultiplier 速度倍率
     * @param isKeyMob 是否為關鍵怪物
     * @param isGuardTarget 是否為守護目標
     */
    public AbstractsMob(String mobId, double healthMultiplier, double damageMultiplier, 
                        double speedMultiplier, boolean isKeyMob, boolean isGuardTarget) {
        this.mobId = mobId;
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.isKeyMob = isKeyMob;
        this.isGuardTarget = isGuardTarget;
    }

    /**
     * 取得怪物實體
     * @return 怪物實體
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * 設定怪物實體
     * @param entity 怪物實體
     */
    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * 取得怪物ID
     * @return 怪物ID字串
     */
    public String getMobId() {
        return mobId;
    }

    /**
     * 取得血量倍率
     * @return 血量倍率
     */
    public double getHealthMultiplier() {
        return healthMultiplier;
    }

    /**
     * 取得傷害倍率
     * @return 傷害倍率
     */
    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    /**
     * 取得速度倍率
     * @return 速度倍率
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * 檢查是否為關鍵怪物
     * @return 是否為關鍵怪物
     */
    public boolean isKeyMob() {
        return isKeyMob;
    }

    /**
     * 檢查是否為守護目標
     * @return 是否為守護目標
     */
    public boolean isGuardTarget() {
        return isGuardTarget;
    }

    /**
     * 設定怪物ID
     * @param mobId 新的怪物ID
     */
    public void setMobId(String mobId) {
        this.mobId = mobId;
    }

    /**
     * 設定血量倍率
     * @param healthMultiplier 新的血量倍率
     */
    public void setHealthMultiplier(double healthMultiplier) {
        this.healthMultiplier = healthMultiplier;
    }

    /**
     * 設定傷害倍率
     * @param damageMultiplier 新的傷害倍率
     */
    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    /**
     * 設定速度倍率
     * @param speedMultiplier 新的速度倍率
     */
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * 設定是否為關鍵怪物
     * @param keyMob 是否為關鍵怪物
     */
    public void setKeyMob(boolean keyMob) {
        isKeyMob = keyMob;
    }

    /**
     * 設定是否為守護目標
     * @param guardTarget 是否為守護目標
     */
    public void setGuardTarget(boolean guardTarget) {
        isGuardTarget = guardTarget;
    }
}
