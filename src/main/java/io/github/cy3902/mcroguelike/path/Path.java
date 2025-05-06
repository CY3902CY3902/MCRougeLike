package io.github.cy3902.mcroguelike.path;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;
/**
 * Path類別擴展了AbstractsPath，用於生成具有指定節點數量和同階層分支限制的樹狀結構。
 */
public class Path extends AbstractPath {

    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private List<String> bossRoomIds;
    private int maxNodes;
    private int maxBranches;
    private int maxHeight;
    private double specialNodeProbability;
    private List<AbstractsNode> nodes;
    private String name;
    private List<String> roomIds;
    private static final Random RANDOM = new Random();
    
    /**
     * 建構子，初始化Path類別，使用設定文件中的參數
     */
    public Path() {
        super("default", 10, 10);
        this.name = "default";
        this.map = null;
        this.maxNodes = 0;
        this.maxBranches = 0;
        this.maxHeight = 0;
        this.specialNodeProbability = 0.5;
        this.nodes = new ArrayList<>();
        this.roomIds = new ArrayList<>();
        this.bossRoomIds = new ArrayList<>();
    }

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
     * @param roomIds 房間ID列表
     * @param bossRoomIds Boss房間ID列表
     */
    public Path(String pathId, String name, AbstractMap map, int maxNodes, int maxBranches, int maxHeight, 
                double specialNodeProbability, List<String> roomIds, List<String> bossRoomIds) {
        super(pathId, maxNodes, maxBranches);
        this.name = name;
        this.map = map;
        this.maxNodes = maxNodes;
        this.maxBranches = maxBranches;
        this.maxHeight = maxHeight;
        this.specialNodeProbability = specialNodeProbability;
        this.nodes = new ArrayList<>();
        this.roomIds = new ArrayList<>(roomIds);
        this.bossRoomIds = new ArrayList<>(bossRoomIds);
        
    }


    public class Node extends AbstractsNode {
        public Node(int value, int level, boolean special, boolean isCompleted) {
            super(value, level, special, isCompleted);
        }
        /** 
         * 獲取隨機的房間
         * @return 隨機的房間
         */
        
