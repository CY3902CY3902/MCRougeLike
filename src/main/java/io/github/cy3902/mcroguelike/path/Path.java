package io.github.cy3902.mcroguelike.path;

import java.util.*;
import java.util.logging.Level;

import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.files.PathFile;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
/**
 * Path類別擴展了AbstractsPath，用於生成具有指定節點數量和同階層分支限制的樹狀結構。
 */
public class Path extends AbstractsPath {

    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private final List<String> bossRoomNames;
    private final int maxNodes;
    private final int maxBranches;
    private final int maxHeight;
    private final double specialNodeProbability;
    private final List<AbstractsNode> nodes;
    private final String name;
    private final List<String> roomNames;
    private static final Random RANDOM = new Random();
    
    /**
     * 建構子，初始化Path類別，使用設定文件中的參數
     *
     * @param pathId 路徑ID
     * @param name 路徑名稱
     * @param map 地圖
     * @param maxNodes 最大節點數
     * @param maxBranches 最大分支數
     * @param maxHeight 最大高度
     * @param specialNodeProbability 特殊節點概率
     * @param roomNames 房間名稱列表
     * @param bossRoomNames Boss房間名稱列表
     */
    public Path(String pathId, String name, AbstractsMap map, int maxNodes, int maxBranches, int maxHeight, 
                double specialNodeProbability, List<String> roomNames, List<String> bossRoomNames) {
        super(pathId, maxNodes, maxBranches);
        this.name = name;
        this.map = map;
        this.maxNodes = maxNodes;
        this.maxBranches = maxBranches;
        this.maxHeight = maxHeight;
        this.specialNodeProbability = specialNodeProbability;
        this.nodes = new ArrayList<>();
        this.roomNames = new ArrayList<>(roomNames);
        this.bossRoomNames = new ArrayList<>(bossRoomNames);
        
    }


    public class Node extends AbstractsNode {
        public Node(int value, int level, boolean special) {
            super(value, level, special);
        }
        /** 
         * 獲取隨機的房間
         * @return 隨機的房間
         */
        
        @Override
        public AbstractsRoom RandomRoomByPath(int level) {
            List<String> validRooms = new ArrayList<>();
            for (String roomName : roomNames) {
                AbstractsRoom room = mcRogueLike.getRoomFile().getRoom(roomName);
                mcRogueLike.info("room: " + room, Level.INFO);
                mcRogueLike.info("roomName: " + roomName, Level.INFO);

                if(room == null) {
                    mcRogueLike.info("room is null: " + roomName, Level.INFO);
                    continue;
                }
                mcRogueLike.info("room.getMinFloor() <= level: " + room.getMinFloor() + " <= " + level, Level.INFO);
                if(room.getMinFloor() <= level && room.getMaxFloor() >= level) {
                    validRooms.add(roomName);
                }
            }

            if (validRooms.isEmpty()) {
                mcRogueLike.info("No valid rooms found for level: " + level, Level.WARNING);
                return null;
            }
            
            return mcRogueLike.getRoomFile().getRoom(validRooms.get(RANDOM.nextInt(validRooms.size())));
        }
    }


 

    @Override
    public List<AbstractsNode> getNodesByLevel(int level) {
        List<AbstractsNode> levelNodes = new ArrayList<>();
        for (AbstractsNode node : nodes) {
            if (node.getLevel() == level) {
                levelNodes.add(node);
            }
        }
        return levelNodes;
    }

    @Override
    public Map<Integer, List<AbstractsNode>> getAllLevelNodes() {
        Map<Integer, List<AbstractsNode>> levelMap = new HashMap<>();
        for (AbstractsNode node : nodes) {
            levelMap.computeIfAbsent(node.getLevel(), k -> new ArrayList<>()).add(node);
        }
        return levelMap;
    }

