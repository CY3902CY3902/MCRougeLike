package io.github.cy3902.mcroguelike.abstracts;

import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * FileProviderList 是一個抽象類別，用於處理目錄中的所有 YAML 配置文件。
 * 提供了初始化、讀取預設值、取得配置值和處理 YAML 配置段的方法。
 */
public class FileProviderList<T extends FileProvider<?>> {
    private final Plugin plugin;
    private final String directory;
    private final String extension;
    private final Map<String, T> providers;

    /**
     * 構造函數
     * @param plugin 插件實例
     * @param directory 目錄名稱
     * @param extension 文件擴展名
     */
    public FileProviderList(Plugin plugin, String directory, String extension) {
        this.plugin = plugin;
        this.directory = directory;
        this.extension = extension;
        this.providers = new HashMap<>();
    }

    /**
     * 添加文件提供者
     * @param key 鍵值
     * @param provider 文件提供者
     */
    public void addProvider(String key, T provider) {
        providers.put(key, provider);
    }

    /**
     * 移除文件提供者
     * @param key 鍵值
     */
    public void removeProvider(String key) {
        providers.remove(key);
    }

    /**
     * 獲取文件提供者
     * @param key 鍵值
     * @return 文件提供者
     */
    public T getProvider(String key) {
        return providers.get(key);
    }

    /**
     * 重新加載所有文件
     */
    public void reloadAll() {
        for (T provider : providers.values()) {
            try {
                provider.reload();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error reloading file: " + provider.getFileName(), e);
            }
        }
    }

    /**
     * 保存所有文件
     */
    @SuppressWarnings("unchecked")
    public void saveAll() {
        for (T provider : providers.values()) {
            try {
                FileProvider<Object> p = (FileProvider<Object>) provider;
                p.save(p.load());
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error saving file: " + provider.getFileName(), e);
            }
        }
    }

    /**
     * 獲取所有文件提供者
     * @return 文件提供者映射
     */
    public Map<String, T> getProviders() {
        return new HashMap<>(providers);
    }

    /**
     * 獲取目錄
     * @return 目錄
     */
    public File getDirectory() {
        File dir = new File(plugin.getDataFolder(), directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 獲取目錄名稱
     * @return 目錄名稱
     */
    public String getDirectoryName() {
        return directory;
    }

    /**
     * 獲取文件擴展名
     * @return 文件擴展名
     */
    public String getExtension() {
        return extension;
    }
}
