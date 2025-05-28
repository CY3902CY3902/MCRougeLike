package io.github.cy3902.mcroguelike.map;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;

/**
 * MapLocation 類別負責管理地圖的位置信息
 * 提供位置信息的讀取、儲存和轉換功能
 */
public class MapLocation {
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private final AbstractMap map;
    private Location location;
    private static final String TABLE_NAME = "mcroguelike_map_location";
    /**
     * 建構子
     * @param map 關聯的地圖對象
     */
    public MapLocation(AbstractMap map) {
        this.map = map;
        this.location = loadLocation();
    }

    /**
     * 載入位置信息
     * 優先從數據庫讀取，如果不存在則使用默認位置
     * @return 載入的位置信息
     */
    private Location loadLocation() {
        Location dbLocation = loadFromDatabase();
        if (dbLocation != null) {
            return dbLocation;
        }
        return map.getStructureSpawnPoint();
    }

    /**
     * 從數據庫載入位置信息
     * @return 數據庫中的位置信息，如果不存在則返回null
     */
    private Location loadFromDatabase() {
        if (!ensureDatabaseConnection()) {
            return null;
        }

        String locationString = queryLocationFromDatabase();
        if (locationString == null) {
            return null;
        }
        return parseLocationString(locationString, map.getName());
    }

    /**
     * 確保數據庫連接有效
     * @return 連接是否有效
     */
    private boolean ensureDatabaseConnection() {
        if (!mcroguelike.getSql().isConnectionValid()) {
            mcroguelike.getSql().connect();
        }
        return mcroguelike.getSql().isConnectionValid();
    }

    /**
     * 從數據庫查詢位置信息
     * @return 位置信息字符串
     */
    private String queryLocationFromDatabase() {
        return mcroguelike.getSql().select(
            "SELECT X, Y, Z FROM " + TABLE_NAME + " WHERE map = ?",
            new String[]{map.getName()}
        );
    }

    /**
     * 解析位置字符串為Location對象
     * @param locationString 位置字符串
     * @return Location對象，解析失敗則返回null
     */
    private Location parseLocationString(String locationString, String worldString) {
        String[] parts = locationString.split(",");
        if (parts.length < 2) {
            return null;
        }

        try {
            World world = Bukkit.getWorld(worldString);
            if (world == null) {
                return null;
            }

            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());

            return new Location(world, x, y, z, 0, 0);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 保存位置信息到數據庫
     */
    public void saveToDatabase() {
        if (!ensureDatabaseConnection() || location == null) {
            return;
        }


        if (isLocationExistsInDatabase()) {
            updateLocationInDatabase(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        } else {
            insertLocationToDatabase(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
    }

    /**
     * 檢查位置是否已存在於數據庫
     * @return 是否存在
     */
    private boolean isLocationExistsInDatabase() {
        return queryLocationFromDatabase() != null;
    }

    /**
     * 更新數據庫中的位置信息
     * @param locationString 位置字符串
     */
    private void updateLocationInDatabase(int locationX, int locationY, int locationZ) {
        mcroguelike.getSql().update(
            "UPDATE " + TABLE_NAME + " SET X = ?, Y = ?, Z = ? WHERE map = ?",
            new String[]{String.valueOf(locationX), String.valueOf(locationY), String.valueOf(locationZ), map.getName()}
        );
    }

    /**
     * 插入新的位置信息到數據庫
     * @param locationString 位置字符串
     */
    private void insertLocationToDatabase(int locationX, int locationY, int locationZ) {
        mcroguelike.getSql().insert(
            "INSERT INTO " + TABLE_NAME + " (map, X, Y, Z) VALUES (?, ?, ?, ?)",
            new String[]{map.getName(), String.valueOf(locationX), String.valueOf(locationY), String.valueOf(locationZ)}
        );
    }


    /**
     * 設置新的位置
     * @param location 新的位置
     */
    public void setLocation(Location location) {
        this.location = location;
        saveToDatabase();
    }

    /**
     * 獲取當前位置
     * @return 當前位置
     */
    public Location getLocation() {
        return location;
    }
}
