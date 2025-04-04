package io.github.cy3902.mcroguelike.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.World;

import io.github.cy3902.mcroguelike.map.MapLocation;
import io.github.cy3902.mcroguelike.utils.LocationUtils;

import org.bukkit.GameRule;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractsMap 是抽象地圖類別，用於管理遊戲地圖的各種設定和規則
 */
public class AbstractsMap {
    private static final List<AbstractsMap> maps = new ArrayList<>();
    protected String name;                // 地圖名稱
    protected Location structureSpawnPoint;          // 出生點
    protected int structureSpawnSeparation;     // 結構物生成間隔
    protected boolean mobGriefing;        // 生物破壞
    protected boolean doDaylightCycle;    // 日夜循環
    protected boolean doWeatherCycle;     // 天氣循環
    protected boolean keepInventory;      // 保留物品欄
    protected boolean doMobSpawning;      // 生物生成
    protected boolean pvp;                // PVP開關
    protected String weather;             // 天氣狀態
    protected boolean allowExplosions;    // 允許爆炸
    protected MapLocation mapLocation;  // 地圖位置

    /**
     * 建構子，初始化地圖的所有設定
     * @param name 地圖名稱
     * @param spawnPoint 出生點
     * @param mobGriefing 生物破壞設定
     * @param doDaylightCycle 日夜循環設定
     * @param doWeatherCycle 天氣循環設定
     * @param keepInventory 死亡保留物品設定
     * @param doMobSpawning 生物生成設定
     * @param pvp PVP設定
     * @param weather 天氣設定
     * @param allowExplosions 爆炸允許設定
     */
    protected AbstractsMap(String name, String structureSpawnPoint, int structureSpawnSeparation, boolean mobGriefing, boolean doDaylightCycle, 
                         boolean doWeatherCycle, boolean keepInventory, boolean doMobSpawning, 
                         boolean pvp, String weather, boolean allowExplosions) {
        this.name = name;
        this.structureSpawnPoint = LocationUtils.stringToLocation(Bukkit.getWorld(name), structureSpawnPoint);
        this.structureSpawnSeparation = structureSpawnSeparation;
        this.mobGriefing = mobGriefing;
        this.doDaylightCycle = doDaylightCycle;
        this.doWeatherCycle = doWeatherCycle;
        this.keepInventory = keepInventory;
        this.doMobSpawning = doMobSpawning;
        this.pvp = pvp;
        this.weather = weather;
        this.allowExplosions = allowExplosions;
        this.mapLocation = new MapLocation(this);
    }

    /**
     * 獲取地圖名稱
     * @return 地圖名稱
     */
    public String getName() {
        return name;
    }

    /**
     * 獲取出生點
     * @return 出生點
     */
    public Location getStructureSpawnPoint() {
        return structureSpawnPoint;
    }

    /**
     * 獲取結構物生成間隔
     * @return 結構物生成間隔
     */
    public int getStructureSpawnSeparation() {
        return structureSpawnSeparation;
    }


    
    /**
     * 檢查是否允許生物破壞
     * @return 生物破壞設定
     */
    public boolean isMobGriefing() {
        return mobGriefing;
    }

    /**
     * 檢查是否開啟日夜循環
     * @return 日夜循環設定
     */
    public boolean isDoDaylightCycle() {
        return doDaylightCycle;
    }

    /**
     * 檢查是否開啟天氣循環
     * @return 天氣循環設定
     */
    public boolean isDoWeatherCycle() {
        return doWeatherCycle;
    }

    /**
     * 檢查是否保留物品欄
     * @return 保留物品欄設定
     */
    public boolean isKeepInventory() {
        return keepInventory;
    }

    /**
     * 檢查是否允許生物生成
     * @return 生物生成設定
     */
    public boolean isDoMobSpawning() {
        return doMobSpawning;
    }

    /**
     * 檢查是否開啟PVP
     * @return PVP設定
     */
    public boolean isPvp() {
        return pvp;
    }

    /**
     * 獲取天氣狀態
     * @return 天氣狀態
     */
    public String getWeather() {
        return weather;
    }

    /**
     * 檢查是否允許爆炸
     * @return 爆炸允許設定
     */
    public boolean isAllowExplosions() {
        return allowExplosions;
    }

    /**
     * 應用地圖規則到指定世界
     * 包括遊戲規則、PVP設定、天氣設定等
     */
    public void applyMapRules() {
    }

    public MapLocation getMapLocation() {
        return mapLocation;
    }

    public void updateMapLocation() {
        Location location = mapLocation.getLocation();
        location.setX(location.getX() + structureSpawnSeparation);
        mapLocation.setLocation(location);
    }
}
