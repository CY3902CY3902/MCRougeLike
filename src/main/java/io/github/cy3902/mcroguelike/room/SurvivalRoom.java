package io.github.cy3902.mcroguelike.room;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * 生存類型房間
 * 玩家需要在指定時間內存活下來
 */
public class SurvivalRoom extends AbstractsRoom {
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
        super(roomId, roomName, structureName, new HashMap<>(), minFloor, maxFloor, timeLimit, baseScore, playerSpawnPoint);
        this.currentSurvivalTime = 0;
        this.isRunning = false;
        this.isPaused = false;
        this.timeLimit = timeLimit;
        
        // Convert List to HashMap
        for (AbstractSpawnpoint spawnPoint : spawnPoints) {
            getSpawnPoints().put(spawnPoint, null); // Location will be set later
        }
    }

    @Override
    public void initialize(List<Player> players) {
        // 設置玩家出生點
        for (Player player : players) {
            World world = player.getWorld();
            player.teleport(getPlayerSpawnPoint(world));
        }
        // 重置遊戲狀態
        reset();
    }

    @Override
    public void reset() {
        currentSurvivalTime = 0;
        isRunning = false;
        isPaused = false;
    }

    /**
     * 更新生存時間
     * 如果達到最大生存時間則結束房間
     * @param elapsedSeconds 經過的秒數
     */
    public void updateSurvivalTime(int elapsedSeconds) {
        this.currentSurvivalTime = elapsedSeconds;
        if (currentSurvivalTime >= timeLimit) {
            getManager().stop(); // 達到最大生存時間時結束房間
        }
    }

    /**
     * 取得當前生存時間
     * @return 當前生存時間(秒)
     */
    public int getCurrentSurvivalTime() {
        return currentSurvivalTime;
    }

    /**
     * 更新計分板
     */
    private void updateScoreboard() {
        // TODO: 實現計分板更新邏輯
    }
}
