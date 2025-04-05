package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.path.Path;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath.AbstractsNode;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class PathGUI {

    private static final Map<Integer, List<Integer>> visibleslot = new HashMap<Integer, List<Integer>>() {{
        put(1, Arrays.asList(4));
        put(2, Arrays.asList(2, 6));
        put(3, Arrays.asList(1, 4, 7));
        put(4, Arrays.asList(1, 3, 5, 7));
        put(5, Arrays.asList(0, 2, 4, 6, 8));
        put(6, Arrays.asList(1, 2, 3, 5, 6, 7, 8));
    }};

    private final Path path;
    private final Map<Player, AbstractsNode> selectedNodes;
    private final Map<Player, Set<AbstractsNode>> availableNodes;
    private final Map<Player, Integer> currentPages;
    private final MCRogueLike mcroguelike;
    public PathGUI(AbstractsPath path2) {
        this.path = (Path) path2;
        this.selectedNodes = new HashMap<>();
        this.availableNodes = new HashMap<>();
        this.currentPages = new HashMap<>();
        this.mcroguelike = MCRogueLike.getInstance();
    }

    public void openGUI(Player player) {
        
        Inventory gui = Bukkit.createInventory(null, 54, "Path Selection");
        
        // 初始化玩家的可用節點（從根節點開始）
        Set<AbstractsNode> available = new HashSet<>();
        available.add(path.getRoot());
        availableNodes.put(player, available);
        
        // 設置初始頁面
        currentPages.put(player, 0);
        
        // 顯示所有節點
        displayNodes(gui, player);
        
        // 添加導航按鈕
        addNavigationButtons(gui, player);

        PathGUIHandler.getInstance().registerGUI(player, this);
        
        player.openInventory(gui);
    }

    private void setGrayGlass(Inventory gui) {
        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = grayGlass.getItemMeta();
        glassMeta.setDisplayName(" ");
        grayGlass.setItemMeta(glassMeta);
        
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, grayGlass);
        }   
    }

    public void displayNodes(Inventory gui, Player player) {
        // 先將所有槽位設置為灰色玻璃
        setGrayGlass(gui);
        
        Set<AbstractsNode> available = availableNodes.get(player);
        AbstractsNode selected = selectedNodes.get(player);
        int currentPage = currentPages.get(player);
        
        // 獲取按層級排序的節點
        Map<Integer, List<AbstractsNode>> nodesByLevel = getNodesByLevel();
        
        // 計算當前頁面要顯示的層級範圍
        int startLevel = currentPage * 5;
        int endLevel = Math.min(startLevel + 5, nodesByLevel.size());
        
        // 顯示每一層的節點
        for (int level = startLevel; level < endLevel; level++) {
            List<AbstractsNode> nodes = nodesByLevel.get(level);
            if (nodes == null || nodes.isEmpty()) continue;
            
            // 獲取該層級的顯示位置
            List<Integer> slots = visibleslot.get(Math.min(nodes.size(), 5));
            if (slots == null) continue;
            
            // 顯示該層級的所有節點
            for (int i = 0; i < Math.min(nodes.size(), slots.size()); i++) {
                AbstractsNode node = nodes.get(i);
                int slot = slots.get(i) + ((level - startLevel) * 9);
                
                // 創建節點物品
                ItemStack item;
                if (level == 0 && selected == null) {
                    // Node 0 is always selectable
                    item = createNodeItem(node, Material.DIAMOND, "§a可選擇");
                } else if (selected != null && selected.getChildren().contains(node)) {
                    // Only the children of the selected node are selectable
                    item = createNodeItem(node, Material.DIAMOND, "§a可選擇");
                } else {
                    // Other nodes are not selectable
                    item = createNodeItem(node, Material.GOLD_INGOT, "§c不可選擇");
                }
                
                gui.setItem(slot, item);
            }
        }
    }

    private Map<Integer, List<AbstractsNode>> getNodesByLevel() {
        Map<Integer, List<AbstractsNode>> nodesByLevel = new TreeMap<>();
        Queue<AbstractsNode> queue = new LinkedList<>();
        Set<AbstractsNode> visited = new HashSet<>();
        
        queue.add(path.getRoot());
        visited.add(path.getRoot());
        
        while (!queue.isEmpty()) {
            AbstractsNode current = queue.poll();
            int level = current.getLevel();
            
            // 將節點添加到對應層級的列表中
            nodesByLevel.computeIfAbsent(level, k -> new ArrayList<>()).add(current);
            
            // 添加子節點到隊列
            for (AbstractsNode child : current.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
        
        return nodesByLevel;
    }

    private int getTotalNodes() {
        int count = 0;
        Queue<AbstractsNode> queue = new LinkedList<>();
        Set<AbstractsNode> visited = new HashSet<>();
        
        queue.add(path.getRoot());
        visited.add(path.getRoot());
        
        while (!queue.isEmpty()) {
            AbstractsNode current = queue.poll();
            count++;
            
            for (AbstractsNode child : current.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
        
        return count;
    }

    public void addNavigationButtons(Inventory gui, Player player) {
        int currentPage = currentPages.get(player);
        int totalPages = (int) Math.ceil(path.getAllLevelNodes().size() / 5.0);
        
        // 上一頁按鈕
        if (currentPage > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName("§e上一頁");
            prevButton.setItemMeta(prevMeta);
            gui.setItem(45, prevButton);
        }
        
        // 下一頁按鈕
        if (currentPage < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName("§e下一頁");
            nextButton.setItemMeta(nextMeta);
            gui.setItem(53, nextButton);
        }
        
        // 頁面信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        pageMeta.setDisplayName("§6頁面 " + (currentPage + 1) + "/" + totalPages);
        pageInfo.setItemMeta(pageMeta);
        gui.setItem(49, pageInfo);
    }

    private ItemStack createNodeItem(AbstractsNode node, Material material, String status) {
        // 如果是特殊節點，使用紅石作為材質
        if (node.isSpecial()) {
            material = Material.REDSTONE;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bNode " + node.getValue());

        List<String> lore = new ArrayList<>();
        lore.add("§7Level: " + node.getLevel());
        if (node.isSpecial()) {
            lore.add("§c特殊節點");
        }
        lore.add(status);
        
        // 添加房間信息
        if (node.getRoom() != null) {
            lore.add("§7房間: " + node.getRoom().getName());
        }

        // 添加父代節點信息
        if (!node.getParents().isEmpty()) {
            lore.add("§7Parent Nodes:");
            for (AbstractsNode parent : node.getParents()) {
                lore.add("§7- Node " + parent.getValue());
            }
        }
        
        // 添加子節點信息
        if (!node.getChildren().isEmpty()) {
            lore.add("§7Child Nodes:");
            for (AbstractsNode child : node.getChildren()) {
                lore.add("§7- Node " + child.getValue());
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void handleNavigationClick(Player player, ItemStack clickedItem) {
        if (!currentPages.containsKey(player)) {
            openGUI(player);
            return;
        }
        
        int currentPage = currentPages.get(player);
        
        if (clickedItem.getItemMeta().getDisplayName().equals("§e上一頁")) {
            currentPages.put(player, currentPage - 1);
        } else if (clickedItem.getItemMeta().getDisplayName().equals("§e下一頁")) {
            currentPages.put(player, currentPage + 1);
        }
    }

    public void handleNodeClick(Player player, ItemStack clickedItem) {
        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (!itemName.startsWith("§bNode ")) return;
        
        int nodeValue = Integer.parseInt(itemName.substring(7));
        AbstractsNode clickedNode = findNodeByValue(nodeValue);
        
        if (clickedNode == null) return;
        
        if (!availableNodes.containsKey(player)) {
            openGUI(player);
            return;
        }
        
        Set<AbstractsNode> available = availableNodes.get(player);
        if (available != null && available.contains(clickedNode)) {
            // 更新已選節點
            selectedNodes.put(player, clickedNode);
            
            // 更新可用節點
            Set<AbstractsNode> newAvailable = new HashSet<>(available);
            newAvailable.remove(clickedNode);
            newAvailable.addAll(clickedNode.getChildren());
            availableNodes.put(player, newAvailable);
            
            // 開始遊戲
            startGame(player);
            
            player.sendMessage("§a已選擇 Node " + nodeValue);
        }
    }

    private void startGame(Player player) {
        // 獲取選中的節點
        AbstractsNode selectedNode = selectedNodes.get(player);
        if (selectedNode == null) {
            player.sendMessage("§c請先選擇一個節點");
            return;
        }

        // 獲取地圖
        AbstractsMap map = path.getMap();
        if (map == null) {
            player.sendMessage("§c沒有可用的地圖");
            return;
        }
        
        // 獲取房間
        AbstractsRoom room = selectedNode.getRoom();
        if (room == null) {
            player.sendMessage("§c沒有可用的房間");
            return;
        }
        
        // 計算位置
        Location baseLocation = map.getMapLocation().getLocation();
        if (baseLocation == null) {
            player.sendMessage("§c地圖位置無效");
            return;
        }

        // 計算生成點（根據間隔）
        int separation = map.getStructureSpawnSeparation();
        Location spawnLocation = calculateSpawnLocation(baseLocation, separation);
        
        // 傳送玩家
        player.teleport(spawnLocation);
        
        // 生成結構
        if (selectedNode.getRoom() != null) {
            selectedNode.getRoom().loadSchematics(spawnLocation);
            player.sendMessage("§a已生成房間: " + selectedNode.getRoom().getName());
            map.updateMapLocation();
        }

        // 關閉GUI
        player.closeInventory();
    }

    private Location calculateSpawnLocation(Location baseLocation, int separation) {
        // 根據間隔計算新的位置
        // 這裡可以根據需要調整計算邏輯
        return baseLocation.clone().add(separation, 0, 0);
    }

    public void cleanupPlayerData(Player player) {
        //刪除此物件
        selectedNodes.remove(player);
        availableNodes.remove(player);
        currentPages.remove(player);
    }

    private AbstractsNode findNodeByValue(int value) {
        Queue<AbstractsNode> queue = new LinkedList<>();
        queue.add(path.getRoot());
        Set<AbstractsNode> visited = new HashSet<>();
        visited.add(path.getRoot());

        while (!queue.isEmpty()) {
            AbstractsNode current = queue.poll();
            if (current.getValue() == value) {
                return current;
            }

            for (AbstractsNode child : current.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }

        return null;
    }
}
