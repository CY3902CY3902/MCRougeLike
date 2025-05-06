package io.github.cy3902.mcroguelike;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractMap;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractRoom;
import io.github.cy3902.mcroguelike.abstracts.AbstractSQL;
import io.github.cy3902.mcroguelike.commands.Commands;
import io.github.cy3902.mcroguelike.config.ConfigFile;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.files.MapFile;
import io.github.cy3902.mcroguelike.files.PathFile;
import io.github.cy3902.mcroguelike.files.RoomFile;
import io.github.cy3902.mcroguelike.files.SpawnpointFile;
import io.github.cy3902.mcroguelike.manager.game.GameStartManager;
import io.github.cy3902.mcroguelike.path.Path;
import io.github.cy3902.mcroguelike.room.AnnihilationRoom;
import io.github.cy3902.mcroguelike.room.SurvivalRoom;
import io.github.cy3902.mcroguelike.utils.FileUtils;
import io.github.cy3902.mcroguelike.utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final List<GameStartManager> gameStartManagers = new ArrayList<>();

    //註冊Path
    private HashMap<String, Class<? extends AbstractPath>> pathRegister = new HashMap<>();
    //註冊Room
    private HashMap<String, Class<? extends AbstractRoom>> roomRegister = new HashMap<>();
    //註冊Spawnpoint
    private HashMap<String, Class<? extends AbstractSpawnpoint>> spawnpointRegister = new HashMap<>();



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
        roomFile = new RoomFile();
        roomFile.reloadAll();
        spawnpointFile = new SpawnpointFile();
        spawnpointFile.reloadAll();

        //註冊Path
        pathRegister.put("Path", Path.class);

        //註冊Room
        roomRegister.put("Survival", SurvivalRoom.class);
        roomRegister.put("Annihilation", AnnihilationRoom.class);



        // Load and apply map rules
        for (AbstractMap map : mapFile.getAllMaps().values()) {
            map.applyMapRules();
        }

        // Register commands
        registerCommands();
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

    public List<GameStartManager> getGameStartManagers() {
        return gameStartManagers;
    }

    public void addGameStartManager(GameStartManager gameStartManager) {
        gameStartManagers.add(gameStartManager);
    }

    public void removeGameStartManager(GameStartManager gameStartManager) {
        gameStartManagers.remove(gameStartManager);
    }

    public void clearGameStartManagers() {
        gameStartManagers.clear();
    }

    public HashMap<String, Class<? extends AbstractPath>> getPathRegister() {
        return pathRegister;
    }

    public HashMap<String, Class<? extends AbstractRoom>> getRoomRegister() {
        return roomRegister;
    }
    
}
