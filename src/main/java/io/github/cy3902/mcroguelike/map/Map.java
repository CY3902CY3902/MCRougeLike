package io.github.cy3902.mcroguelike.map;

import io.github.cy3902.mcroguelike.abstracts.AbstractMap;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public class Map extends AbstractMap {
    public Map(String name, String structureSpawnPoint, int structureSpawnSeparation, boolean mobGriefing, boolean doDaylightCycle, 
               boolean doWeatherCycle, boolean keepInventory, boolean doMobSpawning, 
               boolean pvp, String weather, boolean allowExplosions) {
        super(name, structureSpawnPoint, structureSpawnSeparation, mobGriefing, doDaylightCycle, doWeatherCycle, keepInventory, doMobSpawning, pvp, weather, allowExplosions);
    }

    @Override
    public void applyMapRules() {
        World world = Bukkit.getWorld(name);
        if(world != null) {
            // 設置遊戲規則
            GameRule<Boolean> MOB_GRIEFING = GameRule.MOB_GRIEFING;
            GameRule<Boolean> DO_DAYLIGHT_CYCLE = GameRule.DO_DAYLIGHT_CYCLE;
            GameRule<Boolean> DO_WEATHER_CYCLE = GameRule.DO_WEATHER_CYCLE;
            GameRule<Boolean> KEEP_INVENTORY = GameRule.KEEP_INVENTORY;
            GameRule<Boolean> DO_MOB_SPAWNING = GameRule.DO_MOB_SPAWNING;

            world.setGameRule(MOB_GRIEFING, mobGriefing);
            world.setGameRule(DO_DAYLIGHT_CYCLE, doDaylightCycle);
            world.setGameRule(DO_WEATHER_CYCLE, doWeatherCycle);
            world.setGameRule(KEEP_INVENTORY, keepInventory);
            world.setGameRule(DO_MOB_SPAWNING, doMobSpawning);

            // 設置PVP
            world.setPVP(pvp);

            // 設置天氣
            switch(weather.toLowerCase()) {
                case "clear":
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case "rain":
                    world.setStorm(true);
                    world.setThundering(false);
                    break;
                case "thunder":
                    world.setStorm(true);
                    world.setThundering(true);
                    break;
            }
            
        }
    }
} 