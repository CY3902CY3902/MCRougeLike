package io.github.cy3902.mcroguelike.room;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;


import java.util.HashMap;
import java.util.List;

/**
 * 生存類型房間
 * 玩家需要在指定時間內存活下來
 */
public class SurvivalRoom extends AbstractRoom {
    private int currentSurvivalTime; // 當前生存時間(秒)
    private boolean isRunning;       // 是否正在運行
    private boolean isPaused;        // 是否暫停
    private int timeLimit;           // 時間限制

    /**
     * 建構子
     * @param roomId 房間ID
     * @param roomName 房間名稱
     * @param spawnPoints 怪物生成點列表
     * @param minFloor 最小房間層數
     * @param maxFloor 最大房間層數
     * @param timeLimit 房間時限(秒)
     * @param baseScore 基礎分數
     * @param playerSpawnPoint 玩家生成點
     */
    public SurvivalRoom(
            String roomId,
            String roomName,
            String structureName,
            List<AbstractSpawnpoint> spawnPoints,
            int minFloor,
            int maxFloor,
            int timeLimit,
            int baseScore,
            String playerSpawnPoint
    ) {
        super(roomId, roomName, structureName, RoomType.Survival, new HashMap<>(), minFloor, maxFloor, timeLimit, baseScore, playerSpawnPoint);
        this.currentSurvivalTime = 0;
        this.isRunning = false;
        this.isPaused = false;
        this.timeLimit = timeLimit;
        
        // Convert List to HashMap
        for (AbstractSpawnpoint spawnPoint : spawnPoints) {
            getSpawnPoints().put(spawnPoint, null); // Location will be set later
        }
    }
}