        @Override
        public AbstractRoom RandomRoomByPath(int level) {
            List<String> validRooms = new ArrayList<>();
            for (String roomId : roomIds) {
                AbstractRoom room = mcRogueLike.getRoomFile().getRoom(roomId);
                mcRogueLike.info("room: " + room, Level.INFO);
                mcRogueLike.info("roomId: " + roomId, Level.INFO);
                
                if(room == null) {
                    mcRogueLike.info("room is null: " + roomId, Level.INFO);
                    continue;
                }
                mcRogueLike.info("room.getMinFloor() <= level: " + room.getMinFloor() + " <= " + level, Level.INFO);
                if(room.getMinFloor() <= level && room.getMaxFloor() >= level) {
                    validRooms.add(roomId);
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
        Node root = createNode(0, 0, false, false);
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
                Node child = createNode(nodes.size(), level, RANDOM.nextDouble() < specialNodeProbability, false);
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
            Node finalNode = createNode(nodes.size(), maxHeight, false, false);
            finalNode.setRoom(finalNode.RandomRoomByPath(maxHeight));
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
     * @param level 新節點的層級。
     * @param special 新節點是否為特殊節點。
     * @param isCompleted 新節點是否已完成。
     * @return 返回一個新的Node實例。
     */
    @Override
    protected Node createNode(int i, int level, boolean special, boolean isCompleted) {
        Node node = new Node(i, level, special, isCompleted);
        node.setRoom(node.RandomRoomByPath(level));
        return node;
    }


    @Override
    public boolean convertPathFromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        
        // 創建節點映射
        Map<Integer, Node> nodeMap = new HashMap<>();
        JSONArray nodesArray = jsonObject.getJSONArray("nodes");

        this.map = mcRogueLike.getMapFile().getMap(jsonObject.getString("map"));
        this.maxNodes = jsonObject.getInt("maxNodes");
        this.maxBranches = jsonObject.getInt("maxBranches");
        this.maxHeight = jsonObject.getInt("maxHeight");
        this.specialNodeProbability = jsonObject.getDouble("specialNodeProbability");
        this.roomIds = jsonObject.getJSONArray("roomIds").toList().stream().map(Object::toString).collect(Collectors.toList());
        this.bossRoomIds = jsonObject.getJSONArray("bossRoomIds").toList().stream().map(Object::toString).collect(Collectors.toList());

        try {
            // 第一遍：創建所有節點
            for (int i = 0; i < nodesArray.length(); i++) {
                JSONObject nodeJson = nodesArray.getJSONObject(i);
                int nodeId = nodeJson.getInt("id");
                int level = nodeJson.getInt("level");
                boolean special = nodeJson.getBoolean("special");
                boolean isCompleted = nodeJson.getBoolean("isCompleted");

                Node node;
                node = new Node(nodeId, level, special, isCompleted);
                
                
                // 設置房間
                if (nodeJson.has("roomId")) {
                    String roomId = nodeJson.getString("roomId");
                    AbstractRoom room = mcRogueLike.getRoomFile().getRoom(roomId);
                    if (room != null) {
                        node.setRoom(room);
                    }else{
                        node.setRoom(node.RandomRoomByPath(level));
                    }
                }
                
                nodeMap.put(nodeId, node);
                
                // 如果是根節點，設置為路徑的根節點
                if (level == 0) {
                    root = node;
                }else{
                    nodes.add(node);
                }
            }
            
            // 第二遍：建立節點關係
            for (int i = 0; i < nodesArray.length(); i++) {
                JSONObject nodeJson = nodesArray.getJSONObject(i);
                int nodeId = nodeJson.getInt("id");
                AbstractPath.AbstractsNode node = nodeMap.get(nodeId);
                
                // 建立父節點關係
                JSONArray parentIds = nodeJson.getJSONArray("parentIds");
                for (int j = 0; j < parentIds.length(); j++) {
                    int parentId = parentIds.getInt(j);
                    AbstractPath.AbstractsNode parent = nodeMap.get(parentId);
                    if (parent != null) {
                        node.getParents().add(parent);
                        parent.getChildren().add(node);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 將路徑轉換為 JSON 字符串
     * @param path 要轉換的路徑
     * @return JSON 字符串
     */
    @Override
    public String convertPathToJson() {
        JSONObject json = new JSONObject();
        json.put("pathType", this.getClass().getSimpleName());
        json.put("pathId", this.getPathId());
        
        // 根據路徑類型保存特定屬性
        if (this instanceof Path) {
            Path concretePath = (Path) this;
            json.put("name", concretePath.getName());
            json.put("maxNodes", concretePath.getMaxNodes());
            json.put("maxBranches", concretePath.getMaxBranches());
            json.put("maxHeight", concretePath.getMaxHeight());
            json.put("specialNodeProbability", concretePath.getSpecialNodeProbability());
            json.put("roomIds", new JSONArray(concretePath.getRoomIds()));
            json.put("bossRoomIds", new JSONArray(concretePath.getBossRoomIds()));
            json.put("map", concretePath.getMap().getMapLocation().getLocation().getWorld().getName());
        }
        
        // 保存節點數據
        JSONArray nodesArray = new JSONArray();
        Map<Integer, List<AbstractPath.AbstractsNode>> allNodes = this.getAllLevelNodes();
        for (List<AbstractPath.AbstractsNode> levelNodes : allNodes.values()) {
            for (AbstractPath.AbstractsNode node : levelNodes) {
                JSONObject nodeJson = new JSONObject();
                nodeJson.put("id", node.getValue());
                nodeJson.put("level", node.getLevel());
                nodeJson.put("special", node.isSpecial());
                nodeJson.put("isCompleted", node.isCompleted());
                if (node.getRoom() != null) {
                    nodeJson.put("roomId", node.getRoom().getRoomId());
                }
                
                // 保存父節點ID
                JSONArray parentIds = new JSONArray();
                for (AbstractPath.AbstractsNode parent : node.getParents()) {
                    parentIds.put(parent.getValue());
                }
                nodeJson.put("parentIds", parentIds);
                
                // 保存子節點ID
                JSONArray childIds = new JSONArray();
                for (AbstractPath.AbstractsNode child : node.getChildren()) {
                    childIds.put(child.getValue());
                }
                nodeJson.put("childIds", childIds);
                
                nodesArray.put(nodeJson);
            }
        }
        json.put("nodes", nodesArray);
        
        return json.toString();
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
    public List<String> getRoomIds() {
        return roomIds;
    }

    /**
     * 獲取Boss房間名稱列表
     * @return Boss房間名稱列表
     */
    public List<String> getBossRoomIds() {
        return bossRoomIds;
    }

    /**
     * 獲取地圖
     * @return 地圖名稱
     */
    public AbstractMap getMap() {
        return map;
    }

}








