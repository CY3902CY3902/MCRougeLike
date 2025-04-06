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

    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
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
    public Lang(String internalPath, String fileName) {
        super(fileName, internalPath);
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
            mcroguelike.getLogger().severe("Error saving language file: " + e.getMessage());
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
                "&8 - reload &7重新載入此插件的所有設定檔",
                "&8 - gui &7打開GUI介面",
                "&8 - paste <檔案名> &7貼上結構",
                "&8 - room &7管理房間"
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
        messages.put("reload_success", color(yml.getString("reload_success", "&a插件重新載入成功!")));
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
        
        // 結構相關文字
        messages.put("schem_file_not_found", color(yml.getString("schem_file_not_found", "&c找不到結構文件: %filename%")));
        messages.put("schem_pasted", color(yml.getString("schem_pasted", "&a已貼上結構: %filename%")));
        
        // 房間GUI相關文字
        messages.put("room.gui.title", color(yml.getString("room.gui.title", "&b&lMCRogueLike &f房間管理系統")));
        messages.put("room.gui.edit_title", color(yml.getString("room.gui.edit_title", "&b&lMCRogueLike &f編輯房間: ")));
        messages.put("room.gui.room_type_title", color(yml.getString("room.gui.room_type_title", "&b&lMCRogueLike &f選擇房間類型")));
        messages.put("room.gui.prev_page", color(yml.getString("room.gui.prev_page", "&a上一頁")));
        messages.put("room.gui.next_page", color(yml.getString("room.gui.next_page", "&a下一頁")));
        messages.put("room.gui.page_info", color(yml.getString("room.gui.page_info", "&a頁面 ")));
        messages.put("room.gui.create_new", color(yml.getString("room.gui.create_new", "&a創建新房間")));
        messages.put("room.gui.room_name", color(yml.getString("room.gui.room_name", "&a房間名稱")));
        messages.put("room.gui.room_type", color(yml.getString("room.gui.room_type", "&a房間類型")));
        messages.put("room.gui.structure", color(yml.getString("room.gui.structure", "&a結構名稱")));
        messages.put("room.gui.time_limit", color(yml.getString("room.gui.time_limit", "&a時限")));
        messages.put("room.gui.base_score", color(yml.getString("room.gui.base_score", "&a基礎分數")));
        messages.put("room.gui.player_spawn", color(yml.getString("room.gui.player_spawn", "&a玩家出生點")));
        messages.put("room.gui.min_floor", color(yml.getString("room.gui.min_floor", "&a最小樓層")));
        messages.put("room.gui.max_floor", color(yml.getString("room.gui.max_floor", "&a最大樓層")));
        messages.put("room.gui.save", color(yml.getString("room.gui.save", "&a保存更改")));
        messages.put("room.gui.back", color(yml.getString("room.gui.back", "&c返回")));
        messages.put("room.gui.current", color(yml.getString("room.gui.current", "&7當前")));
        messages.put("room.gui.click_to_edit", color(yml.getString("room.gui.click_to_edit", "&e左鍵點擊: 編輯房間")));
        messages.put("room.gui.click_to_delete", color(yml.getString("room.gui.click_to_delete", "&c右鍵點擊: 刪除房間")));
        messages.put("room.gui.click_to_select", color(yml.getString("room.gui.click_to_select", "&e點擊選擇此類型")));
        messages.put("room.gui.id", color(yml.getString("room.gui.id", "&7ID: ")));
        messages.put("room.gui.type", color(yml.getString("room.gui.type", "&7類型: ")));
        messages.put("room.gui.structure_info", color(yml.getString("room.gui.structure_info", "&7結構: ")));
        messages.put("room.gui.time_limit_info", color(yml.getString("room.gui.time_limit_info", "&7時限: ")));
        messages.put("room.gui.base_score_info", color(yml.getString("room.gui.base_score_info", "&7基礎分數: ")));
        messages.put("room.gui.player_spawn_info", color(yml.getString("room.gui.player_spawn_info", "&7玩家出生點: ")));
        messages.put("room.gui.min_floor_info", color(yml.getString("room.gui.min_floor_info", "&7當前最小樓層: ")));
        messages.put("room.gui.max_floor_info", color(yml.getString("room.gui.max_floor_info", "&7當前最大樓層: ")));
        messages.put("room.gui.unknown", color(yml.getString("room.gui.unknown", "未知")));
        messages.put("room.gui.not_set", color(yml.getString("room.gui.not_set", "未設置")));
        messages.put("room.gui.seconds", color(yml.getString("room.gui.seconds", " 秒")));
        messages.put("room.gui.yes", color(yml.getString("room.gui.yes", "是")));
        messages.put("room.gui.no", color(yml.getString("room.gui.no", "否")));
        messages.put("room.gui.room_deleted", color(yml.getString("room.gui.room_deleted", "&c房間已刪除！")));
        messages.put("room.gui.changes_saved", color(yml.getString("room.gui.changes_saved", "&a更改已保存！")));
        messages.put("room.gui.room_type_updated", color(yml.getString("room.gui.room_type_updated", "&a房間類型已更新為: ")));
        messages.put("room.gui.move_to_spawn", color(yml.getString("room.gui.move_to_spawn", "&e請移動到你想要設置的出生點位置，然後輸入 'confirm' 確認。")));
        messages.put("room.gui.cancel_spawn", color(yml.getString("room.gui.cancel_spawn", "&e輸入 'cancel' 取消設置。")));
        messages.put("room.gui.spawn_set", color(yml.getString("room.gui.spawn_set", "&a出生點已設置為: ")));
        messages.put("room.gui.spawn_cancelled", color(yml.getString("room.gui.spawn_cancelled", "&c已取消設置出生點")));
        messages.put("room.gui.invalid_input", color(yml.getString("room.gui.invalid_input", "&c無效的輸入。請輸入 'confirm' 確認或 'cancel' 取消。")));
        messages.put("room.gui.room_exists", color(yml.getString("room.gui.room_exists", "&c該房間ID已存在！")));
        messages.put("room.gui.room_created", color(yml.getString("room.gui.room_created", "&a已創建新房間！")));
        messages.put("room.gui.settings_updated", color(yml.getString("room.gui.settings_updated", "&a已更新設置！")));
        messages.put("room.gui.invalid_number", color(yml.getString("room.gui.invalid_number", "&c請輸入有效的數值！")));
        messages.put("room.gui.enter_value", color(yml.getString("room.gui.enter_value", "&e請輸入新的")));
        messages.put("room.gui.config_not_found", color(yml.getString("room.gui.config_not_found", "&c無法找到房間配置！")));
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
