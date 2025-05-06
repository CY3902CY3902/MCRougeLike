package io.github.cy3902.mcroguelike.manager.game;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.manager.room.RoomManager;
import io.github.cy3902.mcroguelike.manager.room.SurvivalRoomManager;
import io.github.cy3902.mcroguelike.schem.Schem;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 遊戲開始管理器，用於管理遊戲開始時的狀態
 */
public class GameStartManager {
    private final AbstractPath path;
    private final RoomManager roomManager;
    private final boolean isSpecial;
    private List<Player> players;
    private final World world;
    private int scores;

    private final MCRogueLike mcroguelike = MCRogueLike.getInstance();
    private final Lang lang = mcroguelike.getLang();

    /**
     * 初始化遊戲開始管理器
     * @param path 路徑
     * @param roomManager 房間管理器
     * @param isSpecialLevel 是否為特殊關卡
     * @param world 世界
     * @param spawnLocation 生成點
     */
    public GameStartManager(AbstractPath path, AbstractRoom room ,boolean isSpecial) {
        this.path = path;
        this.isSpecial = isSpecial;
        this.players = new ArrayList<>();
        this.world = path.getMap().getMapLocation().getLocation().getWorld();
        this.scores = 0;

        AbstractMap map = path.getMap();
        Location baseLocation = map.getMapLocation().getLocation();
        this.roomManager = new SurvivalRoomManager(room, baseLocation);

    }

    /**
     * 開始遊戲
     */
    public void start(List<Player> playerList) {
        this.players = playerList;
        if (players.isEmpty()) {
            return;
        }

        // 獲取地圖
        AbstractMap map = path.getMap();
        if (map == null) {
            for (Player player : players) {
                player.sendMessage(lang.getMessage("path.gui.invalid_map"));
            }
            return;
        }
        
        // 獲取房間
        if (roomManager == null) {
            for (Player player : players) {
                player.sendMessage(lang.getMessage("path.gui.invalid_room"));
            }
            return;
        }
        
        // 計算位置
        Location baseLocation = map.getMapLocation().getLocation();
        if (baseLocation == null) {
            for (Player player : players) {
                player.sendMessage(lang.getMessage("path.gui.invalid_map_location"));
            }
            return;
        }

        // 計算生成點（根據間隔）
        int separation = map.getStructureSpawnSeparation();
        Location spawnLocation = calculateSpawnLocation(baseLocation, separation);
        
        
        
        // 生成結構
        if (roomManager != null) {
            map.updateMapLocation();
            roomManager.getRoom().loadSchematics(spawnLocation, (success) -> {
                if (success) {
                    mcroguelike.addGameStartManager(this);
                    // 傳送玩家到生成點
                    Location playerSpawnPoint = roomManager.getRoom().getPlayerSpawnPoint(world);
                    File schemFile = new File(MCRogueLike.getInstance().getDataFolder() + "/schematics/" + roomManager.getRoom().getStructureName() + ".schem");
                    Schem schem = new Schem(roomManager.getRoom().getStructureName(), schemFile, spawnLocation);
                    teleportPlayers(players, calculatePlayerSpawnLocation(schem, spawnLocation, playerSpawnPoint));
                    roomManager.start(players, world);
                    sendMessage(lang.getMessage("path.gui.start_game"));

                    // 設置房間結束回調
                    roomManager.setOnEndCallback(() -> {
                        Integer score = roomManager.calculate();
                        if (score != null && score > 0) {
                            // 分數大於0，可以繼續遊戲
                            this.scores = score;
                            //teleportPlayers(players, calculatePlayerSpawnLocation(schem, spawnLocation, playerSpawnPoint));
                        } else {
                            // 分數為0或null，遊戲失敗
                            sendMessage(lang.getMessage("path.gui.game_failed"));
                            stopGame();
                        }
                    });
                }
            });
        }
    }

    



    /**
     * 發送訊息給所有玩家
     * @param message 訊息
     */
    public void sendMessage(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    /**
     * 暫停遊戲
     */
    public void pauseGame() {
        roomManager.pause();
    }

    /**
     * 恢復遊戲
     */
    public void resumeGame() {
        roomManager.resume(world);
    }

    /**
     * 停止遊戲
     */
    public void stopGame() {
        roomManager.stop();
        mcroguelike.removeGameStartManager(this);
    }

    /**
     * 計算結構生成點
     * @param baseLocation 基礎位置
     * @param separation 間隔
     * @return 生成點
     */
    private Location calculateSpawnLocation(Location baseLocation, int separation) {
        // 根據間隔計算新的位置
        // 這裡可以根據需要調整計算邏輯
        return baseLocation.clone().add(separation, 0, 0);
    }

    /**
     * 計算玩家生成點
     * @param schem 結構
     * @param baseLocation 基礎位置
     * @param playerSpawnPoint 玩家出生點
     * @return 生成點
     */
    private Location calculatePlayerSpawnLocation(Schem schem, Location baseLocation, Location playerSpawnPoint) {
        // 獲取結構中心點
        BlockVector3 centerPoint = schem.calculateCenterPoint();
        if (centerPoint == null) {
            return baseLocation;
        }

        // 計算玩家相對於結構原點的偏移
        BlockVector3 origin = schem.getClipboard().getRegion().getMinimumPoint();
        double offsetFromOriginX = playerSpawnPoint.getX() - origin.getX();
        double offsetFromOriginY = playerSpawnPoint.getY() - origin.getY();
        double offsetFromOriginZ = playerSpawnPoint.getZ() - origin.getZ();
    

        // 計算玩家相對於結構中心的偏移
        double offsetFromCenterX = offsetFromOriginX - centerPoint.getX();
        double offsetFromCenterY = offsetFromOriginY - centerPoint.getY();
        double offsetFromCenterZ = offsetFromOriginZ - centerPoint.getZ();
       

        // 計算最終生成位置
        Location location = baseLocation.clone().add(offsetFromCenterX, offsetFromCenterY, offsetFromCenterZ);

        return location;
    }

    /**
     * 獲取路徑
     * @return 路徑
     */
    public AbstractPath getPath() {
        return path;
    }

    /**
     * 獲取房間
     * @return 房間
     */
    public RoomManager getRoomManager() {
        return roomManager;
    }

    /**
     * 獲取是否為特殊關卡
     * @return 是否為特殊關卡
     */
    public boolean isSpecial() {
        return isSpecial;
    }

    /**
     * 傳送玩家到生成點
     */
    public void teleportPlayers(List<Player> players, Location location) {
        for (Player player : players) {
            player.teleport(location);
        }
    }
}


