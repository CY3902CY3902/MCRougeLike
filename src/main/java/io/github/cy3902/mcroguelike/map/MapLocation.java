package io.github.cy3902.mcroguelike.map;

import org.bukkit.Location;
import org.bukkit.World;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import io.github.cy3902.mcroguelike.utils.LocationUtils;

/**
 * MapLocation 類別負責管理地圖的位置信息
 * 提供位置信息的讀取、儲存和轉換功能
 */
public class MapLocation {
    private final AbstractsMap map;
    private Location location;
    private static final String TABLE_NAME = "mcroguelike_map_location";
    private static final String DEFAULT_LOCATION_FORMAT = "%s,%f,%f,%f,%f,%f";

    /**
     * 建構子
     * @param map 關聯的地圖對象
     */
    public MapLocation(AbstractsMap map) {
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

        return parseLocationString(locationString);
    }

    /**
     * 確保數據庫連接有效
     * @return 連接是否有效
     */
    private boolean ensureDatabaseConnection() {
        if (!MCRogueLike.getSql().isConnectionValid()) {
            MCRogueLike.getSql().connect();
        }
        return MCRogueLike.getSql().isConnectionValid();
    }

    /**
     * 從數據庫查詢位置信息
     * @return 位置信息字符串
     */
    private String queryLocationFromDatabase() {
        return MCRogueLike.getSql().select(
            "SELECT location FROM " + TABLE_NAME + " WHERE map = ?",
            new String[]{map.getName()}
        );
    }

    /**
     * 解析位置字符串為Location對象
     * @param locationString 位置字符串
     * @return Location對象，解析失敗則返回null
     */
    private Location parseLocationString(String locationString) {
        String[] parts = locationString.split(",");
        if (parts.length < 6) {
            return null;
        }

        try {
            World world = MCRogueLike.getInstance().getServer().getWorld(parts[0].trim());
            if (world == null) {
                return null;
            }

            double x = Double.parseDouble(parts[1].trim());
            double y = Double.parseDouble(parts[2].trim());
            double z = Double.parseDouble(parts[3].trim());
            float yaw = Float.parseFloat(parts[4].trim());
            float pitch = Float.parseFloat(parts[5].trim());

            return new Location(world, x, y, z, yaw, pitch);
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

        String locationString = serializeLocation();
        if (locationString == null) {
            return;
        }

        if (isLocationExistsInDatabase()) {
            updateLocationInDatabase(locationString);
        } else {
            insertLocationToDatabase(locationString);
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
    private void updateLocationInDatabase(String locationString) {
        MCRogueLike.getSql().update(
            "UPDATE " + TABLE_NAME + " SET location = ? WHERE map = ?",
            new String[]{locationString, map.getName()}
        );
    }

    /**
     * 插入新的位置信息到數據庫
     * @param locationString 位置字符串
     */
    private void insertLocationToDatabase(String locationString) {
        MCRogueLike.getSql().insert(
            "INSERT INTO " + TABLE_NAME + " (map, location) VALUES (?, ?)",
            new String[]{map.getName(), locationString}
        );
    }

    /**
     * 序列化Location對象為字符串
     * @return 序列化後的位置字符串
     */
    private String serializeLocation() {
        if (location == null) {
            return null;
        }
        return String.format(DEFAULT_LOCATION_FORMAT,
            location.getWorld().getName(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch()
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
