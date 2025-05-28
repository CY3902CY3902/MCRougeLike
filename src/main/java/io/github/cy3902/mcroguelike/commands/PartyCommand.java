package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.manager.PartyPathManager;
import io.github.cy3902.mcroguelike.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartyCommand extends AbstractCommand implements CommandExecutor {
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private final Lang lang;

    public PartyCommand() {
        super("mcroguelike.party", "party", Arrays.asList(1, 2, 3));
        this.lang = mcRogueLike.getLang();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("command.player_only"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("mcroguelike.party")) {
            player.sendMessage(lang.getMessage("command.no_permission"));
            return true;
        }

        if (args.length == 1) {
            // 顯示隊伍指令幫助
            showHelp(player);
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "create":
                createParty(player);
                break;
            case "join":
                if (args.length < 3) {
                    player.sendMessage(lang.getMessage("party.need_party_name"));
                    return true;
                }
                List<Party> inviteParty = mcRogueLike.getPartyInviteRegister().get(player.getUniqueId());
                for (Party party : inviteParty) {
                    if (party.getLeader().getName().equals(args[2])) {
                        joinParty(player, party.getPartyID());
                        player.sendMessage(lang.getMessage("party.invite_already_in_party"));
                        break;
                    }
                }
                break;
            case "leave":
                leaveParty(player);
                break;
            case "invite":
                if (args.length < 3) {
                    player.sendMessage(lang.getMessage("party.need_player_name"));
                    return true;
                }
                invitePlayer(player, Bukkit.getPlayer(args[2]));
                break;
            case "kick":
                if (args.length < 3) {
                    player.sendMessage(lang.getMessage("party.need_player_name"));
                    return true;
                }
                kickPlayer(player, Bukkit.getPlayer(args[2]));
                break;
            case "transfer":
                if (args.length < 3) {
                    player.sendMessage(lang.getMessage("party.need_player_name"));
                    return true;
                }
                transferLeader(player, Bukkit.getPlayer(args[2]));
                break;
            case "info":
                showPartyInfo(player);
                break;
            default:
                showHelp(player);
                break;
        }

        return true;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        onCommand(sender, null, null, args);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("create", "join", "leave", "invite", "kick", "transfer", "info");
        }
        // 如果指令是join，則回傳所有邀請的隊伍名稱
        if (args.length == 3 && args[1].equals("join")) {
            Player player = (Player) sender;
            if (mcRogueLike.getPartyInviteRegister().get(player.getUniqueId()) != null) {
                return mcRogueLike.getPartyInviteRegister().values().stream()
                    .flatMap(parties -> parties.stream())
                    .map(Party::getLeader)
                    .map(Player::getName)
                    .collect(Collectors.toList());
            }
        }
        // 如果指令是transfer，則回傳所有隊伍成員
        if (args.length == 3 && args[1].equals("transfer")) {
            Player player = (Player) sender;
            Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
            if (party != null && party.getMembers().contains(Bukkit.getPlayer(args[2]))) {
                return party.getMembers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }
        // 如果指令是kick，則回傳所有隊伍成員
        if (args.length == 3 && args[1].equals("kick")) {
            Player player = (Player) sender;
            Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
            if (party != null && party.getMembers().contains(Bukkit.getPlayer(args[2]))) {
                return party.getMembers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }
        // 如果指令是invite，則回傳所有在線玩家
        if (args.length == 3 && args[1].equals("invite")) {
            Player player = (Player) sender;
            Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
            if (party != null && !party.getMembers().contains(Bukkit.getPlayer(args[2]))) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }
        return null;
    }

    private void showHelp(Player player) {
        player.sendMessage(lang.getMessage("party.help"));
    }

    private void createParty(Player player) {
        if (mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId()) != null) {
            player.sendMessage(lang.getMessage("party.party_already_created"));
            return;
        }
        Party party = new Party(player);
        party.joinParty(player);
        party.setLeader(player);
        mcRogueLike.addPartyRegister(party.getPartyID(), party);
        player.sendMessage(lang.getMessage("party.party_created"));
    }

    private void joinParty(Player player, UUID partyID) {
        Party inviteParty = mcRogueLike.getPartyRegister().get(partyID);
        if (inviteParty == null) {
            player.sendMessage(lang.getMessage("party.party_not_found"));
            return;
        }
        if (mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId()) != null) {
            player.sendMessage(lang.getMessage("party.party_already_created"));
            return;
        }
        inviteParty.joinParty(player);
        mcRogueLike.addPartyRegister(player.getUniqueId(), inviteParty);
        mcRogueLike.removePartyInviteRegister(inviteParty.getPartyID());
        inviteParty.partyChat(lang.getMessage("party.party_joined").replace("%player%", player.getName()));
    }

    private void leaveParty(Player player) {
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null) {
            player.sendMessage(lang.getMessage("party.party_not_found"));
            return;
        }
        if (party.leaveParty(player)){
            player.sendMessage(lang.getMessage("party.party_leave"));
            party.partyChat(lang.getMessage("party.party_leave").replace("%player%", player.getName()));
            mcRogueLike.removePlayerPartyRegister(player.getUniqueId());
        }

        // 如果隊伍中沒有玩家，則刪除路徑
        if (party.getMembers().isEmpty()) {
            PartyPathManager partyPathManager = mcRogueLike.getPartyPathManagerRegister().get(party.getPartyID());
            AbstractPath path = partyPathManager.getPath();
            if (partyPathManager != null && path != null) {
                partyPathManager.deletePath(path.getPathUUID().toString());
                mcRogueLike.removePartyPathManagerRegister(party.getPartyID());
            }
        }
    }

    private void invitePlayer(Player player, Player target) {
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null) {
            player.sendMessage(lang.getMessage("party.party_not_found"));
            return;
        }
        // 檢查自己是否是隊長
        if (party.isLeader(player)) {
            player.sendMessage(lang.getMessage("party.invite_self"));
            return;
        }
        // 檢查目標是否在隊伍中
        if (party.isInParty(target)) {
            player.sendMessage(lang.getMessage("party.invite_already_in_party"));
            return;
        }
        // 檢查是否已經有邀請
        if (mcRogueLike.getPartyInviteRegister().get(target.getUniqueId()) != null) {
            player.sendMessage(lang.getMessage("party.invite_already_in_party"));
            return;
        }
        // 檢查是否已經在其他隊伍中
        if (party.isInParty(player)) {
            player.sendMessage(lang.getMessage("party.invite_already_in_party"));
            return;
        }
        // 檢查是否在線上
        if (target == null || !target.isOnline()) {
            player.sendMessage(lang.getMessage("party.invite_offline"));
            return;
        }
        // 添加邀請
        mcRogueLike.addPartyInviteRegister(party.getPartyID(), party);
        player.sendMessage(lang.getMessage("party.invite_sent"));
        // 發送邀請訊息
        target.sendMessage(lang.getMessage("party.invite_received").replace("%player%", player.getName()).replace("%leader%", party.getLeader().getName()));
        // 持續時間
        new BukkitRunnable() {
            @Override
            public void run() {
                mcRogueLike.removePartyInviteRegister(party.getPartyID(), party);
            }
        }.runTaskLater(mcRogueLike, 20 * 30);
    }

    private void kickPlayer(Player player, Player kickPlayer) {
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null) {
            player.sendMessage(lang.getMessage("party.party_not_found"));
            return;
        }
        if (!party.getMembers().contains(kickPlayer)) {
            player.sendMessage(lang.getMessage("party.kick_not_in_party"));
            return;
        }
        if (party.isLeader(player)) {   
            player.sendMessage(lang.getMessage("party.kick_leader"));
            return;
        }
        party.kickPlayer(kickPlayer);
    }

    private void transferLeader(Player player, Player newLeader) {
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null) {
            player.sendMessage(lang.getMessage("party.party_not_found"));
            return;
        }
        if (!party.getMembers().contains(newLeader)) {
            player.sendMessage(lang.getMessage("party.transfer_leader_not_in_party"));
            return;
        }
        if (party.isLeader(newLeader)) {
            player.sendMessage(lang.getMessage("party.transfer_leader_self"));
            return;
        }
        party.changeLeader(player, newLeader);
    }

    private void showPartyInfo(Player player) {
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null) {
            player.sendMessage(lang.getMessage("party.party_not_found"));
            return;
        }
        player.sendMessage(lang.getMessage("party.info").replace("%leader%", party.getLeader().getName()).replace("%members%", String.valueOf(party.getMembers().size())));
    }
} 