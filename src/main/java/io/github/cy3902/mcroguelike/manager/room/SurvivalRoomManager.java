package io.github.cy3902.mcroguelike.manager.room;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.bossbar.bossbar;
import io.github.cy3902.mcroguelike.config.Lang;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通房間管理器
 * 實現了基本的房間管理功能
 */
public class SurvivalRoomManager implements RoomManager {
    private final AbstractRoom room;
    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private final List<LivingEntity> activeEnemies;
    private BukkitTask spawnTask;
    private BukkitTask timerTask;
    private SpawnPointManager spawnPointManager;
    private bossbar bossbar;
    private Location baseLocation;
    private List<Player> players;
    private List<Player> deadPlayers;
    private final Lang lang = mcroguelike.getLang();
    private Runnable onEndCallback;

    // 房間狀態
    private boolean isRunning;
    private boolean isPaused;
    private int remainingTime;

    public SurvivalRoomManager(AbstractRoom room, Location baseLocation) {
        String survivalTime = lang.getMessage("bossbar.survival_time").replace("%time%", String.valueOf(room.getTimeLimit()))
        .replace("%max_time%", String.valueOf(room.getTimeLimit()));
        this.room = room;
        this.activeEnemies = new ArrayList<>();
        this.isRunning = false;
        this.isPaused = false;
        this.remainingTime = room.getTimeLimit();
        this.players = new ArrayList<>();
        this.deadPlayers = new ArrayList<>();
        this.baseLocation = baseLocation;
        this.spawnPointManager = new SpawnPointManager(room, room.getSpawnPoints());
        this.bossbar = new bossbar(survivalTime, remainingTime,remainingTime);
    }

    /**
     * 開始房間
     * @param players 玩家列表
     * @param world 世界
     */
    @Override
    public void start(List<Player> players, World world) {
        if (isRunning) {
            return;
        }
        bossbar.createBossBar();
        this.players = players;
        isRunning = true;
        isPaused = false;
        remainingTime = room.getTimeLimit();
        
        // 綁定玩家bossbar
        for (Player player : players) {
            bindPlayer(player);
        }

        // 開始生成怪物
        startSpawning(baseLocation);

        // 開始計時
        startTimer();
    }

    /**
     * 暫停房間
     */
    @Override
    public void pause() {
        if (!isRunning || isPaused) {
            return;
        }

        isPaused = true;
        
        // 暫停生成怪物
        if (spawnTask != null) {
            spawnTask.cancel();
            spawnTask = null;
        }

        // 暫停計時
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        // 暫停所有敵人的AI
        for (LivingEntity enemy : activeEnemies) {
            enemy.setAI(false);
        }
    }

    /**
     * 恢復房間
     * @param world 世界
     */
    @Override
    public void resume(World world) {
        if (!isRunning || !isPaused) {
            return;
        }

        isPaused = false;
        
        // 恢復生成怪物
        startSpawning(baseLocation);

        // 恢復計時
        startTimer();

        // 恢復所有敵人的AI
        for (LivingEntity enemy : activeEnemies) {
            enemy.setAI(true);
        }
    }


    /**
     * 停止房間
     */
    @Override
    public void stop() {
        isRunning = true;
        isPaused = true;

        // 停止生成怪物
        if (spawnTask != null) {
            spawnTask.cancel();
            spawnTask = null;
        }

        // 停止計時
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        // 清理所有活著的敵人
        for (LivingEntity enemy : activeEnemies) {
            enemy.setAI(false);
        }
    }

    /**
     * 結束房間
     */
    @Override
    public void end() {
        isRunning = false;
        isPaused = false;

        // 清理所有活著的敵人
        for (LivingEntity enemy : activeEnemies) {
            enemy.remove();
        }
        activeEnemies.clear();

        // 重置房間
        players.clear();

        // 解綁玩家bossbar
        for (Player player : players) {
            unbindPlayer(player);
        }

        // 執行結束回調
        if (onEndCallback != null) {
            onEndCallback.run();
        }
    }

    /**
     * 結算房間
     */
    @Override
    public Integer calculate() {
        // 結算房間
        if (players.size() == 0) {
            return 0;
        }
        if (deadPlayers.size() == players.size()) {
            // 所有玩家都死了
            return 0;
        }
        for (Player player : players) {
            if (deadPlayers.contains(player)) {
                continue;
            }
        }
        return 0;
    }




    /**
     * 獲取房間
     * @return 房間
     */
    @Override
    public AbstractRoom getRoom() {
        return room;
    }

    /**
     * 檢查房間是否正在運行
     * @return 是否正在運行
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 檢查房間是否暫停
     * @return 是否暫停
     */
    @Override
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 更新剩餘時間
     * @param newTime 新的剩餘時間
     */
    @Override
    public void updateRemainingTime(int newTime) {
        remainingTime = newTime;
    }

    /**
     * 獲取剩餘時間
     * @return 剩餘時間
     */
    @Override
    public int getRemainingTime() {
        return remainingTime;
    }

    @Override
    public void addEnemy(LivingEntity enemy) {
        activeEnemies.add(enemy);
    }

    /**
     * 移除敵人
     * @param enemy 敵人
     */
    @Override
    public void removeEnemy(LivingEntity enemy) {
        activeEnemies.remove(enemy);
    }

    /**
     * 獲取當前活著的敵人列表
     * @return 當前活著的敵人列表
     */
    @Override
    public List<LivingEntity> getActiveEnemies() {
        return new ArrayList<>(activeEnemies);
    }

    /**
     * 開始生成怪物
     * @param world 世界
     */
    private void startSpawning(Location baseLocation) {
        spawnPointManager.spawn(baseLocation);
    }

    /**
     * 開始計時
     */
    private void startTimer() {
        timerTask = Bukkit.getScheduler().runTaskTimer(room.getPlugin(), () -> {
            if (!isRunning || isPaused) {
                return;
            }

            if (remainingTime <= 0) {
                end();
                calculate();
                return;
            }

            remainingTime--;
            setBossbarHealth(remainingTime);
        }, 0L, 20L); // 每秒更新一次
    }

    /**
     * 設置bossbar
     * @param bossbar bossbar
     */
    @Override
    public void setBossbar(bossbar bossbar) {
        this.bossbar = bossbar;
    }

    @Override
    public void bindPlayer(Player player) {
        bossbar.bindPlayer(player);
    }

    /**
     * 解綁玩家
     * @param player 玩家
     */
    @Override
    public void unbindPlayer(Player player) {
        bossbar.unbindPlayer(player);
    }

    /**
     * 設置bossbar標題
     * @param title 標題
     */
    public void setBossbarTitle(String title) {
        bossbar.setTitle(title);
    }   

    /**
     * 設置bossbar血量
     * @param health 血量
     */
    public void setBossbarHealth(double health) {
        bossbar.setHealth(health);
    }

    /**
     * 設置bossbar最大血量
     * @param maxHealth 最大血量
     */
    public void setBossbarMaxHealth(double maxHealth) {
        bossbar.setMaxHealth(maxHealth);
    }

    @Override
    public void setOnEndCallback(Runnable callback) {
        this.onEndCallback = callback;
    }
} 