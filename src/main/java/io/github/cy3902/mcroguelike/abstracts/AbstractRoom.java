package io.github.cy3902.mcroguelike.abstracts;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.manager.room.ScoreManager;
import io.github.cy3902.mcroguelike.manager.room.SpawnPointManager;
import io.github.cy3902.mcroguelike.schem.Schem;
import io.github.cy3902.mcroguelike.utils.LocationUtils;

import java.io.File;
import java.util.HashMap;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * AbstractsLevel 是抽象關卡類別，定義了關卡的基本屬性和行為。
 * 包含了關卡模式、類型的定義，以及關卡的基本操作方法。
 */
public abstract class AbstractRoom {
    protected final MCRogueLike mcRogueLike = MCRogueLike.getInstance();

    /**
     * 關卡模式的列舉
     */
    public enum RoomMode
    {
        Generated,  // 生成模式
        Start,      // 開始模式
        Stop,       // 停止模式
        End         // 結束模式
    }

    /**
     * 關卡類型的列舉
     */
    public enum RoomType {
        Survival,       //生存
        Annihilation,   //剿滅
        Defense,        //防守
        SniperMission   //狙殺
    }

    private final String roomId;
    private final String name;
    private final String structureName;
    private final RoomType type;
    private final HashMap<AbstractSpawnpoint, Location> spawnPoints;
    private final int minFloor;
    private final int maxFloor;
    private final int timeLimit;
    private final int baseScore;
    private final String playerSpawnPoint;


    private SpawnPointManager spawnPointManager;
    private ScoreManager scoreManager;

    /**
     * 建構子，初始化關卡
     * @param roomId 房間ID
     * @param name 關卡名稱
     * @param spawnPoints 怪物生成點列表
     * @param minFloor 最小關卡層數
     * @param maxFloor 最大關卡層數
     * @param timeLimit 關卡時限(秒)
     * @param baseScore 基礎分數
     * @param playerSpawnPoint 玩家出生點
     */
    public AbstractRoom(
            String roomId,
            String name,
            String structureName,
            RoomType type,
            HashMap<AbstractSpawnpoint, Location> spawnPoints,
            int minFloor,
            int maxFloor,
            int timeLimit,
            int baseScore,
            String playerSpawnPoint
    ) {
        this.roomId = roomId;
        this.name = name;
        this.structureName = structureName;
        this.type = type;
        this.spawnPoints = spawnPoints;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.timeLimit = timeLimit;
        this.baseScore = baseScore;
        this.playerSpawnPoint = playerSpawnPoint;
    }



    /**
     * 取得關卡時限
     * @return 關卡時限
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * 取得插件實例
     * @return 插件實例
     */
    public MCRogueLike getPlugin() {
        return MCRogueLike.getInstance();
    }

    /**
     * 取得房間ID
     * @return 房間ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * 載入關卡結構
     * @param location 位置
     * @param callback 回調函數，當結構生成完成時調用
     */
    public void loadSchematics(Location location, Consumer<Boolean> callback) {
        //載入結構
        File schemFile = new File(mcRogueLike.getDataFolder() + "/schematics/" + structureName + ".schem");
        if (!schemFile.exists()) {
            if (callback != null) {
                callback.accept(false);
            }
            return;
        }
        Schem schem = new Schem(structureName, schemFile, location);
        schem.setPasteCallback(callback);
        schem.paste(location);
    }

    /**
     * 載入關卡結構（無回調版本）
     * @param location 位置
     */
    public void loadSchematics(Location location) {
        loadSchematics(location, null);
    }

    /**
     * 取得關卡名稱
     * @return 關卡名稱
     */
    public String getName() {
        return name;
    }

    /**
     * 取得最小關卡層數
     * @return 最小關卡層數
     */
    public int getMinFloor(){
        return minFloor;
    };

    /**
     * 取得最大關卡層數
     * @return 最大關卡層數
     */
    public int getMaxFloor(){
        return maxFloor;
    };

    /**
     * 取得結構名稱
     * @return 結構名稱
     */
    public String getStructureName(){
        return structureName;
    }

    /**
     * 取得玩家出生點
     * @return 玩家出生點
     */
    public Location getPlayerSpawnPoint(World world) {
        return LocationUtils.stringToLocation(world, playerSpawnPoint);
    }


    /**
     * 取得生成點列表
     * @return 生成點列表
     */
    public HashMap<AbstractSpawnpoint, Location> getSpawnPoints() {
        return spawnPoints;
    }


    public SpawnPointManager getSpawnPointManager() {
        return spawnPointManager;
    }

    public void setSpawnPointManager(SpawnPointManager spawnPointManager) {
        this.spawnPointManager = spawnPointManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    /**
     * 取得關卡類型
     * @return 關卡類型
     */
    public RoomType getType() {
        return type;
    }
}
