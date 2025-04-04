package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private final List<AbstractsMap> maps = new ArrayList<>();

    public void addMap(AbstractsMap map) {
        maps.add(map);
    }

    public void removeMap(AbstractsMap map) {
        maps.remove(map);
    }

    public List<AbstractsMap> getMaps() {
        return maps;
    }

    public void clear() {
        maps.clear();
    }
} 