package io.github.cy3902.mcroguelike.gui;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MobGUI {
    private final MCRogueLike mcRogueLike;
    private final File mobDir;
    private final Map<String, FileConfiguration> mobConfigs;
    private final Lang lang;

    public MobGUI() {
        this.mcRogueLike = MCRogueLike.getInstance();
        this.mobDir = new File(mcRogueLike.getDataFolder() + "/Mob");
        this.mobConfigs = new HashMap<>();
        this.lang = mcRogueLike.getLang();
        loadConfigs();
    }

    /**
     * 加載所有怪物配置
     */
    private void loadConfigs() {
        // 確保目錄存在
        if (!mobDir.exists()) {
            mobDir.mkdirs();
        }

        // 加載所有yml文件
        File[] files = mobDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String mobId = file.getName().replace(".yml", "");
                mobConfigs.put(mobId, YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    /**
     * 保存怪物配置
     * @param mobId 怪物ID
     * @param config 配置
     */
    private void saveConfig(String mobId, FileConfiguration config) {
        try {
            File file = new File(mobDir, mobId + ".yml");
            config.save(file);
        } catch (IOException e) {
            mcRogueLike.getLogger().severe("無法保存怪物配置 " + mobId + ": " + e.getMessage());
        }
    }

    /**
     * 保存怪物配置
     * @param mobId 怪物ID
     * @param key 配置鍵
     * @param value 配置值
     */
    public void saveMobConfig(String mobId, String key, Object value) {
        FileConfiguration config = getMobConfig(mobId);
        if (config == null) return;
        
        config.set(key, value);
        saveConfig(mobId, config);
    }

    /**
     * 獲取怪物配置
     * @param mobId 怪物ID
     * @return FileConfiguration
     */
    public FileConfiguration getMobConfig(String mobId) {
        return mobConfigs.get(mobId);
    }

    /**
     * 打開怪物管理GUI
     * @param player 玩家
     */
    public void openMobGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "怪物管理系統");

        // 添加怪物列表
        for (String mobId : mobConfigs.keySet()) {
            ItemStack mobItem = createMobItem(mobId);
            if (mobItem != null) {
                gui.addItem(mobItem);
            }
        }

        // 添加創建新怪物按鈕
        ItemStack createButton = new ItemStack(Material.EMERALD);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(ChatColor.GREEN + "創建新怪物");
        createButton.setItemMeta(createMeta);
        gui.setItem(53, createButton);

        player.openInventory(gui);
    }

    /**
     * 創建怪物物品
     * @param mobId 怪物ID
     * @return ItemStack
     */
    private ItemStack createMobItem(String mobId) {
        FileConfiguration config = getMobConfig(mobId);
        if (config == null) return null;

        ItemStack item = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + config.getString("name", mobId));
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "ID: " + mobId);
        lore.add(ChatColor.GRAY + "怪物ID: " + config.getString("mob_id", "未設置"));
        lore.add(ChatColor.GRAY + "倍率加成: " + config.getDouble("multiplier", 1.0));
        lore.add(ChatColor.GRAY + "是否為BOSS: " + (config.getBoolean("is_boss", false) ? "是" : "否"));
        lore.add(ChatColor.GRAY + "生成數量: " + config.getInt("spawn_amount", 1));
        lore.add("");
        lore.add(ChatColor.YELLOW + "左鍵點擊: 編輯怪物");
        lore.add(ChatColor.RED + "右鍵點擊: 刪除怪物");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 打開怪物編輯GUI
     * @param player 玩家
     * @param mobId 怪物ID
     */
    public void openMobEditGUI(Player player, String mobId) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "編輯怪物: " + mobId);
        
        // 怪物名稱
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName(ChatColor.GOLD + "怪物名稱");
        List<String> nameLore = new ArrayList<>();
        nameLore.add(ChatColor.GRAY + "當前名稱: " + getMobConfig(mobId).getString("name", "未設置"));
        nameLore.add("");
        nameLore.add(ChatColor.YELLOW + "點擊修改名稱");
        nameMeta.setLore(nameLore);
        nameItem.setItemMeta(nameMeta);
        gui.setItem(10, nameItem);
        
        // 怪物ID
        ItemStack mobIdItem = new ItemStack(Material.PAPER);
        ItemMeta mobIdMeta = mobIdItem.getItemMeta();
        mobIdMeta.setDisplayName(ChatColor.GOLD + "怪物ID");
        List<String> mobIdLore = new ArrayList<>();
        mobIdLore.add(ChatColor.GRAY + "當前ID: " + getMobConfig(mobId).getString("mob_id", "未設置"));
        mobIdLore.add("");
        mobIdLore.add(ChatColor.YELLOW + "點擊修改ID");
        mobIdMeta.setLore(mobIdLore);
        mobIdItem.setItemMeta(mobIdMeta);
        gui.setItem(12, mobIdItem);
        
        // 倍率加成
        ItemStack multiplierItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta multiplierMeta = multiplierItem.getItemMeta();
        multiplierMeta.setDisplayName(ChatColor.GOLD + "倍率加成");
        List<String> multiplierLore = new ArrayList<>();
        multiplierLore.add(ChatColor.GRAY + "當前倍率: " + getMobConfig(mobId).getDouble("multiplier", 1.0));
        multiplierLore.add("");
        multiplierLore.add(ChatColor.YELLOW + "點擊修改倍率");
        multiplierMeta.setLore(multiplierLore);
        multiplierItem.setItemMeta(multiplierMeta);
        gui.setItem(14, multiplierItem);
        
        // 是否為BOSS
        ItemStack bossItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta bossMeta = bossItem.getItemMeta();
        bossMeta.setDisplayName(ChatColor.GOLD + "是否為BOSS");
        List<String> bossLore = new ArrayList<>();
        bossLore.add(ChatColor.GRAY + "當前狀態: " + (getMobConfig(mobId).getBoolean("is_boss", false) ? "是" : "否"));
        bossLore.add("");
        bossLore.add(ChatColor.YELLOW + "點擊切換狀態");
        bossMeta.setLore(bossLore);
        bossItem.setItemMeta(bossMeta);
        gui.setItem(16, bossItem);
        
        // 生成數量
        ItemStack amountItem = new ItemStack(Material.SPAWNER);
        ItemMeta amountMeta = amountItem.getItemMeta();
        amountMeta.setDisplayName(ChatColor.GOLD + "生成數量");
        List<String> amountLore = new ArrayList<>();
        amountLore.add(ChatColor.GRAY + "當前數量: " + getMobConfig(mobId).getInt("spawn_amount", 1));
        amountLore.add("");
        amountLore.add(ChatColor.YELLOW + "點擊修改數量");
        amountMeta.setLore(amountLore);
        amountItem.setItemMeta(amountMeta);
        gui.setItem(18, amountItem);
        
        // 保存按鈕
        ItemStack saveButton = new ItemStack(Material.LIME_WOOL);
        ItemMeta saveMeta = saveButton.getItemMeta();
        saveMeta.setDisplayName(ChatColor.GREEN + "保存更改");
        saveButton.setItemMeta(saveMeta);
        gui.setItem(26, saveButton);
        
        player.openInventory(gui);
    }

    /**
     * 創建新怪物
     * @param name 怪物名稱
     * @return 怪物ID
     */
    public String createNewMob(String name) {
        String mobId = name.toLowerCase().replace(" ", "_");
        FileConfiguration config = new YamlConfiguration();
        config.set("name", name);
        config.set("mob_id", "未設置");
        config.set("multiplier", 1.0);
        config.set("is_boss", false);
        config.set("spawn_amount", 1);
        
        try {
            File file = new File(mobDir, mobId + ".yml");
            config.save(file);
            mobConfigs.put(mobId, config);
            return mobId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 刪除怪物
     * @param mobId 怪物ID
     */
    public void deleteMob(String mobId) {
        File file = new File(mobDir, mobId + ".yml");
        if (file.exists()) {
            file.delete();
        }
        mobConfigs.remove(mobId);
    }

    /**
     * 處理怪物GUI的點擊事件
     * @param event 點擊事件
     * @param player 玩家
     */
    public void handleMobGUI(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.EMERALD) {
            // 創建新怪物
            promptForInput(player, "mob_name");
        } else {
            // 編輯或刪除現有怪物
            String mobId = getMobIdFromItem(clickedItem);
            if (mobId != null) {
                if (event.isRightClick()) {
                    // 刪除怪物
                    deleteMob(mobId);
                    openMobGUI(player);
                } else {
                    // 編輯怪物
                    openMobEditGUI(player, mobId);
                }
            }
        }
    }

    /**
     * 從物品中獲取怪物ID
     * @param item 物品
     * @return 怪物ID
     */
    private String getMobIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;
        List<String> lore = item.getItemMeta().getLore();
        for (String line : lore) {
            if (line.startsWith(ChatColor.GRAY + "ID: ")) {
                return line.substring(7);
            }
        }
        return null;
    }

    /**
     * 提示玩家輸入
     * @param player 玩家
     * @param field 輸入字段
     */
    private void promptForInput(Player player, String field) {
        player.closeInventory();
        switch (field) {
            case "mob_name":
                handleMobNameInput(player);
                break;
            case "mob_id":
                handleMobIdInput(player);
                break;
            case "multiplier":
                handleMobMultiplierInput(player);
                break;
            case "spawn_amount":
                handleMobCountInput(player);
                break;
        }
    }

    /**
     * 獲取所有怪物配置
     * @return 怪物配置Map
     */
    public Map<String, FileConfiguration> getMobConfigs() {
        return mobConfigs;
    }

    public void handleMobNameInput(Player player) {
        player.sendMessage(lang.getMessage("mob.gui.enter_name"));
    }

    public void handleMobIdInput(Player player) {
        player.sendMessage(lang.getMessage("mob.gui.enter_id"));
    }

    public void handleMobMultiplierInput(Player player) {
        player.sendMessage(lang.getMessage("mob.gui.enter_multiplier"));
    }

    public void handleMobCountInput(Player player) {
        player.sendMessage(lang.getMessage("mob.gui.enter_count"));
    }
} 