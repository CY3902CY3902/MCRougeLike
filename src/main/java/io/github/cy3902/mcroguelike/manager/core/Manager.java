package io.github.cy3902.mcroguelike.manager.core;


/**
 * 管理器基礎接口
 * 所有管理器都應該實現這個接口
 */
public interface Manager {

    /**
     * 重新加載管理器
     */
    void reload();

    /**
     * 保存管理器數據
     */
    void save();

    /**
     * 關閉管理器
     */
    void shutdown();
} 