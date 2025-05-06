package io.github.cy3902.mcroguelike.manager.room;

/**
 * 分數管理器介面
 * 負責管理房間的分數
 */
public interface ScoreManager {
    /**
     * 更新分數
     * @param points 要增加的分數
     */
    void updateScore(int points);

    /**
     * 獲取當前分數
     * @return 當前分數
     */
    int getCurrentScore();

    /**
     * 重置分數
     */
    void resetScore();

    /**
     * 設置基礎分數
     * @param baseScore 基礎分數
     */
    void setBaseScore(int baseScore);

    /**
     * 獲取基礎分數
     * @return 基礎分數
     */
    int getBaseScore();
} 