package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.path.Path;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath.AbstractsNode;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.files.PathFile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;
import java.util.logging.Level;

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
    private final Lang lang;
    private final PathFile pathFile;

    public PathGUI(AbstractPath path2) {
        this.path = (Path) path2;
        this.selectedNodes = new HashMap<>();
        this.availableNodes = new HashMap<>();
        this.currentPages = new HashMap<>();
        this.mcroguelike = MCRogueLike.getInstance();
        this.lang = mcroguelike.getLang();
        this.pathFile = mcroguelike.getPathFile();
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, lang.getMessage("path.gui.title"));
        
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
                    item = createNodeItem(node, Material.DIAMOND, lang.getMessage("path.gui.selectable"));
                } else if (selected != null && selected.getChildren().contains(node)) {
                    // Only the children of the selected node are selectable
                    item = createNodeItem(node, Material.DIAMOND, lang.getMessage("path.gui.selectable"));
                } else {
                    // Other nodes are not selectable
                    item = createNodeItem(node, Material.GOLD_INGOT, lang.getMessage("path.gui.not_selectable"));
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
            prevMeta.setDisplayName(lang.getMessage("path.gui.previous_page"));
            prevButton.setItemMeta(prevMeta);
            gui.setItem(45, prevButton);
        }
        
        // 下一頁按鈕
        if (currentPage + 1 < totalPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(lang.getMessage("path.gui.next_page"));
            nextButton.setItemMeta(nextMeta);
            gui.setItem(53, nextButton);
        }
        
        // 頁面信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        pageMeta.setDisplayName(lang.getMessage("path.gui.page_info")
            .replace("%current%", String.valueOf(currentPage + 1))
            .replace("%total%", String.valueOf(totalPages)));
        pageInfo.setItemMeta(pageMeta);
        gui.setItem(49, pageInfo);


        // 刪除路徑
        ItemStack deletePath = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = deletePath.getItemMeta();
        deleteMeta.setDisplayName(lang.getMessage("path.gui.delete_path"));
        deletePath.setItemMeta(deleteMeta);
        gui.setItem(47, deletePath);
    }

    private ItemStack createNodeItem(AbstractsNode node, Material material, String status) {
        // 如果是特殊節點，使用紅石作為材質
        if (node.isSpecial()) {
            material = Material.REDSTONE;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(lang.getMessage("path.gui.node_name").replace("%value%", String.valueOf(node.getValue())));

        List<String> lore = new ArrayList<>();
        lore.add(lang.getMessage("path.gui.node_level").replace("%level%", String.valueOf(node.getLevel())));
        if (node.isSpecial()) {
            lore.add(lang.getMessage("path.gui.special_node"));
        }
        lore.add(status);
        
        // 添加房間信息
        if (node.getRoom() != null) {
            lore.add(lang.getMessage("path.gui.room_info").replace("%name%", node.getRoom().getName()));
        }

        // 添加父代節點信息
        if (!node.getParents().isEmpty()) {
            lore.add(lang.getMessage("path.gui.parent_nodes"));
            for (AbstractsNode parent : node.getParents()) {
                lore.add(lang.getMessage("path.gui.parent_node").replace("%value%", String.valueOf(parent.getValue())));
            }
        }
        
        // 添加子節點信息
        if (!node.getChildren().isEmpty()) {
            lore.add(lang.getMessage("path.gui.child_nodes"));
            for (AbstractsNode child : node.getChildren()) {
                lore.add(lang.getMessage("path.gui.child_node").replace("%value%", String.valueOf(child.getValue())));
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    public void cleanupPlayerData(Player player) {
        //刪除此物件
        selectedNodes.remove(player);
        availableNodes.remove(player);
        currentPages.remove(player);
    }

    public AbstractsNode findNodeByValue(int value) {
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


    


    public Map<Player, AbstractsNode> getSelectedNodes() {
        return selectedNodes;
    }

    public Map<Player, Set<AbstractsNode>> getAvailableNodes() {
        return availableNodes;
    }

    public Map<Player, Integer> getCurrentPages() {
        return currentPages;
    }

    public AbstractPath getPath() {
        return path;
    }
}
