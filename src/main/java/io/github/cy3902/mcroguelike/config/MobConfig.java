package io.github.cy3902.mcroguelike.config;

public class MobConfig {
    private double healthMultiplier;
    private double damageMultiplier;
    private double speedMultiplier;
    private boolean isBoss;
    private boolean isGuardTarget;
    private int count;

    /**
     * 默認構造函數
     */
    public MobConfig() {
        this.healthMultiplier = 0.4;
        this.damageMultiplier = 0.4;
        this.speedMultiplier = 0.4;
        this.isBoss = false;
        this.isGuardTarget = false;
        this.count = 1;
    }

    /**
     * 完整構造函數
     * @param healthMultiplier 生命值倍數
     * @param damageMultiplier 傷害倍數
     * @param speedMultiplier 速度倍數
     * @param isBoss 是否為Boss
     */
    public MobConfig(double healthMultiplier, double damageMultiplier, double speedMultiplier, boolean isBoss, boolean isGuardTarget, int count) {
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.isBoss = isBoss;
        this.isGuardTarget = isGuardTarget;
        this.count = count;
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

    public boolean isGuardTarget() {
        return isGuardTarget;
    }

    public void setGuardTarget(boolean guardTarget) {
        isGuardTarget = guardTarget;
    }

    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}
