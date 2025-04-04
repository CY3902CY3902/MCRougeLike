package io.github.cy3902.mcroguelike.manager.core;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 管理器註冊中心
 * 用於集中管理所有管理器
 */
public class ManagerRegistry {
    private static final Map<Class<? extends Manager>, Manager> managers = new HashMap<>();
    private static Plugin plugin;

    /**
     * 初始化管理器註冊中心
     * @param plugin 插件實例
     */
    public static void initialize(Plugin plugin) {
        ManagerRegistry.plugin = plugin;
    }

    /**
     * 註冊管理器
     * @param managerClass 管理器類
     * @param manager 管理器實例
     */
    public static void register(Class<? extends Manager> managerClass, Manager manager) {
        managers.put(managerClass, manager);
        manager.initialize(plugin);
    }

    /**
     * 獲取管理器
     * @param managerClass 管理器類
     * @return 管理器實例
     */
    @SuppressWarnings("unchecked")
    public static <T extends Manager> T get(Class<T> managerClass) {
        return (T) managers.get(managerClass);
    }

    /**
     * 重新加載所有管理器
     */
    public static void reloadAll() {
        for (Manager manager : managers.values()) {
            try {
                manager.reload();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error reloading manager: " + manager.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 保存所有管理器數據
     */
    public static void saveAll() {
        for (Manager manager : managers.values()) {
            try {
                manager.save();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error saving manager: " + manager.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * 關閉所有管理器
     */
    public static void shutdownAll() {
        for (Manager manager : managers.values()) {
            try {
                manager.shutdown();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error shutting down manager: " + manager.getClass().getSimpleName(), e);
            }
        }
        managers.clear();
    }
} 