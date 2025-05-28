package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath.AbstractsNode;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.manager.PartyPathManager;
import io.github.cy3902.mcroguelike.manager.game.GameStartManager;
import io.github.cy3902.mcroguelike.party.Party;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class PathGUIHandler implements Listener {
    private static PathGUIHandler instance;
    private final Map<Player, PathGUI> playerGUIs = new WeakHashMap<>();
    private final Lang lang;
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    /**
     * 構造函數
     */
    private PathGUIHandler() {
        mcroguelike.getServer().getPluginManager().registerEvents(this, mcroguelike);
        this.lang = mcroguelike.getLang();
    }

    /**
     * 獲取實例
     * @return 實例
     */
    public static PathGUIHandler getInstance() {
        if (instance == null) {
            instance = new PathGUIHandler();
        }
        return instance;
    }

    /**
     * 註冊GUI
     * @param player 玩家
     * @param gui GUI
     */
    public void registerGUI(Player player, PathGUI gui) {
        playerGUIs.put(player, gui);
    }

    /**
     * 註銷GUI
     * @param player 玩家
     */
    public void unregisterGUI(Player player) {
        playerGUIs.remove(player);
    }

    /**
     * 處理背包點擊
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(lang.getMessage("path.gui.title"))) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        PathGUI gui = playerGUIs.get(player);
        if (gui == null) return;
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        // 處理導航按鈕點擊
        if (clickedItem.getType() == Material.ARROW) {
            handleNavigationClick(event, player, clickedItem);
            return;
        }
        
        // 處理刪除路徑按鈕點擊
        if (clickedItem.getType() == Material.BARRIER) {
            handleDeletePathClick(player, clickedItem);
            return;
        }

        // 處理節點選擇
        handleNodeClick(player, clickedItem);
    }


    /**
     * 處理背包拖拽
     * @param event 事件
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(lang.getMessage("path.gui.title"))) {
            event.setCancelled(true);
        }
    }

    /**
     * 處理背包關閉
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(lang.getMessage("path.gui.title"))) return;
        
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        PathGUI gui = playerGUIs.get(player);
        if (gui != null) {
            gui.cleanupPlayerData(player);
            unregisterGUI(player);
        }
    }

    /**
     * 處理節點選擇
     * @param player 玩家
     * @param clickedItem 點擊的物品
     */
    public void handleNodeClick(Player player, ItemStack clickedItem) {
        PathGUI gui = playerGUIs.get(player);
        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (!itemName.startsWith(lang.getMessage("path.gui.node_name").replace("%value%", ""))) return;
        
        int nodeValue = Integer.parseInt(itemName.substring(lang.getMessage("path.gui.node_name").replace("%value%", "").length()));
        AbstractsNode clickedNode = gui.findNodeByValue(nodeValue);
        
        if (clickedNode == null) return;
        
        if (!gui.getAvailableNodes().containsKey(player)) {
            gui.openGUI(player);
            return;
        }
        
        Set<AbstractsNode> available = gui.getAvailableNodes().get(player);
        if (available != null && available.contains(clickedNode)) {
            // 更新已選節點
            gui.getSelectedNodes().put(player, clickedNode);
            
            // 更新可用節點
            Set<AbstractsNode> newAvailable = new HashSet<>(available);
            newAvailable.remove(clickedNode);
            newAvailable.addAll(clickedNode.getChildren());
            gui.getAvailableNodes().put(player, newAvailable);
            
            // 檢查是否為隊長
            Party party = mcroguelike.getPlayerPartyRegister().get(player.getUniqueId());
            if (party == null || !party.getLeader().equals(player)) {
                player.sendMessage(lang.getMessage("path.gui.not_leader"));
                return;
            }

            // 開始遊戲
            startGame(player);
            
            player.sendMessage(lang.getMessage("path.gui.node_selected").replace("%node%", String.valueOf(nodeValue)));
        }
    }

    /**
     * 開始遊戲
     * @param party 玩家
     */
    private void startGame(Player player) {
        // 獲取選中的節點
        PathGUI gui = playerGUIs.get(player);
        Party party = mcroguelike.getPlayerPartyRegister().get(player.getUniqueId());
        AbstractsNode selectedNode = gui.getSelectedNodes().get(player);
        if (selectedNode == null) {
            player.sendMessage(lang.getMessage("path.gui.select_node_first"));
            return;
        }

        if (party == null) {
            player.sendMessage(lang.getMessage("path.gui.no_party"));
            return;
        }

        if (gui.getPath() == null) {
            player.sendMessage(lang.getMessage("path.gui.no_path"));
            return;
        }
        // 檢查玩家是否從一開始就加入隊伍
        if (!gui.getPath().getPartyMembers().contains(player.getUniqueId())) {
            player.sendMessage(lang.getMessage("path.gui.not_original_member"));
            return;
        }

        GameStartManager gameStartManager = new GameStartManager(party, gui.getPath(), selectedNode.getRoom(), selectedNode.isSpecial());
        gameStartManager.start();
        // 關閉GUI
        player.closeInventory();
    }


    /**
     * 處理導航按鈕點擊
     * @param event 事件
     * @param player 玩家
     * @param clickedItem 點擊的物品
     */
    public void handleNavigationClick(InventoryClickEvent event, Player player, ItemStack clickedItem) {
        PathGUI gui = playerGUIs.get(player);
        if (!gui.getCurrentPages().containsKey(player)) {
            gui.openGUI(player);
            return;
        }
        
        int currentPage = gui.getCurrentPages().get(player);
        
        if (clickedItem.getItemMeta().getDisplayName().equals(lang.getMessage("path.gui.previous_page"))) {
            gui.getCurrentPages().put(player, currentPage - 1);
        } else if (clickedItem.getItemMeta().getDisplayName().equals(lang.getMessage("path.gui.next_page"))) {
            gui.getCurrentPages().put(player, currentPage + 1);
        }
        gui.displayNodes(event.getInventory(), player);
        gui.addNavigationButtons(event.getInventory(), player);
    }


    /**
     * 處理刪除路徑按鈕點擊
     * @param player 玩家
     * @param clickedItem 點擊的物品
     */
    public void handleDeletePathClick(Player player, ItemStack clickedItem) {
        Party party = mcroguelike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null || !party.isLeader(player)) {
            player.sendMessage(lang.getMessage("path.gui.not_leader"));
            return;
        }

        // 刪除路徑
        PartyPathManager partyPathManager = mcroguelike.getPartyPathManagerRegister().get(party.getPartyID());
        if (partyPathManager != null) {
            partyPathManager.deletePath(partyPathManager.getPath().getPathUUID().toString());
            mcroguelike.removePartyPathManagerRegister(party.getPartyID());
        }
        player.sendMessage(lang.getMessage("path.gui.path_deleted"));
        player.closeInventory();
    }
} 