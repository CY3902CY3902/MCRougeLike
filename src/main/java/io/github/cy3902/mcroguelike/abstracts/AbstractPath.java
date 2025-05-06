package io.github.cy3902.mcroguelike.abstracts;

import io.github.cy3902.mcroguelike.MCRogueLike;


import java.util.*;

/**
 * AbstractsPath 是抽象路徑類別，定義了路徑的基本屬性和行為。
 * 包含了節點的基本結構和操作方法。
 */
public abstract class AbstractPath {
    protected final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    protected final String pathId;
    protected AbstractsNode root;
    protected final int totalNodes;
    protected final int maxBranches;
    protected final Map<Integer, List<AbstractsNode>> levelNodes;
    protected AbstractMap map;

    /**
     * 建構子，初始化路徑類別
     * @param pathId 路徑ID
     * @param totalNodes 總節點數
     * @param maxBranches 最大分支數
     */
    public AbstractPath(String pathId, int totalNodes, int maxBranches) {
        this.pathId = pathId;
        this.totalNodes = totalNodes;
        this.maxBranches = maxBranches;
        this.levelNodes = new HashMap<>();
        this.root = null;
        this.map = null;
    }


    /**
     * 獲取路徑ID
     * @return 路徑ID
     */
    public String getPathId() {
        return pathId;
    }

    /**
     * 生成樹狀結構
     */
    public abstract void generateTree();


    /**
     * 獲取隨機的分支數
     * @param maxPossible 最大可能的分支數
     * @return 隨機的分支數
     */
    protected int getRandomBranchCount(int maxPossible) {
        if (maxPossible <= 0) return 0;
        if (maxPossible == 1) return 1;
        return new Random().nextInt(maxPossible) + 1;
    }

    /**
     * 根據指定的索引創建一個新節點
     * @param i 新節點的索引
     * @param level 新節點的層級
     * @param special 新節點是否為特殊節點
     * @return 返回一個新的Node實例
     */
    protected abstract AbstractsNode createNode(int i, int level, boolean special, boolean isCompleted);


    /**
     * 獲取指定層級的所有節點
     * @param level 指定的層級
     * @return 該層級的所有節點列表
     */
    public List<AbstractsNode> getNodesByLevel(int level) {
        return levelNodes.getOrDefault(level, new ArrayList<>());
    }


    /**
     * 獲取所有層級的節點映射
     * @return 層級到節點列表的映射
     */
    public Map<Integer, List<AbstractsNode>> getAllLevelNodes() {
        return new HashMap<>(levelNodes);
    }

    /**
     * 獲取根節點
     * @return 根節點
     */
    public AbstractsNode getRoot() {
        return root;
    }

    /**
     * 設置根節點
     * @param node 根節點
     */
    public void setRoot(AbstractsNode node) {
        this.root = node;
    }

    /**
     * 獲取地圖
     * @return 地圖
     */
    public AbstractMap getMap() {
        return map;
    }

    /**
     * 設置地圖
     * @param map 地圖
     */
    public void setMap(AbstractMap map) {
        this.map = map;
    }


    /**
     * 從json 轉換
     * @return json
     */
    public abstract String convertPathToJson();


    /**
     * 從json 轉換
     * @param json json
     */
    public abstract boolean convertPathFromJson(String json);



    /**
     * 節點內部類，用於表示路徑中的節點
     */
    public class AbstractsNode {
        private final int value;
        private int level;
        private final List<AbstractsNode> parents;
        private final List<AbstractsNode> children;
        private boolean special;
        private AbstractRoom room;
        private boolean isCompleted;

        /**
         * 建構子，初始化節點
         * @param value 節點的值
         */
        public AbstractsNode(int value, int level, boolean special, boolean isCompleted) {
            this.value = value;
            this.level = level;
            this.parents = new ArrayList<>();
            this.children = new ArrayList<>();
            this.special = special;
            this.room = null;
            this.isCompleted = isCompleted;
        }

        /**
         * 設置節點的房間
         * @param room 要設置的房間
         */
        public void setRoom(AbstractRoom room) {
            this.room = room;
        }

        /**
         * 獲取節點的房間
         * @return 節點的房間
         */
        public AbstractRoom getRoom() {
            return room;
        }

        /**
         * 隨機獲取節點的房間
         * @return 節點的房間
         */
        public AbstractRoom RandomRoomByPath(int level) {
            return null;
        }


        /**
         * 獲取節點的值
         * @return 節點的值
         */
        public int getValue() {
            return value;
        }

        /**
         * 獲取節點的層級
         * @return 節點的層級
         */
        public int getLevel() {
            return level;
        }

        /**
         * 設置節點的層級
         * @param level 要設置的層級
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * 獲取父節點列表
         * @return 父節點列表
         */
        public List<AbstractsNode> getParents() {
            return parents;
        }

        /**
         * 獲取子節點列表
         * @return 子節點列表
         */
        public List<AbstractsNode> getChildren() {
            return children;
        }

        /**
         * 添加父節點
         * @param parent 要添加的父節點
         */
        public void addParent(AbstractsNode parent) {
            parents.add(parent);
        }

        /**
         * 檢查是否為特殊節點
         * @return 如果為特殊節點則返回true
         */
        public boolean isSpecial() {
            return special;
        }

        /**
         * 設置節點是否為特殊節點
         * @param special 要設置的特殊節點狀態
         */
        public void setSpecial(boolean special) {
            this.special = special;
        }

        /**
         * 獲取節點是否已完成
         * @return 如果已完成則返回true
         */
        public boolean isCompleted() {
            return isCompleted;
        }

        /**
         * 設置節點是否已完成
         * @param isCompleted 要設置的已完成狀態
         */
        public void setCompleted(boolean isCompleted) {
            this.isCompleted = isCompleted;
        }
    }
}
