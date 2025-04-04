package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import java.util.HashMap;
import java.util.Map;

public class SpawnpointManager {
    private final Map<String, AbstractSpawnpoint> spawnpoints = new HashMap<>();

    public void addSpawnpoint(String name, AbstractSpawnpoint spawnpoint) {
        spawnpoints.put(name, spawnpoint);
    }

    public void removeSpawnpoint(String name) {
        spawnpoints.remove(name);
    }

    public AbstractSpawnpoint getSpawnpoint(String name) {
        return spawnpoints.get(name);
    }

    public Map<String, AbstractSpawnpoint> getSpawnpoints() {
        return spawnpoints;
    }

    public void clear() {
        spawnpoints.clear();
    }
} 