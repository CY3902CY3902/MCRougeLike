package io.github.cy3902.mcroguelike.manager.room;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;


import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom.SpawnPoint;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.github.cy3902.mcroguelike.abstracts.AbstractMob;
import io.github.cy3902.mcroguelike.schem.Schem;
import io.github.cy3902.mcroguelike.utils.LocationUtils;
import io.lumine.mythic.core.mobs.ActiveMob;

public class SpawnPointManager {

    private List<SpawnPoint> spawnPoints;
    private AbstractRoom room;
    private boolean isSpawning = true;
    private final Random random = new Random();

    public SpawnPointManager(AbstractRoom room, List<SpawnPoint> spawnPoints) {
        this.room = room;
        this.spawnPoints = spawnPoints;
    }

    /**
     * 設置怪物的屬性倍率
     * @param entity 怪物實體
     * @param healthMultiplier 生命值倍率
     * @param damageMultiplier 傷害倍率
     * @param speedMultiplier 速度倍率
     */
    private void setEntityMultipliers(LivingEntity entity, double healthMultiplier, double damageMultiplier, double speedMultiplier) {
        // 設置生命值
        double maxHealth = entity.getMaxHealth();
        entity.setMaxHealth(maxHealth * healthMultiplier);
        entity.setHealth(entity.getMaxHealth());
        
    }


    /**
     * 計算怪物生成點
     * @param schem 結構
     * @param baseLocation 基礎位置
     * @param playerSpawnPoint 怪物出生點
     * @return 生成點
     */
    private Location calculateMobSpawnLocation(Schem schem, Location baseLocation, Location mobSpawnPoint) {
        Location centerPoint = schem.getCenterPoint();
        if (centerPoint == null) {
            return baseLocation;
        }
    
        // 3. 計算 playerSpawnPoint 相對於 schematic 的偏移
        double dx = mobSpawnPoint.getX() - centerPoint.getX();
        double dy = mobSpawnPoint.getY() - centerPoint.getY();
        double dz = mobSpawnPoint.getZ() - centerPoint.getZ();
    
        // 4. 套用偏移到實際 paste 的 baseLocation
        return baseLocation.clone().add(dx, dy, dz);
    }
    

    /**
     * 生成怪物
     * @param location 生成位置
     */
    public void spawn(Location baselocation) {
        if (!isSpawning) {
            return;
        }

        File schemFile = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics/" + room.getStructureName() + ".schem");
        Schem schem = new Schem(room.getStructureName(), schemFile, baselocation.getWorld());
        
        for (SpawnPoint spawnpoint : spawnPoints) {
            Location spawnpointLocation = LocationUtils.stringToLocation(baselocation.getWorld(), spawnpoint.getLocation());
            Location location = calculateMobSpawnLocation(schem, baselocation, spawnpointLocation);
            if (spawnpoint.getSpawnpoint().canSpawn(location)) {
                // 生成所有怪物類型
                List<AbstractMob> mobs = spawnpoint.getSpawnpoint().getMobs();
                if (mobs.isEmpty()) {
                    continue;
                }
                
                for (AbstractMob mob : mobs) {
                    Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(mob.getMobId());
                    if (mythicMob.isPresent()) {
                        // 設置怪物屬性
                        MythicMob mythicMobInstance = mythicMob.get();
                        double healthMultiplier = mob.getHealthMultiplier();
                        double damageMultiplier = mob.getDamageMultiplier();
                        double speedMultiplier = mob.getSpeedMultiplier();
                        
                        // 生成怪物
                        AbstractLocation abstractLocation = new AbstractLocation(location.getWorld().getName(), 
                        location.getX(), location.getY(), location.getZ());
                        ActiveMob activeMob = mythicMobInstance.spawn(abstractLocation, 1.0);
                        
                        if (activeMob != null) {
                            // 獲取實體
                            org.bukkit.entity.Entity entity = activeMob.getEntity().getBukkitEntity();
                            if (entity instanceof LivingEntity) {
                                LivingEntity livingEntity = (LivingEntity) entity;
                                
                                // 設置倍率
                                setEntityMultipliers(livingEntity, healthMultiplier, damageMultiplier, speedMultiplier);
                                spawnpoint.getSpawnpoint().incrementCurrentSpawns();
                            }
                        }
                    }
                }
                
            }
        }
    }

    /**
     * 停止生成
     */
    public void stopSpawning() {
        isSpawning = false;
        for (SpawnPoint spawnpoint : spawnPoints) {
            spawnpoint.getSpawnpoint().stopSpawning();
        }
    }

    /**
     * 重置生成點
     */
    public void reset() {
        isSpawning = true;
        for (SpawnPoint spawnpoint : spawnPoints) {
            spawnpoint.getSpawnpoint().reset();
        }
    }

    /**
     * 開始生成
     */
    public void startSpawning() {
        isSpawning = true;
    }

    /**
     * 獲取是否正在生成
     * @return 是否正在生成
     */
    public boolean isSpawning() {
        return isSpawning;
    }
} 