package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PathEditGUIHandler implements Listener {
    private static PathEditGUIHandler instance;
    private final MCRogueLike plugin;
    private final Map<UUID, String> editingPlayers; // 玩家UUID -> 正在編輯的配置項
    private final Map<UUID, String> playerPaths; // 玩家UUID -> 正在編輯的路徑ID

    private PathEditGUIHandler() {
        this.plugin = MCRogueLike.getInstance();
        this.editingPlayers = new HashMap<>();
        this.playerPaths = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static PathEditGUIHandler getInstance() {
        if (instance == null) {
            instance = new PathEditGUIHandler();
        }
        return instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedInventory == null || clickedItem == null || !clickedItem.hasItemMeta()) return;

        String title = event.getView().getTitle();
        if (!title.equals(ChatColor.DARK_PURPLE + "路徑管理")) return;

        event.setCancelled(true);

        // 處理導航按鈕點擊
        if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "上一頁") ||
            clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "下一頁")) {
            PathEditGUI.getInstance().handleNavigationClick(player, clickedItem);
            return;
        }

        // 處理創建新路徑按鈕
        if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "創建新路徑")) {
            // TODO: 實現創建新路徑的功能
            return;
        }

        // 處理路徑項目點擊
        String pathId = clickedItem.getItemMeta().getLore().get(0).substring(5); // 從 "ID: xxx" 中提取ID
        PathEditGUI.getInstance().openPathEditGUI(player, pathId);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String configKey = editingPlayers.get(player.getUniqueId());
        if (configKey == null) return;

        event.setCancelled(true);
        String input = event.getMessage();
        String pathId = playerPaths.get(player.getUniqueId());

        if (pathId == null) {
            editingPlayers.remove(player.getUniqueId());
            return;
        }

        try {
            Object value;
            switch (configKey) {
                case "max_nodes":
                case "max_branches":
                case "max_height":
                    value = Integer.parseInt(input);
                    break;
                case "special_node_probability":
                    value = Double.parseDouble(input);
                    break;
                case "room_names":
                case "boss_room_names":
                    value = List.of(input.split(","));
                    break;
                default:
                    value = input;
            }

            PathEditGUI.getInstance().updatePathConfig(configKey, value);
            player.sendMessage(ChatColor.GREEN + "已更新配置: " + configKey);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "無效的數值，請重新輸入");
            return;
        }

        editingPlayers.remove(player.getUniqueId());
        PathEditGUI.getInstance().openPathEditGUI(player, pathId);
    }

    private void promptForInput(Player player, String configKey, String message) {
        editingPlayers.put(player.getUniqueId(), configKey);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + message);
    }

    public void registerEditingPlayer(Player player, String pathId) {
        playerPaths.put(player.getUniqueId(), pathId);
    }

    public void unregisterEditingPlayer(Player player) {
        playerPaths.remove(player.getUniqueId());
        editingPlayers.remove(player.getUniqueId());
    }
} 