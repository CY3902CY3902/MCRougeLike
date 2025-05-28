package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom.SpawnPoint;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.config.RoomConfig;
import io.github.cy3902.mcroguelike.files.RoomFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

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

        Material type = event.getCurrentItem().getType();
        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

        // 處理創建新生成點
        if (type == Material.EMERALD && displayName.equals(lang.getMessage("room.gui.create_new_spawnpoint"))) {
            handleCreateNewSpawnpoint(player);
            return;
        }

        // 處理返回按鈕
        if (type == Material.BARRIER && displayName.equals(lang.getMessage("room.gui.back"))) {
            RoomGUIHandler.getInstance().openRoomEditGUI(player, roomId);
            return;
        }

        // 處理生成點相關操作
        if (type == Material.SPAWNER) {
            handleSpawnpointInteraction(player, roomId, event);
            return;
        }

        // 處理分頁
        handlePagination(player, roomId, type, displayName);
    }

    /**
     * 處理玩家聊天事件
     * @param event 事件
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!editingSpawnpoint.containsKey(player.getUniqueId())) return;
        
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
        if (mcRogueLike.getSpawnpointRegister().containsKey(input)) {
           SpawnPoint spawnpoint = new SpawnPoint(mcRogueLike.getSpawnpointFile().getSpawnpoint(input), "");
           mcRogueLike.getRoomFile().getConfig(roomId).getSpawnpoints().add(spawnpoint);
           editingSpawnpoint.put(player.getUniqueId(), input);
           handleSpawnPointConfirm(player, roomId, input);
           editingSpawnpoint.remove(player.getUniqueId());
        }else{
            player.sendMessage(lang.getMessage("room.gui.invalid_spawnpoint_name"));
            editingSpawnpoint.remove(player.getUniqueId());
            editingPlayers.remove(player.getUniqueId());
        }
        openSpawnpointGUI(player, roomId, 0);
    }
    
    /**
     * 處理生成點名稱輸入
     * @param player 玩家
     * @param input 輸入
     */

    private void handleLocationInput(Player player, String input) {
        String roomId = editingPlayers.get(player.getUniqueId());
        String spawnpointName = editingSpawnpoint.get(player.getUniqueId());


        if (roomId == null || spawnpointName == null) return;

        if (roomFile.getConfig(roomId) == null ) {
            return;
        }

        if (input.equals("confirm")) {
            handleSpawnPointConfirm(player, roomId, spawnpointName);
            editingSpawnpoint.remove(player.getUniqueId());
            editingPlayers.remove(player.getUniqueId());
            openSpawnpointGUI(player, roomId, 0);
        } else if (input.equals("cancel")) {
            handleSpawnPointCancel(player);
            editingSpawnpoint.remove(player.getUniqueId());
            editingPlayers.remove(player.getUniqueId());
            openSpawnpointGUI(player, roomId, 0);
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
        for (SpawnPoint spawnpoint : roomFile.getConfig(roomId).getSpawnpoints()) {
            if (spawnpoint.getSpawnpoint().getName().equals(spawnpointName)) {
                spawnpoint.setLocation(locationString);
                break;
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
    }

    /**
     * 處理背包關閉事件
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        String title = event.getView().getTitle();

        // 如果玩家正在輸入，保留編輯狀態
        if (editingSpawnpoint.get(player.getUniqueId()) != null) {
            return;
        }
        if (title.contains(lang.getMessage("room.gui.title")) && title.contains(lang.getMessage("room.gui.mob_spawn"))) {
            editingPlayers.remove(player.getUniqueId());
            editingSpawnpoint.remove(player.getUniqueId());
        }
    }

    /**
     * 開啟生成點GUI
     * @param player 玩家
     * @param roomId 房間ID
     * @param page 頁面
     */
    public void openSpawnpointGUI(Player player, String roomId, int page) {
        Bukkit.getScheduler().runTask(mcRogueLike, () -> {
            spawnpointGUI.openSpawnpointGUI(player, roomId, page);
            editingPlayers.put(player.getUniqueId(), roomId);
        });
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
     * 處理創建新生成點
     * @param player 玩家
     */
    private void handleCreateNewSpawnpoint(Player player) {
        editingSpawnpoint.put(player.getUniqueId(), "");
        player.sendMessage(lang.getMessage("room.gui.enter_value") + lang.getMessage("room.gui.spawn_name"));
        player.closeInventory();
    }

    /**
     * 處理生成點交互
     * @param player 玩家
     * @param roomId 房間ID
     * @param event 事件
     */
    private void handleSpawnpointInteraction(Player player, String roomId, InventoryClickEvent event) {
        String spawnpointId = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        
        if (event.getClick().isLeftClick()) {
            // 編輯生成點
            editingSpawnpoint.put(player.getUniqueId(), spawnpointId);
            player.sendMessage(lang.getMessage("room.gui.move_to_spawn"));
            player.closeInventory();
        } else if (event.getClick().isRightClick()) {
            // 刪除生成點
            player.sendMessage(lang.getMessage("room.gui.delete_spawnpoint"));
            roomFile.getConfig(roomId).getSpawnpoints().removeIf(spawnpoint -> spawnpoint.getSpawnpoint().getName().equals(spawnpointId));
            player.closeInventory();
            openSpawnpointGUI(player, roomId, 0);
        }
    }

    /**
     * 處理分頁
     * @param player 玩家
     * @param roomId 房間ID
     * @param type 類型
     * @param displayName 顯示名稱
     */
    private void handlePagination(Player player, String roomId, Material type, String displayName) {
        if (type == Material.PAPER) {
            int pageNumber = Integer.parseInt(displayName.split(" ")[1]);
            openSpawnpointGUI(player, roomId, pageNumber);
            return;
        }

        if (type == Material.ARROW) {
            String prevPagePattern = lang.getMessage("room.gui.prev_page") + " \\d+";
            String nextPagePattern = lang.getMessage("room.gui.next_page") + " \\d+";
            
            if (displayName.matches(prevPagePattern)) {
                int pageNumber = Integer.parseInt(displayName.split(" ")[1]);
                if (pageNumber > 0) {
                    openSpawnpointGUI(player, roomId, pageNumber - 1);
                }
            } else if (displayName.matches(nextPagePattern)) {
                int pageNumber = Integer.parseInt(displayName.split(" ")[1]);
                if (pageNumber < roomFile.getConfig(roomId).getSpawnpoints().size() / 45) {
                    openSpawnpointGUI(player, roomId, pageNumber + 1);
                }
            }
        }
    }
} 