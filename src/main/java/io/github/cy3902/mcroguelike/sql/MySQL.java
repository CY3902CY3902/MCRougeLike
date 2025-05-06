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
 * MySQL 資料庫操作類別，繼承自 AbstractsSQL。
 * 該類別用於連接 MySQL 資料庫，並創建所需的資料表。
 */
public class MySQL extends AbstractSQL {

    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private static String databaseUrl;
    private static String username;
    private static String password;

    /**
     * 構造函數，用於初始化 MySQL 資料庫連接信息並創建資料表。
     *
     * @param host     資料庫主機地址
     * @param port     資料庫端口
     * @param dbName   資料庫名稱
     * @param username 資料庫用戶名
     * @param password 資料庫密碼
     */
    public MySQL(String host, int port, String dbName, String username, String password) {
        databaseUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        MySQL.username = username;
        MySQL.password = password;
        connect();
        createTableIfNotExists();
    }

    /**
     * 連接到 MySQL 資料庫。
     */
    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection(databaseUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果資料表不存在，則創建所需的資料表。
     */
    @Override
    public void createTableIfNotExists() {
        // 地圖位置表格
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
        String createPlayerPathTableSQL = "CREATE TABLE IF NOT EXISTS mcroguelike_player_path ("
                + "player VARCHAR(255) NOT NULL PRIMARY KEY, "
                + "path VARCHAR(255) NOT NULL"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayerPathTableSQL);
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
        String clearTable = "TRUNCATE TABLE `" + table + "`";
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
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(String sql, String[] params) {
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