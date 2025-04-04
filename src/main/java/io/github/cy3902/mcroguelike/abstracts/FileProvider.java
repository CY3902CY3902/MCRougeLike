package io.github.cy3902.mcroguelike.abstracts;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * 文件提供者抽象類
 * 用於處理配置文件的讀寫操作
 * @param <T> 配置對象的類型
 */
public abstract class FileProvider<T> {
    protected final Plugin plugin;
    protected final String fileName;
    protected final String directory;
    protected File file;
    protected YamlConfiguration yml;

    /**
     * 創建文件提供者
     * @param plugin 插件實例
     * @param fileName 文件名
     * @param directory 目錄名
     */
    public FileProvider(Plugin plugin, String fileName, String directory) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.directory = directory;
        this.file = new File(plugin.getDataFolder(), directory + "/" + fileName);
        if (!this.file.exists()) {
            plugin.saveResource(directory + "/" + fileName, false);
        }
        this.yml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 加載配置
     * @return 配置對象
     */
    public abstract T load();

    /**
     * 保存配置
     * @param config 配置對象
     */
    public abstract void save(T config);

    /**
     * 重新加載配置
     */
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(file);
        load();
    }

    /**
     * 讀取默認配置
     */
    public void readDefault() {
        if (!file.exists()) {
            plugin.saveResource(directory + "/" + fileName, false);
        }
    }

    /**
     * 獲取文件名
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 獲取文件
     * @return 文件對象
     */
    public File getFile() {
        return file;
    }

    /**
     * 獲取YAML配置
     * @return YAML配置對象
     */
    public YamlConfiguration getYml() {
        return yml;
    }

    /**
     * 獲取插件實例
     * @return 插件實例
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * 根據指定路徑從 YAML 配置中取得值
     * @param yml YAML 配置對象
     * @param path 配置路徑
     * @param defaultValue 默認值
     * @param <V> 值的類型
     * @return 取得的值或默認值
     */
    public <V> V getValue(YamlConfiguration yml, String path, V defaultValue) {
        Object value = yml.get(path);
        if (value != null) {
            try {
                return (V) value;
            } catch (ClassCastException ex) {
                if (defaultValue instanceof List && value instanceof String) {
                    return (V) Arrays.asList(((String) value).split(","));
                }
                logError("readYmlError", path, defaultValue);
                return defaultValue;
            }
        }
        yml.set(path, defaultValue);
        return defaultValue;
    }

    /**
     * 根據指定路徑從 YAML 配置中取得配置段
     * @param path 配置段路徑
     * @return 配置段的 Optional 對象
     */
    public Optional<ConfigurationSection> getSection(String path) {
        ConfigurationSection section = this.yml.getConfigurationSection(path);
        return section != null ? Optional.of(section) : Optional.empty();
    }

    /**
     * 記錄錯誤日誌
     * @param messageKey 錯誤信息鍵
     * @param path 配置路徑
     */
    protected void logError(String messageKey, String path) {
        logError(messageKey, path, null);
    }

    /**
     * 記錄錯誤日誌
     * @param messageKey 錯誤信息鍵
     * @param path 配置路徑
     * @param defaultValue 默認值
     */
    protected void logError(String messageKey, String path, Object defaultValue) {
        String message = getLangMessage(messageKey);
        if (defaultValue != null) {
            plugin.getLogger().log(Level.SEVERE, message + path + ", using default value: " + defaultValue);
        } else {
            plugin.getLogger().log(Level.SEVERE, message + path);
        }
    }

    /**
     * 獲取語言消息
     * @param key 消息鍵
     * @return 語言消息
     */
    protected String getLangMessage(String key) {
        // 嘗試從插件獲取語言消息
        try {
            // 假設插件有一個getLang()方法返回Lang對象，該對象有getMessage方法
            return plugin.getClass().getMethod("getLang").invoke(plugin).getClass()
                    .getMethod("getMessage", String.class).invoke(plugin.getClass().getMethod("getLang").invoke(plugin), key).toString();
        } catch (Exception e) {
            // 如果無法獲取，返回鍵名
            return key + ": ";
        }
    }
}