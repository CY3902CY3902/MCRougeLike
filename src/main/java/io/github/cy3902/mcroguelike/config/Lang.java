package io.github.cy3902.mcroguelike.config;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 用於讀取和管理插件語言配置的類別。
 */
public class Lang extends FileProvider<Map<String, String>> {

    private final MCRogueLike mcroguelike;
    private Map<String, String> messages;

    /**
     * 語言類型的枚舉
     */
    public enum LangType {
        zh_TW, en_US
    }

    /**
     * 構造函數，用於初始化 Lang 對象。
     *
     * @param plugin 插件實例
     * @param internalPath 配置文件的內部路徑
     * @param fileName 配置文件的名稱
     */
    public Lang(Plugin plugin, String internalPath, String fileName) {
        super(plugin, fileName, internalPath);
        this.mcroguelike = MCRogueLike.getInstance();
        this.messages = new HashMap<>();
    }

    @Override
    public Map<String, String> load() {
        messages.clear();
        readDefault();
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
            plugin.getLogger().severe("Error saving language file: " + e.getMessage());
        }
    }

    @Override
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private List<String> color(List<String> texts) {
        List<String> colored = new ArrayList<>();
        for (String text : texts) {
            colored.add(color(text));
        }
        return colored;
    }

    @Override
    public void readDefault() {
        // 預設的幫助玩家列表
        List<String> defaultHelpPlayer = Arrays.asList(
                "&f------ &b&lMCRogueLike &f------",
                "&a指令: &e/mcrougelike",
                "&a子命令:",
                "&8 - reload &7重新載入此插件的所有設定檔"
        );

        // 從配置文件中讀取並儲存語言字符串
        messages.put("plugin", color(yml.getString("plugin", "&b&lMCRogueLike &f")));
        messages.put("plugin_enable", color(yml.getString("plugin_enable", "MCRogueLike 插件已啟用")));
        messages.put("plugin_disable", color(yml.getString("plugin_disable", "MCRogueLike 插件已停用")));
        
        // 處理幫助列表
        List<String> helpList = yml.getStringList("help_player");
        if (helpList.isEmpty()) {
            helpList = defaultHelpPlayer;
        }
        messages.put("help_player", String.join("\n", color(helpList)));
        
        messages.put("unknown_command", color(yml.getString("unknown_command", "&c指令輸入錯誤，請使用/mcrougelike查詢")));
        messages.put("no_permission", color(yml.getString("no_permission", "&c你沒有權限使用此指定")));
        messages.put("reload", color(yml.getString("reload", "&f正在重新載入此插件的所有設定檔")));
        messages.put("reload_error", color(yml.getString("reload_error", "&c載入此插件的所有設定檔時出現錯誤")));
        messages.put("read_yml_error", color(yml.getString("read_yml_error", "&cYML 文件配置錯誤! 檔案路徑: ")));
        messages.put("world_not_found_message", color(yml.getString("world_not_found_message", "&c世界不存在，值:")));
        messages.put("read_lang_error", color(yml.getString("read_lang_error", "&c文件配置錯誤，默認繁體中文")));
        messages.put("error", color(yml.getString("error", "&c發生錯誤")));

        // 讀取關卡類型的語言設定
        messages.put("level_type.survival", color(yml.getString("level_type.survival", "生存")));
        messages.put("level_type.annihilation", color(yml.getString("level_type.annihilation", "剿滅")));
        messages.put("level_type.defense", color(yml.getString("level_type.defense", "防守")));
        messages.put("level_type.sniper_mission", color(yml.getString("level_type.sniper_mission", "狙殺")));

        // GUI相關文字
        messages.put("gui.title", color(yml.getString("gui.title", "&b&lMCRogueLike &f路徑選擇")));
        messages.put("gui.path_name", color(yml.getString("gui.path_name", "&a路徑名稱")));
        messages.put("gui.path_difficulty", color(yml.getString("gui.path_difficulty", "&e難度")));
        messages.put("gui.path_description", color(yml.getString("gui.path_description", "&f描述")));
        messages.put("player_only", color(yml.getString("player_only", "&c此指令只能由玩家使用")));
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
        yml.set(key, value);
    }
}
