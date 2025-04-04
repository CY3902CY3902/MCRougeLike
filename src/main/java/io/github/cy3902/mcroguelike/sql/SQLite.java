package io.github.cy3902.mcroguelike.sql;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsSQL;

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
public class SQLite extends AbstractsSQL {

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

        String createMapLocationTableSQL = "CREATE TABLE IF NOT EXISTS mcroguelike_map_location ("
                + "map TEXT NOT NULL PRIMARY KEY, "
                + "location TEXT NOT NULL"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMapLocationTableSQL);
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
        try (Connection conn = MCRogueLike.getSql().getConnection();
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
}