    /**
     * 生成路徑樹
     */
    public void generateTree() {
        // 清空現有節點
        nodes.clear();
        
        // 創建根節點
        Node root = new Node(0, 0, false);
        nodes.add(root);
        this.root = root;  // 設置根節點

        // 生成其他層級的節點
        mcRogueLike.info("maxHeight: " + maxHeight, Level.INFO);
        mcRogueLike.info("maxNodes: " + maxNodes, Level.INFO);
        mcRogueLike.info("maxBranches: " + maxBranches, Level.INFO);

        for (int level = 1; level < maxHeight; level++) {
            // 獲取上一層的所有節點
            List<AbstractsNode> parentNodes = getNodesByLevel(level - 1);
            if (parentNodes.isEmpty()) break;
            
            // 計算這一層還可以生成多少個節點
            int remainingNodes = maxNodes - nodes.size();
            if (remainingNodes <= 0) break;
            
            // 隨機決定這個層級要生成多少個節點
            int levelCount = getRandomBranchCount(Math.min(maxBranches, remainingNodes));
            List<AbstractsNode> levelNodes = new ArrayList<>();
            
            // 生成這一層的節點
            for (int i = 0; i < levelCount; i++) {
                Node child = new Node(nodes.size(), level, RANDOM.nextDouble() < specialNodeProbability);
                nodes.add(child);
                levelNodes.add(child);
            }

            // 確保每個子節點都有一個父節點
            List<AbstractsNode> availableChildren = new ArrayList<>(levelNodes);
            for (AbstractsNode child : availableChildren) {
                // 隨機選擇一個父節點
                AbstractsNode parent = parentNodes.get(RANDOM.nextInt(parentNodes.size()));
                parent.getChildren().add(child);
                child.getParents().add(parent);
            }

            // 為父節點添加額外的子節點
            for (AbstractsNode parent : parentNodes) {
                int additionalChildrenCount = getRandomBranchCount(maxBranches - parent.getChildren().size());
                List<AbstractsNode> remainingChildren = new ArrayList<>(levelNodes);
                remainingChildren.removeAll(parent.getChildren());
                
                for (int i = 0; i < additionalChildrenCount && !remainingChildren.isEmpty(); i++) {
                    AbstractsNode child = remainingChildren.remove(RANDOM.nextInt(remainingChildren.size()));
                    parent.getChildren().add(child);
                    child.getParents().add(parent);
                }
            }
        }
        
        // 添加最終節點
        if (nodes.size() < maxNodes) {
            Node finalNode = new Node(nodes.size(), maxHeight, true);
            nodes.add(finalNode);
            
            // 將所有上一層的節點連接到最終節點
            List<AbstractsNode> lastLevelNodes = getNodesByLevel(maxHeight - 1);
            for (AbstractsNode parent : lastLevelNodes) {
                parent.getChildren().add(finalNode);
                finalNode.getParents().add(parent);
            }
        }
        
        // 輸出節點關係以便調試
        printNodeRelationships();
    }

    /**
     * 獲取隨機的分支數
     * @param maxPossible 最大可能的分支數
     * @return 隨機的分支數
     */
    protected int getRandomBranchCount(int maxPossible) {
        if (maxPossible <= 0) return 0;
        if (maxPossible == 1) return 1;
        // 使用更均勻的隨機數生成
        return RANDOM.nextInt(maxPossible) + 1;
    }

    /**
     * 獲取路徑名稱
     * @return 路徑名稱
     */
    public String getName() {
        return name;
    }

    
    /**
     * 根據指定的索引創建一個新節點。
     *
     * @param i 新節點的索引。
     * @return 返回一個新的Node實例。
     */
    @Override
    protected AbstractsNode createNode(int i, int level, boolean special) {
        return new Node(i, level, special);
    }


    @Override
    protected void calculateNodeLevels() {
        // 層級已經在生成時設置，不需要重新計算
    }

    /**
     * 輸出所有節點及其父節點關係
     */
    private void printNodeRelationships() {
        MCRogueLike.getInstance().getLogger().info("=== Node Relationships ===");
        Set<AbstractsNode> visited = new HashSet<>();
        Queue<AbstractsNode> queue = new LinkedList<>();
        queue.add(root);
        visited.add(root);

        while (!queue.isEmpty()) {
            AbstractsNode current = queue.poll();
            StringBuilder sb = new StringBuilder();
            sb.append("Node").append(current.getValue())
              .append(" (Level ").append(current.getLevel()).append(")")
              .append(" -> Parents: ");
            
            if (current.getParents().isEmpty()) {
                sb.append("None");
            } else {
                for (AbstractsNode parent : current.getParents()) {
                    sb.append("Node").append(parent.getValue())
                      .append(" (Level ").append(parent.getLevel()).append("), ");
                }
                // 移除最後的逗號和空格
                sb.setLength(sb.length() - 2);
            }
            
            MCRogueLike.getInstance().getLogger().info(sb.toString());

            // 添加子節點到隊列
            for (AbstractsNode child : current.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
        MCRogueLike.getInstance().getLogger().info("========================");
    }

    /**
     * 獲取路徑ID
     * @return 路徑ID
     */
    public String getPathId() {
        return pathId;
    }

    /**
     * 獲取最大節點數
     * @return 最大節點數
     */
    public int getMaxNodes() {
        return maxNodes;
    }

    /**
     * 獲取最大分支數
     * @return 最大分支數
     */
    public int getMaxBranches() {
        return maxBranches;
    }

    /**
     * 獲取最大高度
     * @return 最大高度
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * 獲取特殊節點概率
     * @return 特殊節點概率
     */
    public double getSpecialNodeProbability() {
        return specialNodeProbability;
    }

    /**
     * 獲取房間名稱列表
     * @return 房間名稱列表
     */
    public List<String> getRoomNames() {
        return roomNames;
    }

    /**
     * 獲取Boss房間名稱列表
     * @return Boss房間名稱列表
     */
    public List<String> getBossRoomNames() {
        return bossRoomNames;
    }

    /**
     * 獲取地圖
     * @return 地圖名稱
     */
    public AbstractsMap getMap() {
        return map;
    }

}








