package io.github.cy3902.mcroguelike.config;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.config.Lang.LangType;
import io.github.cy3902.mcroguelike.sql.MySQL;
import io.github.cy3902.mcroguelike.sql.SQLite;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * 用於讀取和處理配置文件的類別。
 */
public class ConfigFile extends FileProvider<YamlConfiguration> {

    private final MCRogueLike mcroguelike;

    public enum DatabaseType {
        sqlite, mysql
    }

    /**
     * 初始化 ConfigFile 實例。
     *
     * @param plugin 插件實例
     */
    public ConfigFile(Plugin plugin) {
        super(plugin, "config.yml", "");
        this.mcroguelike = MCRogueLike.getInstance();
    }

    @Override
    public YamlConfiguration load() {
        return yml;
    }

    @Override
    public void save(YamlConfiguration config) {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Error saving config file: " + e.getMessage());
        }
    }

    @Override
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(file);
        readDefault();
    }

    @Override
    public void readDefault() {
        // 讀取語言設置，默認為 "zh_TW"
        String lang = yml.getString("language", "zh_TW");
        String databaseTypeString = yml.getString("database.type", "sqlite");
        DatabaseType databaseType;

        // 設置語言類型
        try {
            mcroguelike.setLangType(Lang.LangType.valueOf(lang));
        } catch (IllegalArgumentException e) {
            // 如果語言無效，設置為默認語言 "zh_TW"
            mcroguelike.setLangType(Lang.LangType.zh_TW);
        }

        // 設置數據庫類型
        try {
            databaseType = DatabaseType.valueOf(databaseTypeString);
        } catch (IllegalArgumentException e) {
            // 如果數據庫類型無效，設置為默認值 sqlite
            databaseType = DatabaseType.sqlite;
        }

        // 根據數據庫類型設置對應的數據庫連接
        if (databaseType == DatabaseType.sqlite) {
            // 讀取 SQLite 配置參數
            String DATABASE_URL = yml.getString("file_path", "plugins/MCRogueLike/SQL/mcrougelike.db");
            mcroguelike.setSql(new SQLite(DATABASE_URL));
        } else if (databaseType == DatabaseType.mysql) {
            // 讀取 MySQL 配置參數
            String host = yml.getString("database.mysql.host", "");
            String dbName = yml.getString("database.mysql.database", "");
            int port = yml.getInt("database.mysql.port", 0);
            String username = yml.getString("database.mysql.username", "");
            String password = yml.getString("database.mysql.password", "");

            // 創建 MySQL 實例並設置
            mcroguelike.setSql(new MySQL(host, port, dbName, username, password));
        }
    }
}
