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
    private int totalNodes;
    private int maxBranches;
    private int maxPlayer;
    private int minPlayer;
    private int maxHeight;
    private double specialNodeProbability;
    private List<String> roomNames;
    private List<String> bossRoomNames;
    private String mapName;

    /**
     * 默認構造函數
     */
    public PathConfig() {
        this.pathId = "";
        this.name = "";
        this.description = "";
        this.minFloor = 1;
        this.maxFloor = 1;
        this.totalNodes = 10;
        this.maxBranches = 3;
        this.maxPlayer = 1;
        this.minPlayer = 1;
        this.maxHeight = 5;
        this.specialNodeProbability = 0.2;
        this.roomNames = new ArrayList<>();
        this.bossRoomNames = new ArrayList<>();
        this.mapName = "";
    }

    /**
     * 完整構造函數
     * @param pathId 路徑ID
     * @param name 路徑名稱
     * @param description 路徑描述
     * @param minFloor 最小樓層
     * @param maxFloor 最大樓層
     * @param totalNodes 總節點數
     * @param maxBranches 最大分支數
     * @param maxPlayer 最大玩家數量
     * @param minPlayer 最小玩家數量
     * @param maxHeight 最大高度
     * @param specialNodeProbability 特殊節點概率
     * @param roomNames 房間名稱列表
     * @param bossRoomNames Boss房間名稱列表
     * @param mapName 地圖名稱
     */
    public PathConfig(String pathId, String name, String description, 
                     int minFloor, int maxFloor, int totalNodes, int maxBranches, 
                     int maxPlayer, int minPlayer,
                     int maxHeight, double specialNodeProbability,
                     List<String> roomNames, List<String> bossRoomNames,
                     String mapName) {
        this.pathId = pathId;
        this.name = name;
        this.description = description;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.totalNodes = totalNodes;
        this.maxBranches = maxBranches;
        this.maxHeight = maxHeight;
        this.maxPlayer = maxPlayer;
        this.minPlayer = minPlayer;
        this.specialNodeProbability = specialNodeProbability;
        this.roomNames = new ArrayList<>(roomNames);
        this.bossRoomNames = new ArrayList<>(bossRoomNames);
        this.mapName = mapName;
    }

    /**
     * 獲取路徑ID
     * @return 路徑ID
     */
    public String getPathId() {
        return pathId;
    }

    /**
     * 設置路徑ID
     * @param pathId 路徑ID
     */
    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    /**
     * 獲取路徑名稱
     * @return 路徑名稱
     */
    public String getName() {
        return name;
    }

    /**
     * 設置路徑名稱
     * @param name 路徑名稱
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 獲取路徑描述
     * @return 路徑描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 設置路徑描述
     * @param description 路徑描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 獲取最小樓層
     * @return 最小樓層
     */
    public int getMinFloor() {
        return minFloor;
    }

    /**
     * 設置最小樓層
     * @param minFloor 最小樓層
     */
    public void setMinFloor(int minFloor) {
        this.minFloor = minFloor;
    }

    /**
     * 獲取最大樓層
     * @return 最大樓層
     */
    public int getMaxFloor() {
        return maxFloor;
    }

    /**
     * 設置最大樓層
     * @param maxFloor 最大樓層
     */
    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    /**
     * 獲取最大節點數
     * @return 最大節點數
     */
    public int getTotalNodes() {
        return totalNodes;
    }

    /**
     * 設置總節點數
     * @param totalNodes 總節點數
     */
    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    /**
     * 獲取最大分支數
     * @return 最大分支數
     */
    public int getMaxBranches() {
        return maxBranches;
    }

    /**
     * 設置最大分支數
     * @param maxBranches 最大分支數
     */
    public void setMaxBranches(int maxBranches) {
        this.maxBranches = maxBranches;
    }

    /**
     * 獲取最大高度
     * @return 最大高度
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * 設置最大高度
     * @param maxHeight 最大高度
     */
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * 獲取特殊節點概率
     * @return 特殊節點概率
     */
    public double getSpecialNodeProbability() {
        return specialNodeProbability;
    }

    /**
     * 設置特殊節點概率
     * @param specialNodeProbability 特殊節點概率
     */
    public void setSpecialNodeProbability(double specialNodeProbability) {
        this.specialNodeProbability = specialNodeProbability;
    }

    /**
     * 獲取房間名稱列表
     * @return 房間名稱列表
     */
    public List<String> getRoomNames() {
        return roomNames;
    }

    /**
     * 設置房間名稱列表
     * @param roomNames 房間名稱列表
     */
    public void setRoomNames(List<String> roomNames) {
        this.roomNames = roomNames;
    }

    /**
     * 獲取Boss房間名稱列表
     * @return Boss房間名稱列表
     */
    public List<String> getBossRoomNames() {
        return bossRoomNames;
    }

    /**
     * 設置Boss房間名稱列表
     * @param bossRoomNames Boss房間名稱列表
     */
    public void setBossRoomNames(List<String> bossRoomNames) {
        this.bossRoomNames = bossRoomNames;
    }

    /**
     * 獲取地圖名稱
     * @return 地圖名稱
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * 獲取最大玩家數量
     * @return 最大玩家數量
     */
    public int getMaxPlayer() {
        return maxPlayer;
    }

    /**
     * 獲取最小玩家數量
     * @return 最小玩家數量
     */
    public int getMinPlayer() {
        return minPlayer;
    }

    /**
     * 設置最大玩家數量
     * @param maxPlayer 最大玩家數量
     */
    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    /**
     * 設置最小玩家數量
     * @param minPlayer 最小玩家數量
     */
    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }   

    /**
     * 設置地圖名稱
     * @param mapName 地圖名稱
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
} 