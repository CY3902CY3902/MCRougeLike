package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.path.Path;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PathEditGUI {
    private static PathEditGUI instance;
    private final MCRogueLike plugin;
    private final File pathDir;
    private Path currentPath;
    private static final int ITEMS_PER_PAGE = 45; // 每頁顯示的路徑數量
    private final Map<UUID, Integer> playerPages; // 玩家UUID -> 當前頁碼

    public PathEditGUI() {
        this.plugin = MCRogueLike.getInstance();
        this.pathDir = new File(plugin.getDataFolder() + "/Path");
        this.playerPages = new HashMap<>();
    }

    public static PathEditGUI getInstance() {
        if (instance == null) {
            instance = new PathEditGUI();
        }
        return instance;
    }

    /**
     * 打開路徑編輯GUI
     * @param player 玩家
     * @param pathId 路徑ID
     */
    public void openPathEditGUI(Player player, String pathId) {
        // 加載路徑配置
        File file = new File(pathDir, pathId + ".yml");
        if (!file.exists()) {
            player.sendMessage(ChatColor.RED + "找不到路徑配置: " + pathId);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        currentPath = new Path(
            pathId,
            config.getString("name", "未命名路徑"),
            config.getInt("max_nodes", 10),
            config.getInt("max_branches", 3),
            config.getInt("max_height", 5),
            config.getDouble("special_node_probability", 0.2),
            config.getStringList("room_names"),
            config.getStringList("boss_room_names")
        );

        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "編輯路徑: " + currentPath.getName());

        // 添加路徑名稱編輯按鈕
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName(ChatColor.GOLD + "路徑名稱");
        List<String> nameLore = new ArrayList<>();
        nameLore.add(ChatColor.GRAY + "當前名稱: " + currentPath.getName());
        nameLore.add(ChatColor.YELLOW + "點擊修改名稱");
        nameMeta.setLore(nameLore);
        nameItem.setItemMeta(nameMeta);
        gui.setItem(10, nameItem);

        // 添加最大節點數編輯按鈕
        ItemStack maxNodesItem = new ItemStack(Material.DIAMOND);
        ItemMeta maxNodesMeta = maxNodesItem.getItemMeta();
        maxNodesMeta.setDisplayName(ChatColor.GOLD + "最大節點數");
        List<String> maxNodesLore = new ArrayList<>();
        maxNodesLore.add(ChatColor.GRAY + "當前值: " + currentPath.getMaxNodes());
        maxNodesLore.add(ChatColor.YELLOW + "點擊修改最大節點數");
        maxNodesMeta.setLore(maxNodesLore);
        maxNodesItem.setItemMeta(maxNodesMeta);
        gui.setItem(12, maxNodesItem);

        // 添加最大分支數編輯按鈕
        ItemStack maxBranchesItem = new ItemStack(Material.ACACIA_LOG);
        ItemMeta maxBranchesMeta = maxBranchesItem.getItemMeta();
        maxBranchesMeta.setDisplayName(ChatColor.GOLD + "最大分支數");
        List<String> maxBranchesLore = new ArrayList<>();
        maxBranchesLore.add(ChatColor.GRAY + "當前值: " + currentPath.getMaxBranches());
        maxBranchesLore.add(ChatColor.YELLOW + "點擊修改最大分支數");
        maxBranchesMeta.setLore(maxBranchesLore);
        maxBranchesItem.setItemMeta(maxBranchesMeta);
        gui.setItem(14, maxBranchesItem);

        // 添加最大高度編輯按鈕
        ItemStack maxHeightItem = new ItemStack(Material.LADDER);
        ItemMeta maxHeightMeta = maxHeightItem.getItemMeta();
        maxHeightMeta.setDisplayName(ChatColor.GOLD + "最大高度");
        List<String> maxHeightLore = new ArrayList<>();
        maxHeightLore.add(ChatColor.GRAY + "當前值: " + currentPath.getMaxHeight());
        maxHeightLore.add(ChatColor.YELLOW + "點擊修改最大高度");
        maxHeightMeta.setLore(maxHeightLore);
        maxHeightItem.setItemMeta(maxHeightMeta);
        gui.setItem(16, maxHeightItem);

        // 添加特殊節點概率編輯按鈕
        ItemStack probabilityItem = new ItemStack(Material.REDSTONE);
        ItemMeta probabilityMeta = probabilityItem.getItemMeta();
        probabilityMeta.setDisplayName(ChatColor.GOLD + "特殊節點概率");
        List<String> probabilityLore = new ArrayList<>();
        probabilityLore.add(ChatColor.GRAY + "當前值: " + currentPath.getSpecialNodeProbability());
        probabilityLore.add(ChatColor.YELLOW + "點擊修改特殊節點概率");
        probabilityMeta.setLore(probabilityLore);
        probabilityItem.setItemMeta(probabilityMeta);
        gui.setItem(28, probabilityItem);

        // 添加房間列表編輯按鈕
        ItemStack roomsItem = new ItemStack(Material.CHEST);
        ItemMeta roomsMeta = roomsItem.getItemMeta();
        roomsMeta.setDisplayName(ChatColor.GOLD + "房間列表");
        List<String> roomsLore = new ArrayList<>();
        roomsLore.add(ChatColor.GRAY + "當前房間數量: " + currentPath.getRoomNames().size());
        roomsLore.add(ChatColor.YELLOW + "點擊編輯房間列表");
        roomsMeta.setLore(roomsLore);
        roomsItem.setItemMeta(roomsMeta);
        gui.setItem(30, roomsItem);

        // 添加Boss房間列表編輯按鈕
        ItemStack bossRoomsItem = new ItemStack(Material.DRAGON_EGG);
        ItemMeta bossRoomsMeta = bossRoomsItem.getItemMeta();
        bossRoomsMeta.setDisplayName(ChatColor.GOLD + "Boss房間列表");
        List<String> bossRoomsLore = new ArrayList<>();
        bossRoomsLore.add(ChatColor.GRAY + "當前Boss房間數量: " + currentPath.getBossRoomNames().size());
        bossRoomsLore.add(ChatColor.YELLOW + "點擊編輯Boss房間列表");
        bossRoomsMeta.setLore(bossRoomsLore);
        bossRoomsItem.setItemMeta(bossRoomsMeta);
        gui.setItem(32, bossRoomsItem);

        // 添加保存按鈕
        ItemStack saveItem = new ItemStack(Material.EMERALD);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName(ChatColor.GREEN + "保存配置");
        saveItem.setItemMeta(saveMeta);
        gui.setItem(49, saveItem);

        // 添加返回按鈕
        ItemStack backItem = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "返回");
        backItem.setItemMeta(backMeta);
        gui.setItem(45, backItem);

        player.openInventory(gui);
    }

    /**
     * 更新路徑配置
     * @param key 配置鍵
     * @param value 配置值
     */
    public void updatePathConfig(String key, Object value) {
        if (currentPath == null) return;

        File file = new File(pathDir, currentPath.getPathId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(key, value);

        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("保存路徑配置時出錯: " + e.getMessage());
        }
    }

    /**
     * 獲取當前編輯的路徑
     * @return 當前路徑
     */
    public Path getCurrentPath() {
        return currentPath;
    }

    /**
     * 打開路徑管理GUI
     * @param player 玩家
     */
    public void openPathManagementGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "路徑管理");

        // 獲取所有路徑配置文件
        File[] files = pathDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            player.sendMessage(ChatColor.RED + "沒有找到任何路徑配置");
            return;
        }

        // 設置玩家的當前頁面
        int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);
        int totalPages = (int) Math.ceil(files.length / (double) ITEMS_PER_PAGE);
        
        // 顯示路徑列表
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, files.length);
        
        for (int i = startIndex; i < endIndex; i++) {
            File file = files[i];
            String pathId = file.getName().replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            ItemStack pathItem = new ItemStack(Material.PAPER);
            ItemMeta meta = pathItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + config.getString("name", "未命名路徑"));
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "ID: " + pathId);
            lore.add(ChatColor.GRAY + "最大節點數: " + config.getInt("max_nodes", 10));
            lore.add(ChatColor.GRAY + "最大分支數: " + config.getInt("max_branches", 3));
            lore.add(ChatColor.GRAY + "最大高度: " + config.getInt("max_height", 5));
            lore.add(ChatColor.YELLOW + "點擊編輯此路徑");
            meta.setLore(lore);
            
            pathItem.setItemMeta(meta);
            gui.setItem(i - startIndex, pathItem);
        }

        // 添加導航按鈕
        if (currentPage > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "上一頁");
            prevButton.setItemMeta(prevMeta);
            gui.setItem(45, prevButton);
        }

        if (currentPage < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName(ChatColor.YELLOW + "下一頁");
            nextButton.setItemMeta(nextMeta);
            gui.setItem(53, nextButton);
        }

        // 添加頁面信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "頁面 " + (currentPage + 1) + "/" + totalPages);
        pageInfo.setItemMeta(pageMeta);
        gui.setItem(49, pageInfo);

        // 添加創建新路徑按鈕
        ItemStack createItem = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createItem.getItemMeta();
        createMeta.setDisplayName(ChatColor.GREEN + "創建新路徑");
        createItem.setItemMeta(createMeta);
        gui.setItem(47, createItem);

        player.openInventory(gui);
    }

    /**
     * 處理導航按鈕點擊
     * @param player 玩家
     * @param clickedItem 被點擊的物品
     */
    public void handleNavigationClick(Player player, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = clickedItem.getItemMeta().getDisplayName();
        int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);

        if (displayName.equals(ChatColor.YELLOW + "上一頁")) {
            playerPages.put(player.getUniqueId(), currentPage - 1);
            openPathManagementGUI(player);
        } else if (displayName.equals(ChatColor.YELLOW + "下一頁")) {
            playerPages.put(player.getUniqueId(), currentPage + 1);
            openPathManagementGUI(player);
        }
    }
} 