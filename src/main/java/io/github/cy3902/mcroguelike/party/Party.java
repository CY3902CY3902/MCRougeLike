package io.github.cy3902.mcroguelike.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.cy3902.mcroguelike.MCRogueLike;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Party {
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private UUID leader;
    private final HashSet<UUID> members;
    private final UUID partyID;
    private String pathID = null;

    /**
     * 構造函數，用於初始化隊伍
     * @param leader 隊長 Player 實體
     * @param partyID 隊伍 ID
     */
    public Party(Player leader, UUID partyID) {
        this.members = new HashSet<>();
        if (partyID == null) {
            this.partyID = UUID.randomUUID();
        } else {
            this.partyID = partyID;
        }
        joinParty(leader);
        setLeader(leader);
    }

    /**
     * 構造函數，用於初始化隊伍
     * @param leader 隊長 Player 實體
     */
    public Party(Player leader) {
        this(leader, null);
    }

    /**
     * 獲取隊長 Player 實體
     */
    public Player getLeader() {
        return Bukkit.getPlayer(leader);
    }

    /**
     * 設置隊長
     * @param player 隊長 Player 實體
     */
    public void setLeader(Player player) {
        this.leader = player.getUniqueId();
        mcRogueLike.getSql().update(
            "UPDATE `mcroguelike_party_member` SET `is_leader` = ? WHERE `party_uuid` = ? AND `member_uuid` = ?",
            new String[]{"true", partyID.toString(), leader.toString()}
        );
    }

    /**
     * 檢查玩家是否是隊長
     * @param player 玩家 Player 實體
     * @return 是否是隊長
     */
    public boolean isLeader(Player player) {
        if (leader == null) {
            return false;
        }
        return leader.equals(player.getUniqueId());
    }

    /**
     * 加入隊伍
     * @param player 玩家 Player 實體
     * @return 是否加入成功
     */
    public boolean joinParty(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (members.contains(uuid)) {
            return false;
        }
        
        members.add(uuid);
        updatePartyMemberInDatabase(player, uuid);
        mcRogueLike.addPlayerPartyRegister(uuid, this);
        return true;
    }

    /**
     * 更新資料庫中的隊伍成員資訊
     * @param player 玩家實體
     * @param uuid 玩家 UUID
     */
    private void updatePartyMemberInDatabase(Player player, UUID uuid) {
        String existingRecord = mcRogueLike.getSql().select(
            "SELECT * FROM `mcroguelike_party_member` WHERE `party_uuid` = ? AND `member_uuid` = ?",
            new String[]{partyID.toString(), uuid.toString()}
        );
        
        if (existingRecord == null) {
            mcRogueLike.getSql().insert(
                "INSERT INTO `mcroguelike_party_member` (`party_uuid`, `member_uuid`, `is_leader`) VALUES (?, ?, ?)",
                new String[]{partyID.toString(), uuid.toString(), isLeader(player) ? "true" : "false"}
            );
        } else {
            mcRogueLike.getSql().update(
                "UPDATE `mcroguelike_party_member` SET `is_leader` = ? WHERE `party_uuid` = ? AND `member_uuid` = ?",
                new String[]{isLeader(player) ? "true" : "false", partyID.toString(), uuid.toString()}
            );
        }
    }

    /**
     * 離開隊伍
     * @param player 玩家 Player 實體
     * @return 是否離開成功
     */
    public boolean leaveParty(Player player) {
        UUID uuid = player.getUniqueId();

        if (!members.contains(uuid)) {
            return false;
        }

        handleLeaderChange(uuid);
        removeMemberFromDatabase(uuid);
    

        return true;
    }

    /**
     * 處理隊長變更
     * @param uuid 玩家 UUID
     */
    private void handleLeaderChange(UUID uuid) {
        if (leader.equals(uuid)) {
            members.remove(uuid);
            if (!members.isEmpty()) {
                setLeader(getMembers().get(0));
            } else {
                leader = null;
            }
        } else {
            members.remove(uuid);
        }
    }

    /**
     * 從資料庫中移除成員
     * @param uuid 玩家 UUID
     */
    private void removeMemberFromDatabase(UUID uuid) {
        mcRogueLike.getSql().delete(
            "DELETE FROM `mcroguelike_party_member` WHERE `party_uuid` = ? AND `member_uuid` = ?",
            new String[]{partyID.toString(), uuid.toString()}
        );
        mcRogueLike.removePlayerPartyRegister(uuid);
    }

    /**
     * 改變隊長
     * @param currentLeader 當前隊長 Player 實體
     * @param newLeader 新隊長 Player 實體
     * @return 是否改變成功
     */
    public boolean changeLeader(Player currentLeader, Player newLeader) {
        UUID newLeaderUUID = newLeader.getUniqueId();
        if (!isLeader(currentLeader) || !members.contains(newLeaderUUID)) {
            return false;
        }
        setLeader(newLeader);
        return true;
    }

    /**
     * 檢查玩家是否在隊伍中
     * @param player 玩家 Player 實體
     * @return 是否在隊伍中
     */
    public boolean isInParty(Player player) {
        return members.contains(player.getUniqueId());
    }

    /**
     * 獲取隊伍成員 UUID 列表
     * @return 隊伍成員 UUID 列表
     */
    public List<UUID> getMemberUUIDs() {
        return new ArrayList<>(members);
    }

    /**
     * 獲取隊伍成員數量
     * @return 隊伍成員數量
     */
    public int getMemberCount() {
        return members.size();
    }

    /**
     * 獲取隊伍成員列表
     * @return 隊伍成員列表
     */
    public List<Player> getMembers() {
        List<Player> members = new ArrayList<>();
        for (UUID uuid : this.members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                members.add(player);
            }
        }
        return members;
    }


    /**
     * 獲取隊伍成員 Player 列表
     * @return 隊伍成員 Player 列表
     */
    public List<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : members) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                online.add(p);
            }
        }
        return online;
    }

    /**
     * 踢出玩家
     * @param player 玩家 Player 實體
     * @return 是否踢出成功
     */
    public boolean kickPlayer(Player player) {
        if (!members.contains(player.getUniqueId())) {
            return false;
        }
        if (leader.equals(player.getUniqueId())) {
            return false;
        } else {
            members.remove(player.getUniqueId());
        }
        mcRogueLike.getSql().delete(
            "DELETE FROM `mcroguelike_party_member` WHERE `party_uuid` = ? AND `member_uuid` = ?",
            new String[]{partyID.toString(), player.getUniqueId().toString()}
        );
        mcRogueLike.removePlayerPartyRegister(player.getUniqueId());
        return true;
    }

    /**
     * 隊伍聊天
     * @param message 訊息
     */
    public void partyChat(String message) {
        for (Player player : getOnlineMembers()) {
            player.sendMessage(message);
        }
    }

    public UUID getPartyID() {
        return partyID;
    }
}