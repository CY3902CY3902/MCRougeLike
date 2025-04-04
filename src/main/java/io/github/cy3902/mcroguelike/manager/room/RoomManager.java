package io.github.cy3902.mcroguelike.manager.room;

import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 房間管理器介面
 * 負責管理房間的運行狀態和敵人
 */
public interface RoomManager {
    /**
     * 開始房間
     * @param players 玩家列表
     */
    void start(List<Player> players, World world);

    /**
     * 暫停房間
     */
    void pause();

    /**
     * 恢復房間
     */
    void resume(World world);

    /**
     * 停止房間
     */
    void stop();

    /**
     * 取得房間實例
     * @return 房間實例
     */
    AbstractsRoom getRoom();

    /**
     * 檢查房間是否正在運行
     * @return 是否正在運行
     */
    boolean isRunning();

    /**
     * 檢查房間是否暫停
     * @return 是否暫停
     */
    boolean isPaused();

    /**
     * 更新剩餘時間
     * @param newTime 新的剩餘時間
     */
    void updateRemainingTime(int newTime);

    /**
     * 取得剩餘時間
     * @return 剩餘時間(秒)
     */
    int getRemainingTime();

    /**
     * 添加敵人
     * @param enemy 敵人實體
     */
    void addEnemy(LivingEntity enemy);

    /**
     * 移除敵人
     * @param enemy 敵人實體
     */
    void removeEnemy(LivingEntity enemy);

    /**
     * 獲取當前活著的敵人列表
     * @return 敵人列表
     */
    List<LivingEntity> getActiveEnemies();
} 