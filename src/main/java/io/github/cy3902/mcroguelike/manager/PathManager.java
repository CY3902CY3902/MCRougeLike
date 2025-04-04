package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import java.util.ArrayList;
import java.util.List;

public class PathManager {
    private final List<AbstractsPath> paths = new ArrayList<>();

    public void addPath(AbstractsPath path) {
        paths.add(path);
    }

    public void removePath(AbstractsPath path) {
        paths.remove(path);
    }

    public List<AbstractsPath> getPaths() {
        return paths;
    }

    public void clear() {
        paths.clear();
    }
} 