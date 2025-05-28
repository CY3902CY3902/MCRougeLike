package io.github.cy3902.mcroguelike.commands;

import io.github.cy3902.mcroguelike.abstracts.AbstractCommand;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.gui.PathGUI;
import io.github.cy3902.mcroguelike.manager.PartyPathManager;
import io.github.cy3902.mcroguelike.party.Party;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand extends AbstractCommand {
    /**
     * 處理GUI相關指令。
     */
    public TestCommand() {
        super("mcroguelike.test", "test", 2);
    }

    /**
     * 處理GUI指令。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     */
    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.getMessage("player_only"));
            return;
        }
        Player player = (Player) sender;

        // 獲取玩家所在的Party
        Party party = mcRogueLike.getPlayerPartyRegister().get(player.getUniqueId());
        if (party == null) {
            player.sendMessage(lang.getMessage("path.gui.no_party"));
            return;
        }

        // 獲取PartyPathManager
        PartyPathManager partyPathManager = mcRogueLike.getPartyPathManagerRegister().get(party.getPartyID());
        if (partyPathManager == null) {
            partyPathManager = new PartyPathManager(party);
            mcRogueLike.addPartyPathManagerRegister(party.getPartyID(), partyPathManager);
        }

        // 獲取路徑
        AbstractPath path = partyPathManager.getPath();
        if (path == null) {
            path = mcRogueLike.getPathFile().getPath(args[1]); 
            partyPathManager.setPath(path);
        }

        // 開啟GUI
        PathGUI gui = new PathGUI(path);
        gui.openGUI(player);
    }

    /**
     * 提供自動完成建議。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 自動完成選項列表
     */
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
