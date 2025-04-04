package io.github.cy3902.mcroguelike.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 路徑配置類
 * 用於存儲路徑的配置信息
 */
public class PathConfig {
    private String pathId;
    private String name;
    private String description;
    private int minFloor;
    private int maxFloor;
    private int maxNodes;
    private int maxBranches;
    private int maxHeight;
    private double specialNodeProbability;
    private List<String> roomNames;
    private List<String> bossRoomNames;

    /**
     * 默認構造函數
     */
    public PathConfig() {
        this.pathId = "";
        this.name = "";
        this.description = "";
        this.minFloor = 1;
        this.maxFloor = 1;
        this.maxNodes = 10;
        this.maxBranches = 3;
        this.maxHeight = 5;
        this.specialNodeProbability = 0.2;
        this.roomNames = new ArrayList<>();
        this.bossRoomNames = new ArrayList<>();
    }

    /**
     * 完整構造函數
     * @param pathId 路徑ID
     * @param name 路徑名稱
     * @param description 路徑描述
     * @param minFloor 最小樓層
     * @param maxFloor 最大樓層
     * @param maxNodes 最大節點數
     * @param maxBranches 最大分支數
     * @param maxHeight 最大高度
     * @param specialNodeProbability 特殊節點概率
     * @param roomNames 房間名稱列表
     * @param bossRoomNames Boss房間名稱列表
     */
    public PathConfig(String pathId, String name, String description, 
                     int minFloor, int maxFloor, int maxNodes, int maxBranches,
                     int maxHeight, double specialNodeProbability,
                     List<String> roomNames, List<String> bossRoomNames) {
        this.pathId = pathId;
        this.name = name;
        this.description = description;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.maxNodes = maxNodes;
        this.maxBranches = maxBranches;
        this.maxHeight = maxHeight;
        this.specialNodeProbability = specialNodeProbability;
        this.roomNames = new ArrayList<>(roomNames);
        this.bossRoomNames = new ArrayList<>(bossRoomNames);
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public void setMinFloor(int minFloor) {
        this.minFloor = minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    public int getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public int getMaxBranches() {
        return maxBranches;
    }

    public void setMaxBranches(int maxBranches) {
        this.maxBranches = maxBranches;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public double getSpecialNodeProbability() {
        return specialNodeProbability;
    }

    public void setSpecialNodeProbability(double specialNodeProbability) {
        this.specialNodeProbability = specialNodeProbability;
    }

    public List<String> getRoomNames() {
        return roomNames;
    }

    public void setRoomNames(List<String> roomNames) {
        this.roomNames = roomNames;
    }

    public List<String> getBossRoomNames() {
        return bossRoomNames;
    }

    public void setBossRoomNames(List<String> bossRoomNames) {
        this.bossRoomNames = bossRoomNames;
    }
} 