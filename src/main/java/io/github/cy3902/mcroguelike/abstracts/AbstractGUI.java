package io.github.cy3902.mcroguelike.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * AbstractsGUI 是一個抽象類別，用於創建和管理遊戲中的GUI介面
 */
public abstract class AbstractGUI implements InventoryHolder {
    protected Inventory inventory;
    protected String title;
    protected int size;

    /**
     * 建構子，初始化GUI的基本參數
     * 
     * @param title GUI的標題
     * @param size GUI的大小（必須是9的倍數）
     */
    public AbstractGUI(String title, int size) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    /**
     * 獲取GUI的庫存
     * 
     * @return GUI的庫存
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 設置GUI中特定位置的物品
     * 
     * @param slot 位置
     * @param item 物品
     */
    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    /**
     * 清空GUI中的所有物品
     */
    protected void clearItems() {
        inventory.clear();
    }

    /**
     * 初始化GUI的內容
     * 具體實現由子類別定義
     */
    protected abstract void initializeItems();

    /**
     * 更新GUI的內容
     * 具體實現由子類別定義
     */
    protected abstract void updateItems();

    /**
     * 獲取GUI的標題
     * 
     * @return GUI的標題
     */
    public String getTitle() {
        return title;
    }

    /**
     * 獲取GUI的大小
     * 
     * @return GUI的大小
     */
    public int getSize() {
        return size;
    }
}
