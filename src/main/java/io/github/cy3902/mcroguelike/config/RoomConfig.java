package io.github.cy3902.mcroguelike.config;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import io.github.cy3902.mcroguelike.abstracts.AbstractRoom.SpawnPoint;

/**
 * 房間配置類
 * 用於存儲房間的配置信息
 */
public class RoomConfig {
    private String roomId;
    private String name;
    private String type;
    private String structure;
    private String description;
    private int timeLimit;
    private int baseScore;
    private double earlyCompletionMultiplier;
    private String playerSpawn;
    private int minFloor;
    private int maxFloor;
    private List<SpawnPoint> spawnpoints;

    /**
     * 默認構造函數
     */
    public RoomConfig() {
        this.roomId = "";
        this.name = "";
        this.type = "";
        this.structure = "";
        this.description = "";
        this.timeLimit = 600;
        this.baseScore = 200;
        this.earlyCompletionMultiplier = 1.5;
        this.playerSpawn = "0,64,0";
        this.minFloor = 1;
        this.maxFloor = 10;
        this.spawnpoints = null;
    }

    /**
     * 完整構造函數
     */
    public RoomConfig(String roomId, String name, String type, String structure, String description, 
                     int timeLimit, int baseScore, double earlyCompletionMultiplier, 
                     String playerSpawn, int minFloor, int maxFloor, 
                     List<SpawnPoint> spawnpoints) {
        this.roomId = roomId;
        this.name = name;
        this.type = type;
        this.structure = structure;
        this.description = description;
        this.timeLimit = timeLimit;
        this.baseScore = baseScore;
        this.earlyCompletionMultiplier = earlyCompletionMultiplier;
        this.playerSpawn = playerSpawn;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.spawnpoints = spawnpoints;
    }

    // Getters
    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStructure() {
        return structure;
    }

    public String getDescription() {
        return description;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public double getEarlyCompletionMultiplier() {
        return earlyCompletionMultiplier;
    }

    public String getPlayerSpawn() {
        return playerSpawn;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public List<SpawnPoint> getSpawnpoints() {
        return spawnpoints;
    }

    // Setters
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    public void setEarlyCompletionMultiplier(double earlyCompletionMultiplier) {
        this.earlyCompletionMultiplier = earlyCompletionMultiplier;
    }

    public void setPlayerSpawn(String playerSpawn) {
        this.playerSpawn = playerSpawn;
    }

    public void setMinFloor(int minFloor) {
        this.minFloor = minFloor;
    }

    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    public void setSpawnpoints(List<SpawnPoint> spawnpoints) {
        this.spawnpoints = spawnpoints;
    }
} 