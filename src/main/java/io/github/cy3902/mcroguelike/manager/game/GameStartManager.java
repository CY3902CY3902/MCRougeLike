package io.github.cy3902.mcroguelike.manager.game;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.manager.room.RoomManager;
import io.github.cy3902.mcroguelike.manager.room.SurvivalRoomManager;
import io.github.cy3902.mcroguelike.party.Party;
import io.github.cy3902.mcroguelike.schem.Schem;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

/**
 * 遊戲開始管理器，用於管理遊戲開始時的狀態
 */
public class GameStartManager {
    private final AbstractPath path;
    private final RoomManager roomManager;
    private final boolean isSpecial;
    private Party party;
    private final World world;
    private int score;

    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private final Lang lang = mcRogueLike.getLang();

    /**
     * 初始化遊戲開始管理器
     * @param path 路徑
     * @param roomManager 房間管理器
     * @param isSpecialLevel 是否為特殊關卡
     * @param world 世界
     * @param spawnLocation 生成點
     */
    public GameStartManager(Party party, AbstractPath path, AbstractRoom room ,boolean isSpecial) {
        this.path = path;
        this.isSpecial = isSpecial;
        this.world = path.getMap().getMapLocation().getLocation().getWorld();
        this.score = 0;

        AbstractMap map = path.getMap();
        Location baseLocation = map.getMapLocation().getLocation();
        this.roomManager = new SurvivalRoomManager(room, baseLocation, party);

    }

    /**
     * 開始遊戲
     */
    public void start() {
        if (party.getMemberUUIDs().isEmpty()) {
            return;
        }

        // 獲取地圖
        AbstractMap map = path.getMap();
        if (map == null) {
            party.partyChat(lang.getMessage("path.gui.invalid_map"));
            return;
        }
        
        // 獲取房間
        if (roomManager == null) {
            party.partyChat(lang.getMessage("path.gui.invalid_room"));
            return;
        }
        
        // 計算位置
        Location baseLocation = map.getMapLocation().getLocation();
        if (baseLocation == null) {
            party.partyChat(lang.getMessage("path.gui.invalid_map_location"));
            return;
        }

        // 計算生成點（根據間隔）
        int separation = map.getStructureSpawnSeparation();
        Location spawnLocation = calculateSpawnLocation(baseLocation, separation);
        
        
        // 生成結構
        if (roomManager != null) {
            // 更新地圖位置
            map.updateMapLocation();
            roomManager.getRoom().loadSchematics(spawnLocation, (success) -> {
                if (success) {
                    roomManager.setBaseLocation(spawnLocation);
                    mcRogueLike.addGameStartManagerRegister(party.getPartyID(), this);

                    // 傳送玩家到生成點
                    Location playerSpawnPoint = roomManager.getRoom().getPlayerSpawnPoint(world);
                    File schemFile = new File(mcRogueLike.getDataFolder() + "/schematics/" + roomManager.getRoom().getStructureName() + ".schem");
                    Schem schem = new Schem(roomManager.getRoom().getStructureName(), schemFile, world);
                    teleportPlayers(party.getOnlineMembers(), calculatePlayerSpawnLocation(schem, spawnLocation, playerSpawnPoint));
                    roomManager.start(party, world);

                    // 設置房間結束回調
                    roomManager.setOnEndCallback(() -> {
                        Integer score = roomManager.calculate();
                        if (score != null && score > 0) {
                            // 分數大於0，可以繼續遊戲
                            this.score += score;
                            //teleportPlayers(players, calculatePlayerSpawnLocation(schem, spawnLocation, playerSpawnPoint));
                        } else {
                            // 分數為0或null，遊戲失敗
                            party.partyChat(lang.getMessage("path.gui.game_failed"));
                            stopGame();
                        }
                    });
                }
            });
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
        mcRogueLike.removeGameStartManagerRegister(party.getPartyID());
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
        Location centerPoint = schem.getCenterPoint();
        if (centerPoint == null) {
            return baseLocation;
        }
    
        // 3. 計算 playerSpawnPoint 相對於 schematic 的偏移
        double dx = playerSpawnPoint.getX() - centerPoint.getX();
        double dy = playerSpawnPoint.getY() - centerPoint.getY();
        double dz = playerSpawnPoint.getZ() - centerPoint.getZ();;
        // 4. 套用偏移到實際 paste 的 baseLocation
        return baseLocation.clone().add(dx, dy, dz);
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


