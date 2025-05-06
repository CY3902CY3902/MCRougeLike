package io.github.cy3902.mcroguelike.room;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * 剿滅類型房間
 * 玩家需要在時間內消滅指定數量的敵人
 */
public class AnnihilationRoom extends AbstractRoom {
    private int currentKills;                  // 當前擊殺數
    private int totalTargets;                  // 目標擊殺數
    private double earlyCompletionMultiplier;  // 提早完成的分數乘數
    private boolean isRunning;                 // 是否正在運行
    private boolean isPaused;                  // 是否暫停
    private int baseScore;                     // 基礎分數

    public AnnihilationRoom(
            String roomId,
            String roomName,
            String structureName,
            List<AbstractSpawnpoint> spawnPoints,
            int minFloor,
            int maxFloor,
            int timeLimit,
            int baseScore,
            double earlyCompletionMultiplier,
            String playerSpawnPoint
    ) {
        super(roomId, roomName, structureName, RoomType.Annihilation, new HashMap<>(), minFloor, maxFloor, timeLimit, baseScore, playerSpawnPoint);
        this.earlyCompletionMultiplier = earlyCompletionMultiplier;
        this.currentKills = 0;
        this.baseScore = baseScore;
        this.isRunning = false;
        this.isPaused = false;
        
        // Convert List to HashMap
        for (AbstractSpawnpoint spawnPoint : spawnPoints) {
            getSpawnPoints().put(spawnPoint, null); // Location will be set later
        }
    }


    /**
     * 設定目標擊殺數
     * @param totalTargets 目標擊殺數
     */
    public void setTotalTargets(int totalTargets) {
        this.totalTargets = totalTargets;
    }

    /**
     * 增加擊殺數並檢查是否完成目標
     */
    public void addKill() {
        currentKills++;
    }

    /**
     * 取得當前擊殺數
     * @return 當前擊殺數
     */
    public int getCurrentKills() {
        return currentKills;
    }

    /**
     * 取得目標擊殺數
     * @return 目標擊殺數
     */
    public int getTotalTargets() {
        return totalTargets;
    }

    /**
     * 取得提早完成的分數乘數
     * @return 提早完成的分數乘數
     */
    public double getEarlyCompletionMultiplier() {
        return earlyCompletionMultiplier;
    }

    /**
     * 更新計分板
     */
    private void updateScoreboard() {
        // TODO: 實現計分板更新邏輯
    }
}
