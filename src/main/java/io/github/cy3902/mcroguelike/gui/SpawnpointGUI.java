package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.files.RoomFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class SpawnpointGUI {
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private final File roomDir;
    private static final int ITEMS_PER_PAGE = 45; // 每頁顯示的物品數量
    private final Map<UUID, Integer> playerPages; // 玩家UUID -> 當前頁碼
    private final Lang lang;
    private final RoomFile roomFile;

    public SpawnpointGUI() {
        this.roomDir = new File(mcroguelike.getDataFolder() + "/Room");
        this.playerPages = new HashMap<>();
        this.lang = mcroguelike.getLang();
        this.roomFile = mcroguelike.getRoomFile();
    }

    /**
     * 打開生成點管理GUI
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void openSpawnpointGUI(Player player, String roomId) {
        RoomConfig config = roomFile.getConfig(roomId);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        List<Map<String, String>> spawnPoints = config.getSpawnpoints();
        if (spawnPoints == null) {
            spawnPoints = new ArrayList<>();
        }

        Inventory gui = Bukkit.createInventory(null, 54, lang.getMessage("room.gui.title") + " - " + lang.getMessage("room.gui.mob_spawn"));

        // 添加生成點列表
        for (int i = 0; i < spawnPoints.size() && i < ITEMS_PER_PAGE; i++) {
            Map<String, String> spawnPoint = spawnPoints.get(i);
            ItemStack spawnItem = createSpawnpointItem(spawnPoint);
            gui.setItem(i, spawnItem);
        }

        // 添加創建新生成點按鈕
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(lang.getMessage("room.gui.create_new_spawnpoint"));
        createButton.setItemMeta(createMeta);
        gui.setItem(47, createButton);

        // 添加返回按鈕
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(lang.getMessage("room.gui.back"));
        backButton.setItemMeta(backMeta);
        gui.setItem(49, backButton);

        player.openInventory(gui);
    }

    /**
     * 創建生成點物品
     * @param spawnPoint 生成點配置
     * @return ItemStack
     */
    private ItemStack createSpawnpointItem(Map<String, String> spawnPoint) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();
        
        String name = spawnPoint.get("name");
        String location = spawnPoint.get("location");
        
        meta.setDisplayName(ChatColor.GOLD + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + lang.getMessage("room.gui.location") + location);
        lore.add("");
        lore.add(lang.getMessage("room.gui.click_to_edit"));
        lore.add(lang.getMessage("room.gui.click_to_delete"));
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 創建新生成點
     * @param player 玩家
     * @param name 生成點名稱
     * @param location 生成點位置
     */
    public void createSpawnpoint(Player player, String name, String location) {
        String roomId = SpawnpointGUIHandler.getInstance().getEditingRoomId(player);
        if (roomId == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        RoomConfig config = roomFile.getConfig(roomId);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        List<Map<String, String>> spawnPoints = config.getSpawnpoints();
        if (spawnPoints == null) {
            spawnPoints = new ArrayList<>();
        }

        Map<String, String> newSpawnPoint = new HashMap<>();
        newSpawnPoint.put("name", name);
        newSpawnPoint.put("location", location);
        spawnPoints.add(newSpawnPoint);

        config.setSpawnpoints(spawnPoints);
        roomFile.saveRoom(roomId, config);
        player.sendMessage(lang.getMessage("room.gui.changes_saved"));
    }

    /**
     * 編輯生成點
     * @param player 玩家
     * @param index 生成點索引
     * @param name 新名稱
     * @param location 新位置
     */
    public void editSpawnpoint(Player player, int index, String name, String location) {
        String roomId = SpawnpointGUIHandler.getInstance().getEditingRoomId(player);
        if (roomId == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        RoomConfig config = roomFile.getConfig(roomId);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        List<Map<String, String>> spawnPoints = config.getSpawnpoints();
        if (spawnPoints == null || index < 0 || index >= spawnPoints.size()) {
            player.sendMessage(lang.getMessage("room.gui.invalid_setting"));
            return;
        }

        Map<String, String> spawnPoint = spawnPoints.get(index);
        spawnPoint.put("name", name);
        spawnPoint.put("location", location);

        config.setSpawnpoints(spawnPoints);
        roomFile.saveRoom(roomId, config);
        player.sendMessage(lang.getMessage("room.gui.changes_saved"));
    }

    /**
     * 刪除生成點
     * @param player 玩家
     * @param index 生成點索引
     */
    public void deleteSpawnpoint(Player player, int index) {
        String roomId = SpawnpointGUIHandler.getInstance().getEditingRoomId(player);
        if (roomId == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        RoomConfig config = roomFile.getConfig(roomId);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        List<Map<String, String>> spawnPoints = config.getSpawnpoints();
        if (spawnPoints == null || index < 0 || index >= spawnPoints.size()) {
            player.sendMessage(lang.getMessage("room.gui.invalid_setting"));
            return;
        }

        spawnPoints.remove(index);
        config.setSpawnpoints(spawnPoints);
        roomFile.saveRoom(roomId, config);
        player.sendMessage(lang.getMessage("room.gui.changes_saved"));
    }
} 