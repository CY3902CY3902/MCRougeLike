package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.files.RoomFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class RoomGUI {
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private final File roomDir;
    private static final int ITEMS_PER_PAGE = 45; // 每頁顯示的物品數量
    private final Map<UUID, Integer> playerPages; // 玩家UUID -> 當前頁碼
    private final Lang lang;
    private final RoomFile roomFile;

    public RoomGUI() {
        this.roomDir = new File(mcroguelike.getDataFolder() + "/Room");
        this.playerPages = new HashMap<>();
        this.lang = mcroguelike.getLang();
        this.roomFile = mcroguelike.getRoomFile();
    }



    /**
     * 打開房間管理GUI
     * @param player 玩家
     * @param page 頁碼（從0開始）
     */
    public void openRoomGUI(Player player, int page) {
        List<String> roomIds = new ArrayList<>(roomFile.getAllConfigs().keySet());
        int totalPages = (int) Math.ceil((double) roomIds.size() / ITEMS_PER_PAGE);
        
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        
        playerPages.put(player.getUniqueId(), page);
        
        Inventory gui = Bukkit.createInventory(null, 54, lang.getMessage("room.gui.title") + " - " + lang.getMessage("room.gui.page_info") + (page + 1));

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
            prevMeta.setDisplayName(lang.getMessage("room.gui.prev_page"));
            prevPage.setItemMeta(prevMeta);
            gui.setItem(45, prevPage);
        }

        // 添加下一頁按鈕
        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(lang.getMessage("room.gui.next_page"));
            nextPage.setItemMeta(nextMeta);
            gui.setItem(53, nextPage);
        }

        // 添加頁面信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        pageMeta.setDisplayName(lang.getMessage("room.gui.page_info") + (page + 1) + "/" + totalPages);
        pageInfo.setItemMeta(pageMeta);
        gui.setItem(49, pageInfo);

        // 添加創建新房間按鈕
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(lang.getMessage("room.gui.create_new"));
        createButton.setItemMeta(createMeta);
        gui.setItem(47, createButton);

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
        RoomConfig  config = roomFile.getConfig(roomId);
        if (config == null) return null;

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + config.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.id") + roomId);
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.type") + config.getType());
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.structure_info") + config.getStructure());
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.time_limit_info") + config.getTimeLimit() + lang.getMessage("room.gui.seconds"));
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.base_score_info") + config.getBaseScore());
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.player_spawn_info") + config.getPlayerSpawn());
        lore.add("");
        lore.add(lang.getMessage("room.gui.click_to_edit"));
        lore.add(lang.getMessage("room.gui.click_to_delete"));
        
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
        if (roomFile.getConfig(roomId) == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found") + ":" + roomId);
            return;
        }
        
        // 確保編輯狀態被設置
        RoomGUIHandler.getInstance().setEditingPlayer(player.getUniqueId(), roomId);
        
        RoomConfig config = roomFile.getConfig(roomId);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.config_not_found"));
            return;
        }

        String roomName = config.getName();
        Inventory gui = Bukkit.createInventory(null, 27, lang.getMessage("room.gui.edit_title") + roomName);

        // 房間名稱
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName(lang.getMessage("room.gui.room_name"));
        List<String> nameLore = new ArrayList<>();
        nameLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + roomName);
        nameMeta.setLore(nameLore);
        nameItem.setItemMeta(nameMeta);
        gui.setItem(9, nameItem);

        // 房間類型
        ItemStack typeItem = new ItemStack(Material.BOOK);
        ItemMeta typeMeta = typeItem.getItemMeta();
        typeMeta.setDisplayName(lang.getMessage("room.gui.room_type"));
        List<String> typeLore = new ArrayList<>();
        typeLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getType());
        typeMeta.setLore(typeLore);
        typeItem.setItemMeta(typeMeta);
        gui.setItem(10, typeItem);

        // 結構名稱
        ItemStack structureItem = new ItemStack(Material.STRUCTURE_BLOCK);
        ItemMeta structureMeta = structureItem.getItemMeta();
        structureMeta.setDisplayName(lang.getMessage("room.gui.structure"));
        List<String> structureLore = new ArrayList<>();
        structureLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getStructure());
        structureMeta.setLore(structureLore);
        structureItem.setItemMeta(structureMeta);
        gui.setItem(11, structureItem);

        // 時限
        ItemStack timeItem = new ItemStack(Material.CLOCK);
        ItemMeta timeMeta = timeItem.getItemMeta();
        timeMeta.setDisplayName(lang.getMessage("room.gui.time_limit"));
        List<String> timeLore = new ArrayList<>();
        timeLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getTimeLimit() + lang.getMessage("room.gui.seconds"));
        timeMeta.setLore(timeLore);
        timeItem.setItemMeta(timeMeta);
        gui.setItem(12, timeItem);

        // 基礎分數
        ItemStack scoreItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta scoreMeta = scoreItem.getItemMeta();
        scoreMeta.setDisplayName(lang.getMessage("room.gui.base_score"));
        List<String> scoreLore = new ArrayList<>();
        scoreLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getBaseScore());
        scoreMeta.setLore(scoreLore);
        scoreItem.setItemMeta(scoreMeta);
        gui.setItem(13, scoreItem);

        // 玩家出生點
        ItemStack spawnItem = new ItemStack(Material.COMPASS);
        ItemMeta spawnMeta = spawnItem.getItemMeta();
        spawnMeta.setDisplayName(lang.getMessage("room.gui.player_spawn"));
        List<String> spawnLore = new ArrayList<>();
        spawnLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getPlayerSpawn());
        spawnMeta.setLore(spawnLore);
        spawnItem.setItemMeta(spawnMeta);
        gui.setItem(14, spawnItem);

        // 最小樓層
        ItemStack minFloorItem = new ItemStack(Material.STONE);
        ItemMeta minFloorMeta = minFloorItem.getItemMeta();
        minFloorMeta.setDisplayName(lang.getMessage("room.gui.min_floor"));
        List<String> minFloorLore = new ArrayList<>();
        minFloorLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getMinFloor());
        minFloorMeta.setLore(minFloorLore);
        minFloorItem.setItemMeta(minFloorMeta);
        gui.setItem(15, minFloorItem);

        // 最大樓層
        ItemStack maxFloorItem = new ItemStack(Material.STONE_BRICKS);
        ItemMeta maxFloorMeta = maxFloorItem.getItemMeta();
        maxFloorMeta.setDisplayName(lang.getMessage("room.gui.max_floor"));
        List<String> maxFloorLore = new ArrayList<>();
        maxFloorLore.add(ChatColor.GRAY + lang.getMessage("room.gui.current") + ": " + ChatColor.WHITE + config.getMaxFloor());
        maxFloorMeta.setLore(maxFloorLore);
        maxFloorItem.setItemMeta(maxFloorMeta);
        gui.setItem(16, maxFloorItem);

        //怪物出生點設置
        ItemStack mobSpawnItem = new ItemStack(Material.SPAWNER);
        ItemMeta mobSpawnMeta = mobSpawnItem.getItemMeta();
        mobSpawnMeta.setDisplayName(lang.getMessage("room.gui.mob_spawn"));
        List<String> mobSpawnLore = new ArrayList<>();
        mobSpawnLore.add( ChatColor.WHITE + lang.getMessage("room.gui.mob_spawn_enter"));
        mobSpawnMeta.setLore(mobSpawnLore);
        mobSpawnItem.setItemMeta(mobSpawnMeta);
        gui.setItem(17, mobSpawnItem);

        // 保存按鈕
        ItemStack saveItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName(lang.getMessage("room.gui.save"));
        saveItem.setItemMeta(saveMeta);
        gui.setItem(26, saveItem);

        player.openInventory(gui);
    }



    /**
     * 創建新房間
     * @param roomId 房間ID
     * @param type 房間類型
     */
    public void createNewRoom(String roomId, String type) {

        RoomConfig config =  new RoomConfig(roomId, "new_room", type, "default", "default", 
        300, 100, 1.0, "0,64,0", 0, 10, new ArrayList<>());
        roomFile.saveRoom(roomId, config);
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
        roomFile.removeProvider(roomId);
    }

    /**
     * 保存指定房間的配置
     * @param roomId 房間ID
     */
    public void saveRoomConfig(String roomId) {
        roomFile.saveRoom(roomId, roomFile.getConfig(roomId));
    }
} 