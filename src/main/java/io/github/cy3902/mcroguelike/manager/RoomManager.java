package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractsRoom;
import io.github.cy3902.mcroguelike.files.RoomFile;
import io.github.cy3902.mcroguelike.manager.core.Manager;
import io.github.cy3902.mcroguelike.config.RoomConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 房間管理器
 * 用於管理所有房間
 */
public class RoomManager implements Manager {
    private final Map<String, AbstractsRoom> rooms = new HashMap<>();
    private final Map<String, RoomFile> roomFiles = new HashMap<>();
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();

    /**
     * 構造函數
     */
    public RoomManager() {
        loadRooms();
    }


    @Override
    public void reload() {
        rooms.clear();
        roomFiles.clear();
        
        loadRooms();
    }

    /**
     * 加載所有房間
     */
    private void loadRooms() {
        RoomFile roomFile = new RoomFile();
        for (String roomId : roomFile.getAllConfigs().keySet()) {
            roomFiles.put(roomId, roomFile);
            rooms.put(roomId, roomFile.getRoom(roomId));
        }
    }

    /**
     * 獲取指定ID的房間
     * @param roomId 房間ID
     * @return 房間物件
     */
    public AbstractsRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * 獲取所有房間
     * @return 房間列表
     */
    public Map<String, AbstractsRoom> getRooms() {
        return rooms;
    }

    /**
     * 保存所有房間
     */
    @Override
    public void save() {
        for (Map.Entry<String, RoomFile> entry : roomFiles.entrySet()) {
            String roomId = entry.getKey();
            RoomFile roomFile = entry.getValue();
            RoomConfig config = roomFile.getConfig(roomId);
            if (config != null) {
                try {
                    roomFile.saveRoom(roomId, config);
                } catch (Exception e) {
                    mcRogueLike.getLogger().log(Level.SEVERE, "Error saving room: " + roomId, e);
                }
            }
        }
    }

    @Override
    public void shutdown() {
        save();
        rooms.clear();
        roomFiles.clear();
    }

    /**
     * 添加房間
     * @param roomId 房間ID
     * @param room 房間實例
     */
    public void addRoom(String roomId, AbstractsRoom room) {
        rooms.put(roomId, room);
        RoomFile roomFile = new RoomFile();
        roomFiles.put(roomId, roomFile);
    }

    /**
     * 移除房間
     * @param roomId 房間ID
     */
    public void removeRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            rooms.remove(roomId);
            roomFiles.remove(roomId);
        }
    }

    /**
     * 清除所有房間
     */
    public void clear() {
        rooms.clear();
        roomFiles.clear();
    }
} 