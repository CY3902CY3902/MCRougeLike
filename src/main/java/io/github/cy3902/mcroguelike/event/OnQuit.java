package io.github.cy3902.mcroguelike.event;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.party.Party;

public class OnQuit implements Listener {
    private final MCRogueLike mcRogueLike = MCRogueLike.getInstance();
    private final Lang lang = mcRogueLike.getLang();
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Party party = onQuitParty(event.getPlayer());
        if (party != null) {
            party.partyChat(lang.getMessage("party.player_quit").replace("%player%", event.getPlayer().getName()));
        }
    }

    public static Party onQuitParty(Player player) {
        MCRogueLike mcRogueLike = MCRogueLike.getInstance();
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party != null) {
            mcRogueLike.removePlayerPartyRegister(player.getUniqueId());
            return party;
        }
        return null;
    }
}
