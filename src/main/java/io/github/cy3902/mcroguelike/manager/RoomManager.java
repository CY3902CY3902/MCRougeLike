package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private final Map<String, AbstractsRoom> rooms = new HashMap<>();

    public void addRoom(String id, AbstractsRoom room) {
        rooms.put(id, room);
    }

    public void removeRoom(String id) {
        rooms.remove(id);
    }

    public void removeRoom(AbstractsRoom room) {
        rooms.values().remove(room);
    }

    public AbstractsRoom getRoom(String id) {
        return rooms.get(id);
    }

    public Map<String, AbstractsRoom> getRooms() {
        return rooms;
    }

    public void clear() {
        rooms.clear();
    }
} 