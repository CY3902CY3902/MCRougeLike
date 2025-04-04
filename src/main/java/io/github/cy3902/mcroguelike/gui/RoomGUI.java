package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.room.AnnihilationRoom;
import io.github.cy3902.mcroguelike.room.SurvivalRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class RoomGUI {
    private final MCRogueLike plugin;
    private final File roomDir;
    private final Map<String, FileConfiguration> roomConfigs;
    private static final int ITEMS_PER_PAGE = 45; // 每頁顯示的物品數量
    private final Map<UUID, Integer> playerPages; // 玩家UUID -> 當前頁碼

    public RoomGUI() {
        this.plugin = MCRogueLike.getInstance();
        this.roomDir = new File(plugin.getDataFolder() + "/Room");
        this.roomConfigs = new HashMap<>();
        this.playerPages = new HashMap<>();
        loadConfigs();
    }

    /**
     * 加載所有房間配置
     */
    private void loadConfigs() {
        // 確保目錄存在
        if (!roomDir.exists()) {
            roomDir.mkdirs();
        }

        // 加載所有yml文件
        File[] files = roomDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String roomId = file.getName().replace(".yml", "");
                roomConfigs.put(roomId, YamlConfiguration.loadConfiguration(file));
                MCRogueLike.getInstance().info(roomId, Level.INFO);
            }
        }
    }

    /**
     * 保存房間配置
     * @param roomId 房間ID
     * @param config 配置
     */
    private void saveConfig(String roomId, FileConfiguration config) {
        try {
            File file = new File(roomDir, roomId + ".yml");
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("無法保存房間配置 " + roomId + ": " + e.getMessage());
        }
    }

    /**
     * 獲取房間配置
     * @param roomId 房間ID
     * @return FileConfiguration
     */
    public FileConfiguration getRoomConfig(String roomId) {
        return roomConfigs.get(roomId);
    }

    /**
     * 打開房間管理GUI
     * @param player 玩家
     * @param page 頁碼（從0開始）
     */
    public void openRoomGUI(Player player, int page) {
        List<String> roomIds = new ArrayList<>(roomConfigs.keySet());
        int totalPages = (int) Math.ceil((double) roomIds.size() / ITEMS_PER_PAGE);
        
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        playerPages.put(player.getUniqueId(), page);
        
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "房間管理系統 - 第 " + (page + 1) + " 頁");

        // 添加房間列表
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, roomIds.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            String roomId = roomIds.get(i);
            ItemStack roomItem = createRoomItem(roomId);
            if (roomItem != null) {
                gui.setItem(i - startIndex, roomItem);
            }
        }

        // 添加上一頁按鈕
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(ChatColor.GREEN + "上一頁");
            prevPage.setItemMeta(prevMeta);
            gui.setItem(45, prevPage);
        }

        // 添加下一頁按鈕
        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GREEN + "下一頁");
            nextPage.setItemMeta(nextMeta);
            gui.setItem(53, nextPage);
        }

        // 添加創建新房間按鈕
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(ChatColor.GREEN + "創建新房間");
        createButton.setItemMeta(createMeta);
        gui.setItem(49, createButton);

        player.openInventory(gui);
    }

    /**
     * 打開房間管理GUI（默認第一頁）
     * @param player 玩家
     */
    public void openRoomGUI(Player player) {
        openRoomGUI(player, 0);
    }

    /**
     * 獲取玩家的當前頁碼
     * @param player 玩家
     * @return 當前頁碼
     */
    public int getPlayerPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * 創建房間物品
     * @param roomId 房間ID
     * @return ItemStack
     */
    private ItemStack createRoomItem(String roomId) {
        FileConfiguration config = getRoomConfig(roomId);
        if (config == null) return null;

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + config.getString("name", roomId));
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "ID: " + roomId);
        lore.add(ChatColor.GRAY + "類型: " + config.getString("type", "未知"));
        lore.add(ChatColor.GRAY + "結構: " + config.getString("structure", "未知"));
        lore.add(ChatColor.GRAY + "時限: " + config.getInt("time_limit", 0) + "秒");
        lore.add(ChatColor.GRAY + "基礎分數: " + config.getInt("baseScore", 0));
        lore.add(ChatColor.GRAY + "玩家出生點: " + config.getString("player_spawn", "未設置"));
        lore.add("");
        lore.add(ChatColor.YELLOW + "左鍵點擊: 編輯房間");
        lore.add(ChatColor.RED + "右鍵點擊: 刪除房間");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 打開房間編輯GUI
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void openRoomEditGUI(Player player, String roomId) {
        // 確保編輯狀態被設置
        RoomGUIHandler.getInstance().setEditingPlayer(player.getUniqueId(), roomId);
        
        FileConfiguration config = getRoomConfig(roomId);
        if (config == null) {
            player.sendMessage(ChatColor.RED + "無法找到房間配置！");
            return;
        }

        String roomName = config.getString("name", "未命名房間");
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "編輯房間: " + roomName);

        // 房間名稱
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName(ChatColor.GREEN + "房間名稱");
        List<String> nameLore = new ArrayList<>();
        nameLore.add(ChatColor.GRAY + "當前名稱: " + ChatColor.WHITE + roomName);
        nameMeta.setLore(nameLore);
        nameItem.setItemMeta(nameMeta);
        gui.setItem(10, nameItem);

        // 房間類型
        ItemStack typeItem = new ItemStack(Material.BOOK);
        ItemMeta typeMeta = typeItem.getItemMeta();
        typeMeta.setDisplayName(ChatColor.GREEN + "房間類型");
        List<String> typeLore = new ArrayList<>();
        typeLore.add(ChatColor.GRAY + "當前類型: " + ChatColor.WHITE + config.getString("type", "未知"));
        typeMeta.setLore(typeLore);
        typeItem.setItemMeta(typeMeta);
        gui.setItem(11, typeItem);

        // 結構名稱
        ItemStack structureItem = new ItemStack(Material.STRUCTURE_BLOCK);
        ItemMeta structureMeta = structureItem.getItemMeta();
        structureMeta.setDisplayName(ChatColor.GREEN + "結構名稱");
        List<String> structureLore = new ArrayList<>();
        structureLore.add(ChatColor.GRAY + "當前結構: " + ChatColor.WHITE + config.getString("structure", "未設置"));
        structureMeta.setLore(structureLore);
        structureItem.setItemMeta(structureMeta);
        gui.setItem(12, structureItem);

        // 時限
        ItemStack timeItem = new ItemStack(Material.CLOCK);
        ItemMeta timeMeta = timeItem.getItemMeta();
        timeMeta.setDisplayName(ChatColor.GREEN + "時限");
        List<String> timeLore = new ArrayList<>();
        timeLore.add(ChatColor.GRAY + "當前時限: " + ChatColor.WHITE + config.getInt("time_limit", 300) + " 秒");
        timeMeta.setLore(timeLore);
        timeItem.setItemMeta(timeMeta);
        gui.setItem(13, timeItem);

        // 基礎分數
        ItemStack scoreItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta scoreMeta = scoreItem.getItemMeta();
        scoreMeta.setDisplayName(ChatColor.GREEN + "基礎分數");
        List<String> scoreLore = new ArrayList<>();
        scoreLore.add(ChatColor.GRAY + "當前分數: " + ChatColor.WHITE + config.getInt("baseScore", 100));
        scoreMeta.setLore(scoreLore);
        scoreItem.setItemMeta(scoreMeta);
        gui.setItem(14, scoreItem);

        // 玩家出生點
        ItemStack spawnItem = new ItemStack(Material.COMPASS);
        ItemMeta spawnMeta = spawnItem.getItemMeta();
        spawnMeta.setDisplayName(ChatColor.GREEN + "玩家出生點");
        List<String> spawnLore = new ArrayList<>();
        spawnLore.add(ChatColor.GRAY + "當前位置: " + ChatColor.WHITE + config.getString("player_spawn", "未設置"));
        spawnMeta.setLore(spawnLore);
        spawnItem.setItemMeta(spawnMeta);
        gui.setItem(15, spawnItem);

        // 最小樓層
        ItemStack minFloorItem = new ItemStack(Material.STONE);
        ItemMeta minFloorMeta = minFloorItem.getItemMeta();
        minFloorMeta.setDisplayName(ChatColor.GREEN + "最小樓層");
        List<String> minFloorLore = new ArrayList<>();
        minFloorLore.add(ChatColor.GRAY + "當前最小樓層: " + ChatColor.WHITE + config.getInt("floor.min", 1));
        minFloorMeta.setLore(minFloorLore);
        minFloorItem.setItemMeta(minFloorMeta);
        gui.setItem(16, minFloorItem);

        // 最大樓層
        ItemStack maxFloorItem = new ItemStack(Material.STONE_BRICKS);
        ItemMeta maxFloorMeta = maxFloorItem.getItemMeta();
        maxFloorMeta.setDisplayName(ChatColor.GREEN + "最大樓層");
        List<String> maxFloorLore = new ArrayList<>();
        maxFloorLore.add(ChatColor.GRAY + "當前最大樓層: " + ChatColor.WHITE + config.getInt("floor.max", 1));
        maxFloorMeta.setLore(maxFloorLore);
        maxFloorItem.setItemMeta(maxFloorMeta);
        gui.setItem(17, maxFloorItem);

        // 保存按鈕
        ItemStack saveItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName(ChatColor.GREEN + "保存更改");
        saveItem.setItemMeta(saveMeta);
        gui.setItem(26, saveItem);

        player.openInventory(gui);
    }

    /**
     * 更新房間配置（僅更新內存中的配置）
     * @param roomId 房間ID
     * @param key 配置鍵
     * @param value 配置值
     */
    public void updateRoomConfig(String roomId, String key, Object value) {
        FileConfiguration config = getRoomConfig(roomId);
        if (config == null) return;
        config.set(key, value);
    }

    /**
     * 保存所有房間配置到文件
     */
    public void saveAllConfigs() {
        for (Map.Entry<String, FileConfiguration> entry : roomConfigs.entrySet()) {
            try {
                File file = new File(roomDir, entry.getKey() + ".yml");
                entry.getValue().save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("無法保存房間配置 " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }


    /**
     * 創建新房間
     * @param roomId 房間ID
     * @param type 房間類型
     */
    public void createNewRoom(String roomId, String type) {
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", "新房間");
        config.set("type", type);
        config.set("structure", "default_structure");
        config.set("time_limit", 300);
        config.set("baseScore", 100);
        config.set("player_spawn", "0,64,0");
        config.set("floor.min", 0);
        config.set("floor.max", 10);
        
        roomConfigs.put(roomId, config);
        saveConfig(roomId, config);
        reloadConfigs();
    }

    /**
     * 重新加載所有房間配置
     */
    public void reloadConfigs() {
        roomConfigs.clear();
        loadConfigs();
    }

    /**
     * 獲取所有房間配置
     * @return Map<String, FileConfiguration>
     */
    public Map<String, FileConfiguration> getRoomConfigs() {
        return roomConfigs;
    }

    /**
     * 刪除房間
     * @param roomId 房間ID
     */
    public void deleteRoom(String roomId) {
        File file = new File(roomDir, roomId + ".yml");
        if (file.exists()) {
            file.delete();
        }
        AbstractsRoom room = plugin.getRoomManager().getRoom(roomId);
        if (room != null) {
            plugin.getRoomManager().removeRoom(room);
        }
        roomConfigs.remove(roomId);
        reloadConfigs(); // 重新加載配置
    }

    /**
     * 保存指定房間的配置
     * @param roomId 房間ID
     */
    public void saveRoomConfig(String roomId) {
        FileConfiguration config = getRoomConfig(roomId);
        if (config == null) return;
        
        try {
            File file = new File(roomDir, roomId + ".yml");
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("無法保存房間配置 " + roomId + ": " + e.getMessage());
        }
    }
} 