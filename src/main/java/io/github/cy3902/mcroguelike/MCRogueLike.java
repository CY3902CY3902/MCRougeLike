package io.github.cy3902.mcroguelike;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.abstracts.AbstractSQL;
import io.github.cy3902.mcroguelike.commands.Commands;
import io.github.cy3902.mcroguelike.config.ConfigFile;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.event.OnJoin;
import io.github.cy3902.mcroguelike.event.OnQuit;
import io.github.cy3902.mcroguelike.files.MapFile;
import io.github.cy3902.mcroguelike.files.PathFile;
import io.github.cy3902.mcroguelike.files.RoomFile;
import io.github.cy3902.mcroguelike.files.SpawnpointFile;
import io.github.cy3902.mcroguelike.manager.PartyPathManager;
import io.github.cy3902.mcroguelike.manager.game.GameStartManager;
import io.github.cy3902.mcroguelike.path.Path;
import io.github.cy3902.mcroguelike.room.AnnihilationRoom;
import io.github.cy3902.mcroguelike.room.SurvivalRoom;
import io.github.cy3902.mcroguelike.utils.FileUtils;
import io.github.cy3902.mcroguelike.utils.MsgUtils;
import io.github.cy3902.mcroguelike.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class MCRogueLike extends JavaPlugin {
    private static MCRogueLike mcRogueLike;
    private Lang lang;
    private MapFile mapFile;
    private PathFile pathFile;
    private RoomFile roomFile;
    private SpawnpointFile spawnpointFile;

    private Lang.LangType langType;
    private AbstractSQL sql;
    private ConfigFile configFile;

    private final MsgUtils msgUtils = new MsgUtils(this);

    //註冊Path
    private HashMap<String, Class<? extends AbstractPath>> pathRegister = new HashMap<>();
    //註冊Room
    private HashMap<String, Class<? extends AbstractRoom>> roomRegister = new HashMap<>();
    //註冊Spawnpoint
    private HashMap<String, Class<? extends AbstractSpawnpoint>> spawnpointRegister = new HashMap<>();

    //註冊GameStartManager
    private HashMap<UUID, GameStartManager> gameStartManagerRegister = new HashMap<>();

    //註冊PartyPathManager
    private HashMap<UUID, PartyPathManager> partyPathManagerRegister = new HashMap<>();

    //註冊Party
    private HashMap<UUID, Party> partyRegister = new HashMap<>();

    //註冊Party邀請
    private HashMap<UUID, List<Party>> partyInviteRegister = new HashMap<>();

    //註冊玩家的party
    private HashMap<UUID, Party> playerPartyRegister = new HashMap<>();

    public MCRogueLike() {
        mcRogueLike = this;
    }

    @Override
    public void onEnable() {
        try {
            initEssential();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to initialize plugin", e);
        }
    }

    @Override
    public void onDisable() {
        // Clean up resources if needed
        sql = null;
        lang = null;
        configFile = null;
    }

    public void initEssential() throws IOException {


        // Initialize folders
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File langFolder = new File(dataFolder, "Lang");
        File sqlFolder = new File(dataFolder, "SQL");
        File mapFolder = new File(dataFolder, "Map");
        File pathFolder = new File(dataFolder, "Path");
        File roomFolder = new File(dataFolder, "Room");
        File spawnpointFolder = new File(dataFolder, "Spawnpoint");
        File schematicsFolder = new File(dataFolder, "Schematics");
        File playerPathFolder = new File(dataFolder, "PlayerPath");

        // Create and populate folders
        if (!langFolder.exists()) {
            
            langFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "MCRogueLike/Lang", langFolder);
        }
        if (!pathFolder.exists()) {
            
            pathFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "MCRogueLike/Path", pathFolder);
        }
        if (!sqlFolder.exists()) {
            sqlFolder.mkdirs();
        }
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdirs();
        }
        if (!mapFolder.exists()) {
            mapFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "MCRogueLike/Map", mapFolder);
        }
        if (!roomFolder.exists()) {
            roomFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "MCRogueLike/Room", roomFolder);
        }
        if (!spawnpointFolder.exists()) {
            spawnpointFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "MCRogueLike/Spawnpoint", spawnpointFolder);
        }   
        if (!playerPathFolder.exists()) {
            playerPathFolder.mkdirs();
        }


        // Initialize configuration files
        configFile = new ConfigFile(this);
        configFile.reload();
        lang = new Lang("Lang", langType + ".yml");
        lang.reload();
        mapFile = new MapFile();
        mapFile.reloadAll();
        pathFile = new PathFile();
        pathFile.reloadAll();
        spawnpointFile = new SpawnpointFile();
        spawnpointFile.reloadAll();
        roomFile = new RoomFile();
        roomFile.reloadAll();

        //註冊Path
        pathRegister.put("Path", Path.class);

        //註冊Room
        roomRegister.put("Survival", SurvivalRoom.class);
        roomRegister.put("Annihilation", AnnihilationRoom.class);

        // Load and apply map rules
        for (AbstractMap map : mapFile.getAllMaps().values()) {
            map.applyMapRules();
        }

        // 註冊事件
        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
        Bukkit.getPluginManager().registerEvents(new OnQuit(), this);

        // Register commands
        registerCommands();

        // 重新載入所有玩家的party
        for (Player player : Bukkit.getOnlinePlayers()) {
            OnJoin.reloadParty(player);
        }
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("mcrougelike").setExecutor(new Commands());
        Bukkit.getPluginCommand("mcrougelike").setTabCompleter(new Commands());
        Commands.register();
    }

    public static MCRogueLike getInstance() {
        return mcRogueLike;
    }

    public Lang.LangType getLangType() {
        return langType;
    }

    public Lang getLang() {
        return lang;
    }

    /**
     * Logs a message to the plugin's logger.
     *
     * @param msg The message to log
     * @param level The logging level
     */
    public void info(String msg, Level level) {
        getLogger().log(level, msg);
    }

    public AbstractSQL getSql() {
        return sql;
    }

    public void setLangType(Lang.LangType langType) {
        this.langType = langType;
    }

    public void setSql(AbstractSQL sql) {
        this.sql = sql;
    }


    public String color(String msg) {
        return msgUtils.msg(msg);
    }

    public List<String> color(List<String> msg) {
        return msgUtils.msg(msg);
    }

    public MapFile getMapFile() {
        return mapFile;
    }

    public PathFile getPathFile() {
        return pathFile;
    }       

    public RoomFile getRoomFile() {
        return roomFile;
    }

    public SpawnpointFile getSpawnpointFile() {
        return spawnpointFile;
    }

    // 獲取Party註冊
    public HashMap<String, Class<? extends AbstractPath>> getPathRegister() {
        return pathRegister;
    }

    // 獲取Room註冊
    public HashMap<String, Class<? extends AbstractRoom>> getRoomRegister() {
        return roomRegister;
    }

    // 獲取Spawnpoint註冊
    public HashMap<String, Class<? extends AbstractSpawnpoint>> getSpawnpointRegister() {
        return spawnpointRegister;
    }
    
    // 獲取GameStartManager註冊
    public HashMap<UUID, GameStartManager> getGameStartManagerRegister() {
        return gameStartManagerRegister;
    }

    // 添加GameStartManager註冊
    public void addGameStartManagerRegister(UUID uuid, GameStartManager gameStartManager) {
        gameStartManagerRegister.put(uuid, gameStartManager);
    }

    // 移除GameStartManager註冊
    public void removeGameStartManagerRegister(UUID uuid) {
        gameStartManagerRegister.remove(uuid);
    }

    // 獲取Party註冊
    public HashMap<UUID, Party> getPartyRegister() {
        return partyRegister;
    }

    // 添加Party註冊
    public void addPartyRegister(UUID uuid, Party party) {
        partyRegister.put(uuid, party);
    }

    // 移除Party註冊
    public void removePartyRegister(UUID uuid) {
        partyRegister.remove(uuid);
    }

    // 獲取Party邀請註冊
    public HashMap<UUID, List<Party>> getPartyInviteRegister() {
        return partyInviteRegister;
    }

    // 添加Party邀請註冊
    public void addPartyInviteRegister(UUID uuid, Party party) {
        if (partyInviteRegister.containsKey(uuid)) {
            partyInviteRegister.get(uuid).add(party);
        } else {
            partyInviteRegister.put(uuid, new ArrayList<>(Arrays.asList(party)));
        }
    }

    // 移除Party邀請註冊  
    public void removePartyInviteRegister(UUID uuid) {
        partyInviteRegister.remove(uuid);
    }

    
    public void removePartyInviteRegister(UUID uuid, Party party) {
        partyInviteRegister.get(uuid).remove(party);
    }

    // 獲取玩家的party
    public HashMap<UUID, Party> getPlayerPartyRegister() {
        return playerPartyRegister;
    }

    // 添加玩家的party
    public void addPlayerPartyRegister(UUID uuid, Party party) {
        playerPartyRegister.put(uuid, party);
    }

    // 移除玩家的party
    public void removePlayerPartyRegister(UUID uuid) {
        playerPartyRegister.remove(uuid);
    }

    // 獲取PartyPathManager註冊
    public HashMap<UUID, PartyPathManager> getPartyPathManagerRegister() {
        return partyPathManagerRegister;
    }

    // 添加PartyPathManager註冊
    public void addPartyPathManagerRegister(UUID uuid, PartyPathManager partyPathManager) {
        partyPathManagerRegister.put(uuid, partyPathManager);
    }

    // 移除PartyPathManager註冊
    public void removePartyPathManagerRegister(UUID uuid) {
        partyPathManagerRegister.remove(uuid);
    }
    
    
    
    
    

}
