package io.github.cy3902.mcroguelike.utils;

import org.bukkit.Location;
import org.bukkit.World;
import io.github.cy3902.mcroguelike.MCRogueLike;

/**
 * 位置工具類，用於處理位置相關的轉換和計算
 */
public class LocationUtils {
    
    /**
     * 將字符串格式的座標轉換為Location對象
     * 格式: "x,y,z" 或 "world,x,y,z" 或 "world,x,y,z,yaw,pitch"
     * @param world 世界對象
     * @param locationString 位置字符串
     * @return Location對象，如果轉換失敗則返回null
     */
    public static Location stringToLocation(World world, String locationString) {
        if (locationString == null || locationString.trim().isEmpty()) {
            return null;
        }

        String[] parts = locationString.split(",");
        if (parts.length < 3) {
            return null;
        }

        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            double z = Double.parseDouble(parts[2].trim());
            float yaw = parts.length > 4 ? Float.parseFloat(parts[4].trim()) : 0f;
            float pitch = parts.length > 5 ? Float.parseFloat(parts[5].trim()) : 0f;

            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 將Location對象轉換為字符串格式
     * @param location Location對象
     * @return 格式化的位置字符串
     */
    public static String locationToString(Location location) {
        if (location == null) {
            return null;
        }
        return String.format("%s,%f,%f,%f,%f,%f",
            location.getWorld().getName(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch()
        );
    }

    /**
     * 計算兩個位置之間的距離
     * @param loc1 第一個位置
     * @param loc2 第二個位置
     * @return 距離
     */
    public static double distance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().equals(loc2.getWorld())) {
            return Double.MAX_VALUE;
        }
        return loc1.distance(loc2);
    }

    /**
     * 檢查位置是否在指定範圍內
     * @param location 要檢查的位置
     * @param center 中心位置
     * @param radius 半徑
     * @return 是否在範圍內
     */
    public static boolean isInRange(Location location, Location center, double radius) {
        return distance(location, center) <= radius;
    }
} 