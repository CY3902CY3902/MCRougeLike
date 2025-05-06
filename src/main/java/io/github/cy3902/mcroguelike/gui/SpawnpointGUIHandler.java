package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.files.RoomFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 生成點GUI處理器
 */
public class SpawnpointGUIHandler implements Listener {
    private static SpawnpointGUIHandler instance;
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private final SpawnpointGUI spawnpointGUI;
    private final Map<UUID, String> editingPlayers; // 玩家UUID -> 正在編輯的房間ID
    private final Map<UUID, String> editingSpawnpoint; // 玩家UUID -> 正在編輯的生成點ID
    private final Lang lang;
    private final RoomFile roomFile;

    private SpawnpointGUIHandler() {
        this.spawnpointGUI = new SpawnpointGUI();
        this.editingPlayers = new HashMap<>();
        this.editingSpawnpoint = new HashMap<>();
        this.lang = mcRogueLike.getLang();
        this.roomFile = mcRogueLike.getRoomFile();
        mcRogueLike.getServer().getPluginManager().registerEvents(this, mcRogueLike);
    }

    public static SpawnpointGUIHandler getInstance() {
        if (instance == null) {
            instance = new SpawnpointGUIHandler();
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
        Player player = (Player) event.getWhoClicked();
        
        String title = event.getView().getTitle();
        if (!title.contains(lang.getMessage("room.gui.title")) || !title.contains(lang.getMessage("room.gui.mob_spawn"))) return;
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null) return;
        
        String roomId = editingPlayers.get(player.getUniqueId());
        if (roomId == null) return;

        if (event.getCurrentItem().getType() == Material.EMERALD && 
            event.getCurrentItem().getItemMeta().getDisplayName().equals(lang.getMessage("room.gui.create_new_spawnpoint"))) {
            player.closeInventory();
            player.sendMessage(lang.getMessage("room.gui.enter_value") + lang.getMessage("room.gui.spawn_name"));
            editingSpawnpoint.put(player.getUniqueId(), "");
        } else if (event.getCurrentItem().getType() == Material.BARRIER && 
                   event.getCurrentItem().getItemMeta().getDisplayName().equals(lang.getMessage("room.gui.back"))) {
            RoomGUIHandler.getInstance().openRoomEditGUI(player, roomId);
        } else if (event.getCurrentItem().getType() == Material.SPAWNER && event.getClick().isLeftClick()) {
            player.closeInventory();
            editingSpawnpoint.put(player.getUniqueId(), event.getCurrentItem().getItemMeta().getDisplayName());
            player.sendMessage(lang.getMessage("room.gui.move_to_spawn"));
            
        } else if (event.getCurrentItem().getType() == Material.SPAWNER && event.getClick().isRightClick()) {
            player.sendMessage(lang.getMessage("room.gui.delete_spawnpoint"));
            String spawnpointName = event.getCurrentItem().getItemMeta().getDisplayName();
            String spawnpointLocation = event.getCurrentItem().getItemMeta().getLore().get(0);
            Map<String, String> spawnpoint = new HashMap<>();
            spawnpoint.put(spawnpointName, spawnpointLocation);
            roomFile.getConfig(roomId).getSpawnpoints().remove(spawnpoint);
            spawnpointGUI.openSpawnpointGUI(player, roomId);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!editingSpawnpoint.containsKey(player.getUniqueId()) || !editingPlayers.containsKey(player.getUniqueId())) return;
        
        event.setCancelled(true);
        if (editingSpawnpoint.get(player.getUniqueId()).equals("")) {
            handleSpawnPointNameInput(player, event.getMessage());
        } else {
            handleLocationInput(player, event.getMessage());
        }
    }
    
    /**
     * 處理生成點名稱輸入
     * @param player 玩家
     * @param input 輸入
     */
    private void handleSpawnPointNameInput(Player player, String input) {
        String roomId = editingPlayers.get(player.getUniqueId());
        if (roomId == null) return;
        editingSpawnpoint.remove(player.getUniqueId());
        handleSpawnPointConfirm(player, roomId, input);
        player.sendMessage(lang.getMessage("room.gui.enter_value") + lang.getMessage("room.gui.spawn_location"));
        Bukkit.getScheduler().runTask(mcRogueLike, () -> {
            spawnpointGUI.openSpawnpointGUI(player, roomId);
        });
    }
    
    /**
     * 處理生成點名稱輸入
     * @param player 玩家
     * @param input 輸入
     */

    private void handleLocationInput(Player player, String input) {
        String roomId = editingPlayers.get(player.getUniqueId());
        String spawnpointName = editingSpawnpoint.get(player.getUniqueId());
        if (roomId == null) return;

        if (input.equals("confirm")) {
            handleSpawnPointConfirm(player, roomId, spawnpointName);
            editingSpawnpoint.remove(player.getUniqueId());
            
            // 使用調度器同步打開GUI
            Bukkit.getScheduler().runTask(mcRogueLike, () -> {
                spawnpointGUI.openSpawnpointGUI(player, roomId);
            });
        } else if (input.equals("cancel")) {
            handleSpawnPointCancel(player);
            editingSpawnpoint.remove(player.getUniqueId());
            
            // 使用調度器同步打開GUI
            Bukkit.getScheduler().runTask(mcRogueLike, () -> {
                spawnpointGUI.openSpawnpointGUI(player, roomId);
            });
        } else {
            player.sendMessage(lang.getMessage("room.gui.invalid_input"));
        }
    }


    /**
     * 處理生成點確認
     * @param player 玩家
     * @param roomId 房間ID
     * @param spawnpointName 生成點名稱
     */
    public void handleSpawnPointConfirm(Player player, String roomId, String spawnpointName) {
        String locationString = player.getLocation().getBlockX() + ", " + 
                              player.getLocation().getBlockY() + ", " + 
                              player.getLocation().getBlockZ();
        for (Map<String, String> spawnpoint : roomFile.getConfig(roomId).getSpawnpoints()) {
            if (spawnpoint.get("name").equals(spawnpointName)) {
                spawnpoint.put("location", locationString);
            }
        }
        player.sendMessage(lang.getMessage("room.gui.spawn_set") + locationString);
    }

    /**
     * 處理生成點取消
     * @param player 玩家
     */
    public void handleSpawnPointCancel(Player player) {
        player.sendMessage(lang.getMessage("room.gui.spawn_cancelled"));
        editingSpawnpoint.remove(player.getUniqueId());
    }


    /**
     * 處理生成點開啟
     * @param event 事件
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();

        if (title.contains(lang.getMessage("room.gui.title")) && title.contains(lang.getMessage("room.gui.mob_spawn"))) {
            String roomId = editingPlayers.get(player.getUniqueId());
            if (roomId == null) {
                // 從標題中提取房間ID
                String roomName = title.substring(title.indexOf(" - ") + 3);
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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        String title = event.getView().getTitle();
        if (title.contains(lang.getMessage("room.gui.title")) && title.contains(lang.getMessage("room.gui.mob_spawn"))) {
            editingPlayers.remove(player.getUniqueId());
        }
    }

    public void setEditingPlayer(UUID playerId, String roomId) {
        editingPlayers.put(playerId, roomId);
    }

    public String getEditingRoomId(Player player) {
        return editingPlayers.get(player.getUniqueId());
    }

    public void clearEditingState(Player player) {
        editingPlayers.remove(player.getUniqueId());
    }

    public void openSpawnpointGUI(Player player, String roomId) {
        spawnpointGUI.openSpawnpointGUI(player, roomId);
        setEditingPlayer(player.getUniqueId(), roomId);
    }
    
} 