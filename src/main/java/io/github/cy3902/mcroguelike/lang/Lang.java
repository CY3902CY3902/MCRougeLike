package io.github.cy3902.mcroguelike.lang;

import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 語言處理類
 * 用於處理多語言支持
 */
public class Lang extends FileProvider<Map<String, String>> {
    private Map<String, String> messages;
    private String language;

    public Lang(Plugin plugin, String language) {
        super(plugin, "lang_" + language + ".yml", "lang");
        this.language = language;
        this.messages = new HashMap<>();
    }

    @Override
    public Map<String, String> load() {
        messages.clear();
        for (String key : yml.getKeys(true)) {
            if (!yml.isConfigurationSection(key)) {
                messages.put(key, yml.getString(key, key));
            }
        }
        return messages;
    }

    @Override
    public void save(Map<String, String> config) {
        for (Map.Entry<String, String> entry : config.entrySet()) {
            yml.set(entry.getKey(), entry.getValue());
        }
        try {
            yml.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving language file: " + file.getName(), e);
        }
    }

    @Override
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(file);
        load();
    }

    @Override
    public void readDefault() {
        if (!file.exists()) {
            plugin.saveResource(directory + "/" + fileName, false);
        }
    }

    /**
     * 獲取消息
     * @param key 消息鍵
     * @return 消息
     */
    public String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }

    /**
     * 設置消息
     * @param key 消息鍵
     * @param value 消息值
     */
    public void setMessage(String key, String value) {
        messages.put(key, value);
    }

    /**
     * 獲取語言
     * @return 語言
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 設置語言
     * @param language 語言
     */
    public void setLanguage(String language) {
        this.language = language;
        // Create a new Lang instance with the new language
        Lang newLang = new Lang(plugin, language);
        this.messages = newLang.load();
        this.yml = newLang.yml;
        this.file = newLang.file;
    }
} 