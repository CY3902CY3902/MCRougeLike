package io.github.cy3902.mcroguelike.lang;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 語言管理器
 * 用於管理多個語言
 */
public class LangManager {
    private final Plugin plugin;
    private final Map<String, Lang> languages;
    private String defaultLanguage;

    public LangManager(Plugin plugin, String defaultLanguage) {
        this.plugin = plugin;
        this.languages = new HashMap<>();
        this.defaultLanguage = defaultLanguage;
        loadLanguage(defaultLanguage);
    }

    /**
     * 加載語言
     * @param language 語言
     */
    public void loadLanguage(String language) {
        if (!languages.containsKey(language)) {
            Lang lang = new Lang(plugin, language);
            lang.load();
            languages.put(language, lang);
        }
    }

    /**
     * 獲取語言
     * @param language 語言
     * @return 語言對象
     */
    public Lang getLanguage(String language) {
        if (!languages.containsKey(language)) {
            loadLanguage(language);
        }
        return languages.get(language);
    }

    /**
     * 獲取默認語言
     * @return 默認語言對象
     */
    public Lang getDefaultLanguage() {
        return getLanguage(defaultLanguage);
    }

    /**
     * 設置默認語言
     * @param language 語言
     */
    public void setDefaultLanguage(String language) {
        this.defaultLanguage = language;
        loadLanguage(language);
    }

    /**
     * 獲取消息
     * @param key 消息鍵
     * @return 消息
     */
    public String getMessage(String key) {
        return getDefaultLanguage().getMessage(key);
    }

    /**
     * 獲取消息
     * @param key 消息鍵
     * @param language 語言
     * @return 消息
     */
    public String getMessage(String key, String language) {
        return getLanguage(language).getMessage(key);
    }

    /**
     * 重新加載所有語言
     */
    public void reloadAll() {
        for (Lang lang : languages.values()) {
            lang.reload();
        }
    }
} 