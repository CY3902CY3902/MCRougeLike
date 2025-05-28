package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom.SpawnPoint;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SpawnpointGUI {
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private static final int ITEMS_PER_PAGE = 45; // 每頁顯示的物品數量
    private final Lang lang;
    private final RoomFile roomFile;
    private int totalPages;

    public SpawnpointGUI() {
        this.lang = mcroguelike.getLang();
        this.roomFile = mcroguelike.getRoomFile();
        this.totalPages = 0;
    }

    /**
     * 打開生成點管理GUI
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void openSpawnpointGUI(Player player, String roomId, int page) {
        RoomConfig config = roomFile.getConfig(roomId);
        totalPages = (int) Math.ceil((double) roomFile.getConfig(roomId).getSpawnpoints().size() / ITEMS_PER_PAGE);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        List<SpawnPoint> spawnPoints = config.getSpawnpoints();
        if (spawnPoints == null) {
            spawnPoints = new ArrayList<>();
        }

        Inventory gui = Bukkit.createInventory(null, 54, lang.getMessage("room.gui.title") + " - " + lang.getMessage("room.gui.mob_spawn"));

        int itemIndex = page * ITEMS_PER_PAGE;

        // 添加生成點列表
        for (int i = itemIndex; i < spawnPoints.size() && i < itemIndex + ITEMS_PER_PAGE; i++) {
            SpawnPoint spawnPoint = spawnPoints.get(i);
            ItemStack spawnItem = createSpawnpointItem(spawnPoint);
            gui.setItem(i - itemIndex, spawnItem);
        }

        // 添加創建新生成點按鈕
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(lang.getMessage("room.gui.create_new_spawnpoint"));
        createButton.setItemMeta(createMeta);
        gui.setItem(47, createButton);

        // 添加上一頁按鈕
        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(lang.getMessage("room.gui.prev_page") + " " + (totalPages));
            prevPage.setItemMeta(prevMeta);
            gui.setItem(45, prevPage);
        }

        // 添加下一頁按鈕
        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(lang.getMessage("room.gui.next_page") + " " + (totalPages));
            nextPage.setItemMeta(nextMeta);
            gui.setItem(53, nextPage);
        }

        // 添加頁碼顯示
        ItemStack pageItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageItem.getItemMeta();
        pageMeta.setDisplayName(lang.getMessage("room.gui.page_info") + " " + (totalPages));
        pageItem.setItemMeta(pageMeta);
        gui.setItem(49, pageItem);
        


        // 添加返回按鈕
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(lang.getMessage("room.gui.back"));
        backButton.setItemMeta(backMeta);
        gui.setItem(51, backButton);

        player.openInventory(gui);
    }

    /**
     * 創建生成點物品
     * @param spawnPoint 生成點配置
     * @return ItemStack
     */
    private ItemStack createSpawnpointItem(SpawnPoint spawnPoint) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();
        
        String name = spawnPoint.getSpawnpoint().getName();
        String location = spawnPoint.getLocation();
        
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
        String roomId = SpawnpointGUIHandler.getInstance().getEditingPlayer(player.getUniqueId());
        if (roomId == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        RoomConfig config = roomFile.getConfig(roomId);
        if (config == null) {
            player.sendMessage(lang.getMessage("room.gui.room_not_found"));
            return;
        }

        List<SpawnPoint> spawnPoints = config.getSpawnpoints();
        if (spawnPoints == null) {
            spawnPoints = new ArrayList<>();
        }

        SpawnPoint newSpawnPoint = new SpawnPoint(mcroguelike.getSpawnpointFile().getSpawnpoint(name), location);
        spawnPoints.add(newSpawnPoint);

        config.setSpawnpoints(spawnPoints);
        roomFile.saveRoom(roomId, config);
        player.sendMessage(lang.getMessage("room.gui.changes_saved"));
    }

} 