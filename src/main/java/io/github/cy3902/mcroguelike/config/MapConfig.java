package io.github.cy3902.mcroguelike.config;

/**
 * 地圖配置類
 * 用於存儲地圖的配置信息
 */
public class MapConfig {
    private String structureSpawnPoint;
    private int structureSpawnSeparation;
    private boolean mobGriefing;
    private boolean doDaylightCycle;
    private boolean doWeatherCycle;
    private boolean keepInventory;
    private boolean doMobSpawning;
    private boolean pvp;
    private String weather;
    private boolean allowExplosions;

    /**
     * 默認構造函數
     */
    public MapConfig() {
        this.structureSpawnPoint = "0,64,0";
        this.structureSpawnSeparation = 100;
        this.mobGriefing = false;
        this.doDaylightCycle = false;
        this.doWeatherCycle = false;
        this.keepInventory = true;
        this.doMobSpawning = false;
        this.pvp = false;
        this.weather = "clear";
        this.allowExplosions = false;
    }

    /**
     * 完整構造函數
     */
    public MapConfig(String structureSpawnPoint, int structureSpawnSeparation,
                    boolean mobGriefing, boolean doDaylightCycle, boolean doWeatherCycle,
                    boolean keepInventory, boolean doMobSpawning, boolean pvp,
                    String weather, boolean allowExplosions) {
        this.structureSpawnPoint = structureSpawnPoint;
        this.structureSpawnSeparation = structureSpawnSeparation;
        this.mobGriefing = mobGriefing;
        this.doDaylightCycle = doDaylightCycle;
        this.doWeatherCycle = doWeatherCycle;
        this.keepInventory = keepInventory;
        this.doMobSpawning = doMobSpawning;
        this.pvp = pvp;
        this.weather = weather;
        this.allowExplosions = allowExplosions;
    }

    // Getters and Setters
    public String getStructureSpawnPoint() {
        return structureSpawnPoint;
    }

    public void setStructureSpawnPoint(String structureSpawnPoint) {
        this.structureSpawnPoint = structureSpawnPoint;
    }

    public int getStructureSpawnSeparation() {
        return structureSpawnSeparation;
    }

    public void setStructureSpawnSeparation(int structureSpawnSeparation) {
        this.structureSpawnSeparation = structureSpawnSeparation;
    }

    public boolean isMobGriefing() {
        return mobGriefing;
    }

    public void setMobGriefing(boolean mobGriefing) {
        this.mobGriefing = mobGriefing;
    }

    public boolean isDoDaylightCycle() {
        return doDaylightCycle;
    }

    public void setDoDaylightCycle(boolean doDaylightCycle) {
        this.doDaylightCycle = doDaylightCycle;
    }

    public boolean isDoWeatherCycle() {
        return doWeatherCycle;
    }

    public void setDoWeatherCycle(boolean doWeatherCycle) {
        this.doWeatherCycle = doWeatherCycle;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean isDoMobSpawning() {
        return doMobSpawning;
    }

    public void setDoMobSpawning(boolean doMobSpawning) {
        this.doMobSpawning = doMobSpawning;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public boolean isAllowExplosions() {
        return allowExplosions;
    }

    public void setAllowExplosions(boolean allowExplosions) {
        this.allowExplosions = allowExplosions;
    }
} 