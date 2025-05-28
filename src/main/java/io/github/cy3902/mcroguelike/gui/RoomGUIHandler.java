package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.files.RoomFile;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.File;

public class RoomGUIHandler implements Listener {
    private static RoomGUIHandler instance;
    private final MCRogueLike plugin;
    private final RoomGUI roomGUI;
    private final Map<UUID, String> editingPlayers; // 玩家UUID -> 正在編輯的房間ID
    private final Map<UUID, InputState> inputStates; // 玩家UUID -> 輸入狀態
    private final Map<UUID, String> editingKeys; // 玩家UUID -> 正在編輯的配置鍵
    private final Lang lang;
    private final RoomFile roomFile;
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    private enum InputState {
        NONE,
        WAITING_FOR_INPUT,
        WAITING_FOR_LOCATION,
        WAITING_FOR_ROOM_TYPE
    }

    /**
     * 構造函數
     */
    private RoomGUIHandler() {
        this.plugin = MCRogueLike.getInstance();
        this.roomGUI = new RoomGUI();
        this.editingPlayers = new HashMap<>();
        this.inputStates = new HashMap<>();
        this.editingKeys = new HashMap<>();
        this.lang = mcroguelike.getLang();
        this.roomFile = mcroguelike.getRoomFile();
        mcroguelike.getServer().getPluginManager().registerEvents(this, mcroguelike);
    }

    /**
     * 獲取單例實例
     * @return 單例實例
     */
    public static RoomGUIHandler getInstance() {
        if (instance == null) {
            instance = new RoomGUIHandler();
        }
        return instance;
    }

