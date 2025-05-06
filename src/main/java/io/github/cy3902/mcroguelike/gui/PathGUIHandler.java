package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.config.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Map;
import java.util.WeakHashMap;

public class PathGUIHandler implements Listener {
    private static PathGUIHandler instance;
    private final Map<Player, PathGUI> playerGUIs = new WeakHashMap<>();
    private final Lang lang;
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();

    private PathGUIHandler() {
        mcroguelike.getServer().getPluginManager().registerEvents(this, mcroguelike);
        this.lang = mcroguelike.getLang();
    }

    public static PathGUIHandler getInstance() {
        if (instance == null) {
            instance = new PathGUIHandler();
        }
        return instance;
    }

    public void registerGUI(Player player, PathGUI gui) {
        playerGUIs.put(player, gui);
    }

    public void unregisterGUI(Player player) {
        playerGUIs.remove(player);
    }

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
            gui.handleNavigationClick(player, clickedItem);
            gui.displayNodes(event.getInventory(), player);
            gui.addNavigationButtons(event.getInventory(), player);
            return;
        }
        
        // 處理節點選擇
        gui.handleNodeClick(player, clickedItem);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(lang.getMessage("path.gui.title"))) {
            event.setCancelled(true);
        }
    }

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
} 