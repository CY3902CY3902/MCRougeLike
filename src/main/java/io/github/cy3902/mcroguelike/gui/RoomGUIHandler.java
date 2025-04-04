package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RoomGUIHandler implements Listener {
    private static RoomGUIHandler instance;
    private final MCRogueLike plugin;
    private final RoomGUI roomGUI;
    private final Map<UUID, String> editingPlayers; // 玩家UUID -> 正在編輯的房間ID
    private final Map<UUID, InputState> inputStates; // 玩家UUID -> 輸入狀態
    private final Map<UUID, String> editingKeys; // 玩家UUID -> 正在編輯的配置鍵

    private enum InputState {
        NONE,
        WAITING_FOR_INPUT,
        WAITING_FOR_LOCATION,
        WAITING_FOR_ROOM_TYPE
    }

    private RoomGUIHandler() {
        this.plugin = MCRogueLike.getInstance();
        this.roomGUI = new RoomGUI();
        this.editingPlayers = new HashMap<>();
        this.inputStates = new HashMap<>();
        this.editingKeys = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static RoomGUIHandler getInstance() {
        if (instance == null) {
            instance = new RoomGUIHandler();
        }
        return instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // 主GUI處理
        if (title.startsWith(ChatColor.DARK_PURPLE + "房間管理系統")) {
            event.setCancelled(true);
            handleMainGUI(event, player, clickedItem);
        }
        // 編輯GUI處理
        else if (title.startsWith(ChatColor.DARK_PURPLE + "編輯房間: ")) {
            event.setCancelled(true);
            handleEditGUI(player, clickedItem, title);
        }
        // 房間類型選擇GUI處理
        else if (title.equals(ChatColor.DARK_PURPLE + "選擇房間類型")) {
            event.setCancelled(true);
            handleRoomTypeSelection(player, clickedItem);
        }
    }

    private void handleMainGUI(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.EMERALD) {
            // 創建新房間
            promptForInput(player, null, "new_room", "請輸入新房間的ID");
        } else if (clickedItem.getType() == Material.ARROW) {
            // 處理分頁按鈕
            String displayName = clickedItem.getItemMeta().getDisplayName();
            int currentPage = roomGUI.getPlayerPage(player);
            
            if (displayName.equals(ChatColor.GREEN + "上一頁")) {
                roomGUI.openRoomGUI(player, currentPage - 1);
            } else if (displayName.equals(ChatColor.GREEN + "下一頁")) {
                roomGUI.openRoomGUI(player, currentPage + 1);
            }
        } else {
            String roomId = getRoomIdFromItem(clickedItem);
            if (roomId == null) {
                plugin.getLogger().warning("無法從物品中獲取房間ID");
                return;
            }

            plugin.getLogger().info("獲取到房間ID: " + roomId);

            if (event.isRightClick()) {
                // 刪除房間
                roomGUI.deleteRoom(roomId);
                player.sendMessage(ChatColor.RED + "房間已刪除！");
                roomGUI.openRoomGUI(player, roomGUI.getPlayerPage(player));
            } else {
                // 編輯房間
                editingPlayers.put(player.getUniqueId(), roomId);
                plugin.getLogger().info("設置編輯玩家: " + player.getName() + " -> " + roomId);
                plugin.getLogger().info("當前編輯玩家列表: " + editingPlayers);
                roomGUI.openRoomEditGUI(player, roomId);
            }
        }
    }

    private void handleEditGUI(Player player, ItemStack clickedItem, String title) {
        String roomId = editingPlayers.get(player.getUniqueId());
        plugin.getLogger().info("正在編輯GUI的玩家: " + player.getName());
        plugin.getLogger().info("玩家UUID: " + player.getUniqueId());
        plugin.getLogger().info("編輯玩家列表: " + editingPlayers);
        plugin.getLogger().info("獲取到的房間ID: " + roomId);

        if (roomId == null) {
            plugin.getLogger().warning("無法獲取正在編輯的房間ID");
            return;
        }

        if (clickedItem.getType() == Material.LIME_WOOL) {
            // 保存更改
            player.sendMessage(ChatColor.GREEN + "更改已保存！");
            roomGUI.saveRoomConfig(roomId);
            editingPlayers.remove(player.getUniqueId());
            inputStates.remove(player.getUniqueId());
            editingKeys.remove(player.getUniqueId());
            roomGUI.openRoomGUI(player);
        } else {
            // 編輯具體項目
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            switch (itemName) {
                case "房間名稱":
                    promptForInput(player, roomId, "name", "請輸入新的房間名稱");
                    break;
                case "房間類型":
                    openRoomTypeSelection(player);
                    break;
                case "結構名稱":
                    promptForInput(player, roomId, "structure", "請輸入新的結構名稱");
                    break;
                case "時限":
                    promptForInput(player, roomId, "time_limit", "請輸入新的時限（秒）");
                    break;
                case "基礎分數":
                    promptForInput(player, roomId, "baseScore", "請輸入新的基礎分數");
                    break;
                case "玩家出生點":
                    promptForLocation(player, roomId);
                    break;
                case "最小樓層":
                    promptForInput(player, roomId, "floor.min", "請輸入新的最小樓層");
                    break;
                case "最大樓層":
                    promptForInput(player, roomId, "floor.max", "請輸入新的最大樓層");
                    break;
            }
        }
    }

    private void openRoomTypeSelection(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "選擇房間類型");
        
        // 生存房間
        ItemStack survivalItem = createRoomTypeItem(Material.SHIELD, "生存房間", "玩家需要在指定時間內存活下來");
        gui.setItem(11, survivalItem);
        
        // 剿滅房間
        ItemStack annihilationItem = createRoomTypeItem(Material.DIAMOND_SWORD, "剿滅房間", "玩家需要在時間內消滅指定數量的敵人");
        gui.setItem(12, annihilationItem);
        
        // 防守房間
        ItemStack defenseItem = createRoomTypeItem(Material.IRON_DOOR, "防守房間", "玩家需要防守特定位置不被敵人攻破");
        gui.setItem(13, defenseItem);
        
        // 狙擊房間
        ItemStack sniperItem = createRoomTypeItem(Material.BOW, "狙擊房間", "玩家需要從遠處狙擊特定目標");
        gui.setItem(14, sniperItem);
        
        // 返回按鈕
        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "返回");
        backButton.setItemMeta(backMeta);
        gui.setItem(26, backButton);
        
        player.openInventory(gui);
    }

    private ItemStack createRoomTypeItem(Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + description);
        lore.add("");
        lore.add(ChatColor.YELLOW + "點擊選擇此類型");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void handleRoomTypeSelection(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.BARRIER) {
            // 返回按鈕
            String roomId = editingPlayers.get(player.getUniqueId());
            if (roomId != null) {
                roomGUI.openRoomEditGUI(player, roomId);
            } else {
                roomGUI.openRoomGUI(player);
            }
            return;
        }

        String roomId = editingPlayers.get(player.getUniqueId());
        if (roomId == null) return;

        String type = null;
        switch (clickedItem.getType()) {
            case SHIELD:
                type = "Survival";
                break;
            case DIAMOND_SWORD:
                type = "Annihilation";
                break;
            case IRON_DOOR:
                type = "Defense";
                break;
            case BOW:
                type = "SniperMission";
                break;
        }

        if (type != null) {
            roomGUI.updateRoomConfig(roomId, "type", type);
            player.sendMessage(ChatColor.GREEN + "房間類型已更新為: " + type);
            roomGUI.openRoomEditGUI(player, roomId);
        }
    }

    private void promptForLocation(Player player, String roomId) {
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "請移動到你想要設置的出生點位置，然後輸入 'confirm' 確認。");
        player.sendMessage(ChatColor.YELLOW + "輸入 'cancel' 取消設置。");
        inputStates.put(player.getUniqueId(), InputState.WAITING_FOR_LOCATION);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();
        InputState state = inputStates.get(player.getUniqueId());
        
        if (state == null) return;

        event.setCancelled(true);

        switch (state) {
            case WAITING_FOR_INPUT:
                handleTextInput(player, message);
                break;
            case WAITING_FOR_LOCATION:
                handleLocationInput(player, message);
                break;
        }
    }

    private void handleLocationInput(Player player, String message) {
        String roomId = editingPlayers.get(player.getUniqueId());
        if (roomId == null) return;

        if (message.equals("confirm")) {
            Location loc = player.getLocation();
            String locationString = String.format("%.0f,%.0f,%.0f", loc.getX(), loc.getY(), loc.getZ());
            roomGUI.updateRoomConfig(roomId, "player_spawn", locationString);
            player.sendMessage(ChatColor.GREEN + "出生點已設置為: " + locationString);
            inputStates.remove(player.getUniqueId());
            
            // 使用調度器同步打開GUI
            Bukkit.getScheduler().runTask(plugin, () -> {
                roomGUI.openRoomEditGUI(player, roomId);
            });
        } else if (message.equals("cancel")) {
            player.sendMessage(ChatColor.RED + "已取消設置出生點");
            inputStates.remove(player.getUniqueId());
            
            // 使用調度器同步打開GUI
            Bukkit.getScheduler().runTask(plugin, () -> {
                roomGUI.openRoomEditGUI(player, roomId);
            });
        } else {
            player.sendMessage(ChatColor.RED + "無效的輸入。請輸入 'confirm' 確認或 'cancel' 取消。");
        }
    }

    private void handleTextInput(Player player, String message) {
        final String roomId = editingPlayers.get(player.getUniqueId());
        final String key = editingKeys.get(player.getUniqueId());
        if (roomId == null || key == null) return;

        // 處理新房間創建
        if (key.equals("new_room")) {
            if (roomGUI.getRoomConfig(message) != null) {
                player.sendMessage(ChatColor.RED + "該房間ID已存在！");
                return;
            }
            roomGUI.createNewRoom(message, "Survival"); // 默認創建生存房間
            player.sendMessage(ChatColor.GREEN + "已創建新房間！");
            inputStates.remove(player.getUniqueId());
            editingKeys.remove(player.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> roomGUI.openRoomGUI(player));
            return;
        }

        // 處理數值輸入
        if (key.equals("time_limit") || key.equals("baseScore") || key.equals("floor.min") || key.equals("floor.max")) {
            try {
                int value = Integer.parseInt(message);
                roomGUI.updateRoomConfig(roomId, key, value);
                player.sendMessage(ChatColor.GREEN + "已更新設置！");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "請輸入有效的數值！");
                return;
            }
        } else if (key.equals("name") || key.equals("structure")) {
            // 處理文本輸入
            roomGUI.updateRoomConfig(roomId, key, message);
            player.sendMessage(ChatColor.GREEN + "已更新設置！");
        }

        inputStates.remove(player.getUniqueId());
        editingKeys.remove(player.getUniqueId());
        
        // 使用調度器同步打開GUI
        Bukkit.getScheduler().runTask(plugin, () -> {
            roomGUI.openRoomEditGUI(player, roomId);
        });
    }

    private void promptForInput(Player player, String roomId, String key, String message) {
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + message);
        inputStates.put(player.getUniqueId(), InputState.WAITING_FOR_INPUT);
        editingKeys.put(player.getUniqueId(), key);
    }

    private String getRoomIdFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null) return null;
        
        List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.startsWith(ChatColor.GRAY + "ID: ")) {
                String roomId = ChatColor.stripColor(line.substring(4));
                // 清理房間ID中的特殊字符
                return roomId.replace(":", "").trim();
            }
        }
        return null;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();

        // 如果是編輯GUI，確保編輯狀態被設置
        if (title.startsWith(ChatColor.DARK_PURPLE + "編輯房間: ")) {
            String roomId = editingPlayers.get(player.getUniqueId());
            if (roomId == null) {
                // 從標題中提取房間ID
                String roomName = title.substring(title.indexOf(": ") + 2);
                for (Map.Entry<String, FileConfiguration> entry : roomGUI.getRoomConfigs().entrySet()) {
                    if (entry.getValue().getString("name", "").equals(roomName)) {
                        roomId = entry.getKey();
                        editingPlayers.put(player.getUniqueId(), roomId);
                        plugin.getLogger().info("設置編輯玩家: " + player.getName() + " -> " + roomId);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        // 如果玩家正在輸入，保留編輯狀態
        if (inputStates.get(player.getUniqueId()) != null) {
            return;
        }

        // 如果玩家在編輯GUI中，保留編輯狀態
        String title = event.getView().getTitle();
        if (title.startsWith(ChatColor.DARK_PURPLE + "編輯房間: ")) {
            return;
        }

        // 如果玩家在房間類型選擇GUI中，保留編輯狀態
        if (title.equals(ChatColor.DARK_PURPLE + "選擇房間類型")) {
            return;
        }

        // 如果玩家在房間管理系統GUI中，保留編輯狀態
        if (title.startsWith(ChatColor.DARK_PURPLE + "房間管理系統")) {
            return;
        }

        // 只有在以上情況都不滿足時，才清除編輯狀態
        if (editingPlayers.containsKey(player.getUniqueId())) { 
            editingPlayers.remove(player.getUniqueId());
            editingKeys.remove(player.getUniqueId());
        }
    }

    public void setEditingPlayer(UUID playerId, String roomId) {
        editingPlayers.put(playerId, roomId);
    }
} 