    /**
     * 處理背包點擊事件
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        // 如果玩家不在主菜單或編輯菜單中，則不處理
        String title = event.getView().getTitle();
        if (!title.contains(lang.getMessage("room.gui.title")) && !title.contains(lang.getMessage("room.gui.edit_title"))) return;
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null) return;
        
        if (title.contains(lang.getMessage("room.gui.title"))) {
            handleMainMenuClick(event);
        } else if (title.contains(lang.getMessage("room.gui.edit_title"))) {
            handleEditMenuClick(event);
        }
    }

    /**
     * 處理主菜單點擊事件
     * @param event 事件
     */
    private void handleMainMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem.getType() == Material.ARROW) {
            if (clickedItem.getItemMeta().getDisplayName().matches(lang.getMessage("room.gui.prev_page") + " \\d+")) {
                int currentPage = roomGUI.getPlayerPage(player);
                roomGUI.openRoomGUI(player, currentPage - 1);
            } else if (clickedItem.getItemMeta().getDisplayName().matches(lang.getMessage("room.gui.next_page") + " \\d+")) {
                int currentPage = roomGUI.getPlayerPage(player);
                roomGUI.openRoomGUI(player, currentPage + 1);
            }
        } else if (clickedItem.getType() == Material.BOOK) {
            List<String> lore = clickedItem.getItemMeta().getLore();
            if (lore != null && !lore.isEmpty()) {
                String roomId = lore.get(0).replace(lang.getMessage("room.gui.id"), "");
                if (event.getClick() == ClickType.LEFT) {
                    roomGUI.openRoomEditGUI(player, roomId);
                } else if (event.getClick() == ClickType.RIGHT) {
                    player.closeInventory();
                    player.sendMessage(lang.getMessage("room.gui.confirm_delete"));
                    inputStates.put(player.getUniqueId(), InputState.WAITING_FOR_INPUT);
                    editingKeys.put(player.getUniqueId(), "delete");
                    editingPlayers.put(player.getUniqueId(), roomId);
                }
            }
        }
    }

    /**
     * 處理編輯菜單點擊事件
     * @param event 事件
     */
    private void handleEditMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem.getType() == Material.LIME_WOOL && 
            clickedItem.getItemMeta().getDisplayName().equals(lang.getMessage("room.gui.save"))) {
            String roomId = editingPlayers.get(player.getUniqueId());
            if (roomId == null) {
                player.sendMessage(lang.getMessage("room.gui.room_not_found"));
                return;
            }
            handleRoomSave(player, roomId);
        } else if (clickedItem.getType() == Material.COMPASS){
            String setting = getSettingFromItem(clickedItem);
            if (setting != null) {
                player.closeInventory();
                player.sendMessage(lang.getMessage("room.gui.move_to_spawn"));
                inputStates.put(player.getUniqueId(), InputState.WAITING_FOR_INPUT);
                editingKeys.put(player.getUniqueId(), setting);
            }
        } else if (clickedItem.getType() == Material.SPAWNER){ 
            player.closeInventory();
            player.sendMessage(lang.getMessage("room.gui.mob_spawn_enter"));
            String roomId = editingPlayers.get(player.getUniqueId());
            SpawnpointGUIHandler.getInstance().openSpawnpointGUI(player, roomId, 0);

        } else {
            String setting = getSettingFromItem(clickedItem);
            if (setting != null) {
                player.closeInventory();
                player.sendMessage(lang.getMessage("room.gui.enter_value") + lang.getMessage("room.gui." + setting));
                inputStates.put(player.getUniqueId(), InputState.WAITING_FOR_INPUT);
                editingKeys.put(player.getUniqueId(), setting);
            }
        }
    }

    /**
     * 處理玩家聊天事件
     * @param event 事件
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!inputStates.containsKey(player.getUniqueId())) return;
        
        event.setCancelled(true);
        String input = event.getMessage();
        InputState state = inputStates.get(player.getUniqueId());
        
        if (state == InputState.WAITING_FOR_INPUT) {
            handleTextInput(player, input);
        } else if (state == InputState.WAITING_FOR_LOCATION) {
            handleLocationInput(player, input);
        }
    }

    /**
     * 處理文本輸入
     * @param player 玩家
     * @param input 輸入
     */
    private void handleTextInput(Player player, String input) {
        String roomId = editingPlayers.get(player.getUniqueId());
        
        final String key = editingKeys.get(player.getUniqueId());
        if (roomId == null || key == null) return;

        if (key.equals("delete") && input.equals("confirm")) {
            handleRoomDelete(player, roomId);
            return;
        }   
        // 處理數值輸入
        if (key.equals("time_limit") || key.equals("baseScore") || key.equals("min_floor") || key.equals("max_floor") || key.equals("early_completion_multiplier")) {
            try {
                double value = Double.parseDouble(input);
                handleSettingsUpdate(player, roomId, key, String.valueOf(value));
            } catch (NumberFormatException e) {
                handleInvalidNumber(player);
                return;
            }
        } else if (key.equals("player_spawn")) {
           // 處理玩家出生點設置
            if (key.equals("player_spawn") && !input.equals("confirm") && !input.equals("cancel")) {
                handleInvalidInput(player);
                return;
             }
            if (key.equals("player_spawn") && input.equals("confirm")) {
                Location location = player.getLocation();
                input = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                handleSettingsUpdate(player, roomId, key, input);
            }
        } else if (key.equals("name") || key.equals("structure")) {
            // 處理文本輸入
            handleSettingsUpdate(player, roomId, key, input);
        }

        inputStates.remove(player.getUniqueId());
        editingKeys.remove(player.getUniqueId());
        
        // 使用調度器同步打開GUI
        Bukkit.getScheduler().runTask(plugin, () -> {
            roomGUI.openRoomEditGUI(player, roomId);
        });
    }

    /**
     * 處理位置輸入
     * @param player 玩家
     * @param input 輸入
     */
    private void handleLocationInput(Player player, String input) {
        String roomId = editingPlayers.get(player.getUniqueId());
        if (roomId == null) return;

        if (input.equals("confirm")) {
            handleSpawnPointConfirm(player, roomId);
            inputStates.remove(player.getUniqueId());
            
            // 使用調度器同步打開GUI
            Bukkit.getScheduler().runTask(plugin, () -> {
                roomGUI.openRoomEditGUI(player, roomId);
            });
        } else if (input.equals("cancel")) {
            handleSpawnPointCancel(player);
            inputStates.remove(player.getUniqueId());
            
            // 使用調度器同步打開GUI
            Bukkit.getScheduler().runTask(plugin, () -> {
                roomGUI.openRoomEditGUI(player, roomId);
            });
        } else {
            handleInvalidInput(player);
        }
    }

    /**
     * 從物品中獲取設置
     * @param item 物品
     * @return 設置
     */
    private String getSettingFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        String displayName = item.getItemMeta().getDisplayName();
        if (displayName.equals(lang.getMessage("room.gui.room_name"))) return "name";
        if (displayName.equals(lang.getMessage("room.gui.room_type"))) return "type";
        if (displayName.equals(lang.getMessage("room.gui.structure"))) return "structure";
        if (displayName.equals(lang.getMessage("room.gui.time_limit"))) return "time_limit";
        if (displayName.equals(lang.getMessage("room.gui.base_score"))) return "baseScore";
        if (displayName.equals(lang.getMessage("room.gui.player_spawn"))) return "player_spawn";
        if (displayName.equals(lang.getMessage("room.gui.min_floor"))) return "min_floor";
        if (displayName.equals(lang.getMessage("room.gui.max_floor"))) return "max_floor";
        
        return null;
    }

    /**
     * 處理背包打開事件
     * @param event 事件
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();

        // 如果是編輯GUI，確保編輯狀態被設置
        if (title.startsWith(lang.getMessage("room.gui.edit_title"))) {
            String roomId = editingPlayers.get(player.getUniqueId());
            if (roomId == null) {
                // 從標題中提取房間ID
                String roomName = title.substring(title.indexOf(": ") + 2);
                for (Map.Entry<String, RoomConfig> entry : roomFile.getAllConfigs().entrySet()) {
                    if (entry.getValue().getName().equals(roomName)) {
                        roomId = entry.getKey();
                        editingPlayers.put(player.getUniqueId(), roomId);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 處理背包關閉事件
     * @param event 事件
     */
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
        if (title.startsWith(lang.getMessage("room.gui.edit_title"))) {
            return;
        }

        // 如果玩家在房間類型選擇GUI中，保留編輯狀態
        if (title.equals(lang.getMessage("room.gui.room_type_title"))) {
            return;
        }

        // 如果玩家在房間管理系統GUI中，保留編輯狀態
        if (title.startsWith(lang.getMessage("room.gui.title"))) {
            return;
        }

        // 只有在以上情況都不滿足時，才清除編輯狀態
        if (editingPlayers.containsKey(player.getUniqueId())) { 
            editingPlayers.remove(player.getUniqueId());
            editingKeys.remove(player.getUniqueId());
        }
    }

    /**
     * 處理房間刪除
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void handleRoomDelete(Player player, String roomId) {
        File file = new File(plugin.getDataFolder(), "Room/" + roomId + ".yml");
        if (file.exists()) {
            file.delete();
        }
        roomFile.removeProvider(roomId);
        player.sendMessage(lang.getMessage("room.gui.room_deleted"));
    }

    /**
     * 處理房間保存
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void handleRoomSave(Player player, String roomId) {
        RoomConfig config = roomFile.getConfig(roomId);
        if (config != null) {
            roomFile.saveRoom(roomId, config);
            player.sendMessage(lang.getMessage("room.gui.changes_saved"));
            player.closeInventory();
        }
    }

    /**
     * 處理房間類型更新
     * @param player 玩家
     * @param roomId 房間ID
     * @param type 類型
     */
    public void handleRoomTypeUpdate(Player player, String roomId, String type) {
        roomFile.getConfig(roomId).setType(type);
        player.sendMessage(lang.getMessage("room.gui.room_type_updated") + type);
    }

    /**
     * 處理生成點確認
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void handleSpawnPointConfirm(Player player, String roomId) {
        String locationString = player.getLocation().getBlockX() + ", " + 
                              player.getLocation().getBlockY() + ", " + 
                              player.getLocation().getBlockZ();
        roomFile.getConfig(roomId).setPlayerSpawn(locationString);
        player.sendMessage(lang.getMessage("room.gui.spawn_set") + locationString);
    }

    /**
     * 處理生成點取消
     * @param player 玩家
     */
    public void handleSpawnPointCancel(Player player) {
        player.sendMessage(lang.getMessage("room.gui.spawn_cancelled"));
    }

    /**
     * 處理無效輸入
     * @param player 玩家
     */
    public void handleInvalidInput(Player player) {
        player.sendMessage(lang.getMessage("room.gui.invalid_input"));
    }

    /**
     * 處理無效輸入
     * @param player 玩家
     */
    public void handleInvalidNumber(Player player) {
        player.sendMessage(lang.getMessage("room.gui.invalid_number"));
    }

    /**
     * 處理設置更新
     * @param player 玩家
     * @param roomId 房間ID
     * @param setting 設置
     * @param value 值
     */
    public void handleSettingsUpdate(Player player, String roomId, String setting, String value) {
        try {
            if (setting.equals("player_spawn")) {
                roomFile.getConfig(roomId).setPlayerSpawn(value);
                player.sendMessage(lang.getMessage("room.gui.settings_updated"));
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(lang.getMessage("room.gui.invalid_input"));
        }

        if (setting.equals("structure")) {
            roomFile.getConfig(roomId).setStructure(value);
            player.sendMessage(lang.getMessage("room.gui.settings_updated"));
            return;
        }

        try {
            double numericValue = Double.parseDouble(value);
          
            switch (setting) {
                case "time_limit":
                    roomFile.getConfig(roomId).setTimeLimit((int) numericValue);
                    break;
                case "baseScore":
                    roomFile.getConfig(roomId).setBaseScore((int) numericValue);
                    break;
                case "min_floor":
                    roomFile.getConfig(roomId).setMinFloor((int) numericValue);
                    break;
                case "max_floor":
                    roomFile.getConfig(roomId).setMaxFloor((int) numericValue);
                    break;
                case "early_completion_multiplier":
                    roomFile.getConfig(roomId).setEarlyCompletionMultiplier((double) numericValue);
                    break;
                case "name":
                    roomFile.getConfig(roomId).setName(value);
                    break;
                default:
                    player.sendMessage(lang.getMessage("room.gui.invalid_setting"));
                    return;
            }
            player.sendMessage(lang.getMessage("room.gui.settings_updated"));
        } catch (NumberFormatException e) {
            player.sendMessage(lang.getMessage("room.gui.invalid_number"));
        }
    }

    /**
     * 處理消息
     * @param player 玩家
     * @param message 消息
     */
    public void handleMessage(Player player, String message) {
        player.sendMessage(message);
    }

    /**
     * 獲取正在編輯的房間ID
     * @param playerId 玩家ID
     * @return 房間ID
     */
    public String getEditingPlayer(UUID playerId) {
        return editingPlayers.get(playerId);
    }

    /**
     * 設置正在編輯的房間ID
     * @param playerId 玩家ID
     * @param roomId 房間ID
     */
    public void setEditingPlayer(UUID playerId, String roomId) {
        editingPlayers.put(playerId, roomId);
    }
    
    /**
     * 開啟房間編輯GUI
     * @param player 玩家
     * @param roomId 房間ID
     */
    public void openRoomEditGUI(Player player, String roomId) {
        roomGUI.openRoomEditGUI(player, roomId);
    }



} 