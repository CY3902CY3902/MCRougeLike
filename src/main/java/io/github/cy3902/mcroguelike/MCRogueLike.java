package io.github.cy3902.mcroguelike;

import io.github.cy3902.mcroguelike.abstracts.AbstractSpawnpoint;
import io.github.cy3902.mcroguelike.abstracts.AbstractsMap;
import io.github.cy3902.mcroguelike.abstracts.AbstractsPath;
import io.github.cy3902.mcroguelike.abstracts.AbstractsSQL;
import io.github.cy3902.mcroguelike.commands.Commands;
import io.github.cy3902.mcroguelike.commands.PathCommand;
import io.github.cy3902.mcroguelike.config.ConfigFile;
import io.github.cy3902.mcroguelike.config.Lang;
import io.github.cy3902.mcroguelike.files.MapFile;
import io.github.cy3902.mcroguelike.files.PathFile;
import io.github.cy3902.mcroguelike.files.RoomFile;
import io.github.cy3902.mcroguelike.files.SpawnpointFile;
import io.github.cy3902.mcroguelike.utils.FileUtils;
import io.github.cy3902.mcroguelike.utils.MsgUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.cy3902.mcroguelike.manager.RoomManager;
import io.github.cy3902.mcroguelike.manager.MapManager;
import io.github.cy3902.mcroguelike.manager.PathManager;
import io.github.cy3902.mcroguelike.manager.SpawnpointManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class MCRogueLike extends JavaPlugin {
    private static MCRogueLike mcRogueLike;
    private static Lang lang;
    private static String DATABASE_URL;
    private static Lang.LangType langType;
    private static AbstractsSQL sql;
    private static ConfigFile configFile;

    private final MsgUtils msgUtils = new MsgUtils(this);

    private final RoomManager roomManager;
    private final MapManager mapManager;
    private final PathManager pathManager;
    private final SpawnpointManager spawnpointManager;

    public MCRogueLike() {
        mcRogueLike = this;
        this.roomManager = new RoomManager();
        this.mapManager = new MapManager();
        this.pathManager = new PathManager();
        this.spawnpointManager = new SpawnpointManager();
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
        mapManager.clear();
        pathManager.clear();
        spawnpointManager.clear();
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
        File schematicsFolder = new File(dataFolder, "schematics");

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


        // Initialize configuration files
        
        configFile = new ConfigFile(this);
        configFile.readDefault();

        lang = new Lang("Lang", langType + ".yml");
        lang.readDefault();

        roomManager.reload();
        mapManager.reload();
        pathManager.reload();
        spawnpointManager.reload();
        
        // Load and apply map rules
        for (AbstractsMap map : mapManager.getMaps().values()) {
            map.applyMapRules();
        }

        // Register commands
        registerCommands();
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("mcrougelike").setExecutor(new Commands());
        Bukkit.getPluginCommand("mcrougelike").setTabCompleter(new Commands());
        Commands.register();
        Commands.registerCommand(new PathCommand());
    }

    public static MCRogueLike getInstance() {
        return mcRogueLike;
    }

    public static Lang.LangType getLangType() {
        return langType;
    }

    public static Lang getLang() {
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

    public static AbstractsSQL getSql() {
        return sql;
    }

    public static void setLangType(Lang.LangType langType) {
        MCRogueLike.langType = langType;
    }

    public static void setSql(AbstractsSQL sql) {
        MCRogueLike.sql = sql;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public PathManager getPathManager() {
        return pathManager;
    }

    public SpawnpointManager getSpawnpointManager() {
        return spawnpointManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public String color(String msg) {
        return msgUtils.msg(msg);
    }

    public List<String> color(List<String> msg) {
        return msgUtils.msg(msg);
    }

}
