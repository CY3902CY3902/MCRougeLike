package io.github.cy3902.mcroguelike.event;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.party.Party;

public class OnJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        reloadParty(event.getPlayer());
    }

    public static void reloadParty(Player player) {
        MCRogueLike mcRogueLike = MCRogueLike.getInstance();
        String existingRecord = mcRogueLike.getSql().select(
                    "SELECT party_uuid FROM `mcroguelike_party_member` WHERE member_uuid = ?",
                    new String[]{player.getUniqueId().toString()}
                );
        if (existingRecord != null) {
            String partyID = existingRecord.split(",")[0];
            Party party = mcRogueLike.getPartyRegister().get(UUID.fromString(partyID));
            if (party != null) {
                mcRogueLike.addPlayerPartyRegister(player.getUniqueId(), party);
            }else{
                Party newParty = new Party(player, UUID.fromString(partyID));
                mcRogueLike.addPartyRegister(newParty.getPartyID(), newParty);
            }
        }
    }
}
