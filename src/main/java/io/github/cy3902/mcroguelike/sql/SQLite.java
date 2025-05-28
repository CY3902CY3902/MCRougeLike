package io.github.cy3902.mcroguelike.sql;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite 資料庫操作類別，繼承自 AbstractsSQL。
 * 該類別用於連接 SQLite 資料庫，並創建所需的資料表。
 */
public class SQLite extends AbstractSQL {

    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private static String DATABASE_URL;

    /**
     * 構造函數，用於初始化 SQLite 資料庫連接信息並創建資料表。
     *
     * @param filepath SQLite 資料庫檔案的路徑
     */
    public SQLite(String filepath) {
        DATABASE_URL = filepath;
        connect();
        createTableIfNotExists();
    }

    /**
     * 連接到 SQLite 資料庫。
     */
    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果資料表不存在，則創建所需的資料表。
     */
    @Override
    public void createTableIfNotExists() {

        // 創建地圖位置表格
        String createMapLocationTableSQL = "CREATE TABLE IF NOT EXISTS mcroguelike_map_location ("
                + "map VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "X DOUBLE NOT NULL," 
                + "Y DOUBLE NOT NULL,"
                + "Z DOUBLE NOT NULL"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMapLocationTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 創建玩家路徑表格
        String createPlayerPathTableSQL = "CREATE TABLE IF NOT EXISTS mcroguelike_party_path ("
                + "party_uuid VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "path VARCHAR(255) NOT NULL"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayerPathTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 創建結構位置表格
        String createSchemLocationTableSQL = "CREATE TABLE IF NOT EXISTS mcroguelike_schem ("
                + "name VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "center_x DOUBLE NOT NULL, "
                + "center_y DOUBLE NOT NULL, "
                + "center_z DOUBLE NOT NULL "
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createSchemLocationTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 創建隊伍成員表格
        String createPartyMemberTableSQL = "CREATE TABLE IF NOT EXISTS mcroguelike_party_member ("
                + "party_uuid VARCHAR(255) NOT NULL, "
                + "member_uuid VARCHAR(255) NOT NULL, "
                + "is_leader Boolean NOT NULL DEFAULT false, "
                + "PRIMARY KEY (party_uuid, member_uuid)"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPartyMemberTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
                
    }

    /**
     * 檢查資料庫連接是否有效。
     *
     * @return 如果連接有效，則返回 true；否則返回 false
     */
    @Override
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 關閉資料庫連接。
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清空指定的資料表。
     *
     * @param table 資料表名稱
     */
    @Override
    public void clearTables(String table) {
        String clearTable = "DELETE FROM `" + table + "`";
        connect();
        try (Connection conn = mcroguelike.getSql().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(clearTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String select(String sql, String[] params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                StringBuilder result = new StringBuilder();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        result.append(",");
                    }
                    result.append(rs.getString(i));
                }
                return result.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(String sql, String[] params) {
        // 將 MySQL 的 INSERT ... ON DUPLICATE KEY UPDATE 轉換為 SQLite 的 INSERT OR REPLACE
        if (sql.contains("ON DUPLICATE KEY UPDATE")) {
            sql = sql.substring(0, sql.indexOf("ON DUPLICATE KEY UPDATE")).trim();
            sql = sql.replace("INSERT INTO", "INSERT OR REPLACE INTO");
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(String sql, String[] params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String sql, String[] params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

}