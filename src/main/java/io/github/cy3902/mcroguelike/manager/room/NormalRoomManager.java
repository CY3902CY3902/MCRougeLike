package io.github.cy3902.mcroguelike.manager.room;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import org.bukkit.Bukkit;
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
public class NormalRoomManager implements RoomManager {
    private final AbstractsRoom room;
    private final List<LivingEntity> activeEnemies;
    private BukkitTask spawnTask;
    private BukkitTask timerTask;

    public NormalRoomManager(AbstractsRoom room) {
        this.room = room;
        this.activeEnemies = new ArrayList<>();
        room.setManager(this);
    }

    @Override
    public void start(List<Player> players, World world) {
        if (room.isRunning()) {
            return;
        }

        room.initialize(players);
        room.setRunning(true);
        room.setPaused(false);
        room.setRemainingTime(room.getTimeLimit());

        // 開始生成怪物
        startSpawning(world);

        // 開始計時
        startTimer();
    }

    @Override
    public void pause() {
        if (!room.isRunning() || room.isPaused()) {
            return;
        }

        room.setPaused(true);
        
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

    @Override
    public void resume(World world) {
        if (!room.isRunning() || !room.isPaused()) {
            return;
        }

        room.setPaused(false);
        
        // 恢復生成怪物
        startSpawning(world);

        // 恢復計時
        startTimer();

        // 恢復所有敵人的AI
        for (LivingEntity enemy : activeEnemies) {
            enemy.setAI(true);
        }
    }

    @Override
    public void stop() {
        room.setRunning(false);
        room.setPaused(false);

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
            enemy.remove();
        }
        activeEnemies.clear();

        // 重置房間
        room.reset();
    }

    @Override
    public AbstractsRoom getRoom() {
        return room;
    }

    @Override
    public boolean isRunning() {
        return room.isRunning();
    }

    @Override
    public boolean isPaused() {
        return room.isPaused();
    }

    @Override
    public void updateRemainingTime(int newTime) {
        room.setRemainingTime(newTime);
    }

    @Override
    public int getRemainingTime() {
        return room.getRemainingTime();
    }

    @Override
    public void addEnemy(LivingEntity enemy) {
        activeEnemies.add(enemy);
    }

    @Override
    public void removeEnemy(LivingEntity enemy) {
        activeEnemies.remove(enemy);
    }

    @Override
    public List<LivingEntity> getActiveEnemies() {
        return new ArrayList<>(activeEnemies);
    }

    private void startSpawning(World world) {
        spawnTask = Bukkit.getScheduler().runTaskTimer(room.getPlugin(), () -> {
            if (!room.isRunning() || room.isPaused()) {
                return;
            }

            for (AbstractSpawnpoint spawnPoint : room.getSpawnPoints().keySet()) {
                LivingEntity enemy = spawnPoint.spawn(room.getSpawnPoints().get(spawnPoint));
                if (enemy != null) {
                    addEnemy(enemy);
                }
            }
        }, 0L, 20L); // 每秒生成一次
    }

    private void startTimer() {
        timerTask = Bukkit.getScheduler().runTaskTimer(room.getPlugin(), () -> {
            if (!room.isRunning() || room.isPaused()) {
                return;
            }

            int remainingTime = room.getRemainingTime();
            if (remainingTime <= 0) {
                stop();
                return;
            }

            room.setRemainingTime(remainingTime - 1);
        }, 0L, 20L); // 每秒更新一次
    }
} 