package io.github.cy3902.mcroguelike.manager.room;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.antlr.v4.parse.ANTLRParser.prequelConstruct_return;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.sk89q.worldedit.math.BlockVector3;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.github.cy3902.mcroguelike.abstracts.AbstractMob;
import io.github.cy3902.mcroguelike.schem.Schem;
import io.lumine.mythic.core.mobs.ActiveMob;

public class SpawnPointManager {

    private HashMap<AbstractSpawnpoint, Location> spawnPoints;
    private AbstractRoom room;
    private boolean isSpawning = false;
    private final Random random = new Random();

    public SpawnPointManager(AbstractRoom room, HashMap<AbstractSpawnpoint, Location> spawnPoints) {
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
        // 獲取結構中心點
        BlockVector3 centerPoint = schem.calculateCenterPoint();
        if (centerPoint == null) {
            return baseLocation;
        }

        // 計算玩家相對於結構原點的偏移
        BlockVector3 origin = schem.getClipboard().getRegion().getMinimumPoint();
        double offsetFromOriginX = mobSpawnPoint.getX() - origin.getX();
        double offsetFromOriginY = mobSpawnPoint.getY() - origin.getY();
        double offsetFromOriginZ = mobSpawnPoint.getZ() - origin.getZ();
    

        // 計算玩家相對於結構中心的偏移
        double offsetFromCenterX = offsetFromOriginX - centerPoint.getX();
        double offsetFromCenterY = offsetFromOriginY - centerPoint.getY();
        double offsetFromCenterZ = offsetFromOriginZ - centerPoint.getZ();
       

        // 計算最終生成位置
        Location location = baseLocation.clone().add(offsetFromCenterX, offsetFromCenterY, offsetFromCenterZ);

        return location;
    }

    /**
     * 生成怪物
     * @param location 生成位置
     * @return 生成的怪物實體
     */
    public LivingEntity spawn(Location baselocation) {
        if (!isSpawning) {
            return null;
        }

        File schemFile = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics/" + room.getStructureName() + ".schem");
        Schem schem = new Schem(room.getStructureName(), schemFile, baselocation);
        
        // 遍歷所有生成點，找到可以生成的點
        for (AbstractSpawnpoint spawnpoint : spawnPoints.keySet()) {
            Location location = calculateMobSpawnLocation(schem, baselocation, spawnPoints.get(spawnpoint));
            if (spawnpoint.canSpawn(location)) {
                // 生成所有怪物類型
                List<AbstractMob> mobs = spawnpoint.getMobs();
                if (mobs.isEmpty()) {
                    continue;
                }
                
                LivingEntity lastEntity = null;
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
                                
                                lastEntity = livingEntity;
                                spawnpoint.incrementCurrentSpawns();
                            }
                        }
                    }
                }
                
                return lastEntity;
            }
        }
        
        return null;
    }

    /**
     * 停止生成
     */
    public void stopSpawning() {
        isSpawning = false;
        for (AbstractSpawnpoint spawnpoint : spawnPoints.keySet()) {
            spawnpoint.stopSpawning();
        }
    }

    /**
     * 重置生成點
     */
    public void reset() {
        isSpawning = true;
        for (AbstractSpawnpoint spawnpoint : spawnPoints.keySet()) {
            spawnpoint.reset();
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