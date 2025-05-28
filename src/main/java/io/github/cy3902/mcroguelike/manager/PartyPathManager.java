package io.github.cy3902.mcroguelike.manager;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.abstracts.AbstractPath;
import io.github.cy3902.mcroguelike.party.Party;

import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class PartyPathManager {
    private final MCRogueLike mcroguelike;
    private final Party party;
    private AbstractPath path;
    private List<UUID> pathPlayers;

    public PartyPathManager(Party party) {
        this.mcroguelike = MCRogueLike.getInstance();
        this.party = party;
        this.path = null;
        this.pathPlayers = new ArrayList<>();
    }

    public void setPath(AbstractPath abstractPath) {
        // 檢查是否已存在路徑
        String existingPath = mcroguelike.getSql().select(
            "SELECT path FROM " + "mcroguelike_party_path" + " WHERE party_uuid = ?",
            new String[]{party.getPartyID().toString()}
        );

        if (existingPath == null) {
            // 生成樹狀結構
            abstractPath.generateTree();

            // 將路徑轉換為 JSON
            String pathJson = abstractPath.convertPathToJson(party);

            pathPlayers = party.getMembers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toList());

            // 插入新路徑
            mcroguelike.getSql().update(
                "INSERT INTO " + "mcroguelike_party_path" + " (party_uuid, path) VALUES (?, ?)",
                new String[]{party.getPartyID().toString(), abstractPath.getPathUUID().toString()}
            );

            // 創建並寫入檔案
            savePathToFile(abstractPath.getPathUUID().toString(), pathJson);
            
            this.path = abstractPath;
        } else {
            // 從數據庫讀取 JSON 數據檔案名稱
            String pathJson = mcroguelike.getSql().select(
                "SELECT path FROM " + "mcroguelike_party_path" + " WHERE party_uuid = ?",
                new String[]{party.getPartyID().toString()}
            );

            if (pathJson != null) {
                AbstractPath loadedPath = loadPathFromFile(pathJson);
                if (loadedPath != null) {
                    this.path = loadedPath;
                    pathPlayers.addAll(loadedPath.getPartyMembers());
                } else {
                    // 如果讀取失敗，刪除舊路徑並重新生成
                    deletePath(pathJson);
                    setPath(abstractPath);
                }
            }
        }
    }

    public AbstractPath getPath() {
        if (this.path == null) {
            String existingPath = mcroguelike.getSql().select(
                "SELECT path FROM " + "mcroguelike_party_path" + " WHERE party_uuid = ?",
                new String[]{party.getPartyID().toString()}
            );

            if (existingPath != null) {
                this.path = loadPathFromFile(existingPath);
            } else {
                deletePath(existingPath);
            }
        }
        return this.path;
    }

    public void deletePath(String pathJson) {
        mcroguelike.getSql().delete(
            "DELETE FROM " + "mcroguelike_party_path" + " WHERE party_uuid = ?",
            new String[]{party.getPartyID().toString()}
        );
        // 刪除檔案
        File file = new File(mcroguelike.getDataFolder(), "PlayerPath/" + pathJson + ".json");
        if (file.exists()) {
            file.delete();
        }
        this.path = null;
    }

    /**
     * 將路徑保存到檔案
     * @param fileName 檔案名稱
     * @param pathJson JSON 內容
     */
    private void savePathToFile(String fileName, String pathJson) {
        File file = new File(mcroguelike.getDataFolder(), "PlayerPath/" + fileName + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            Files.write(file.toPath(), pathJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 從檔案讀取路徑
     * @param fileName 檔案名稱
     * @return 讀取到的路徑，如果讀取失敗則返回 null
     */
    private AbstractPath loadPathFromFile(String fileName) {
        File file = new File(mcroguelike.getDataFolder(), "PlayerPath/" + fileName + ".json");
        if (!file.exists()) {
            return null;
        }
    
        try {
            String jsonContent = new String(Files.readAllBytes(file.toPath()));
            JSONObject json = new JSONObject(jsonContent);
            String pathType = json.getString("pathType");
    
            Class<? extends AbstractPath> pathClass = mcroguelike.getPathRegister().get(pathType);
            if (pathClass == null) {
                deletePath(fileName);
                return null;
            }
    
            // 用反射建立實例（確保有 public 無參數建構子）
            AbstractPath path = pathClass.getDeclaredConstructor().newInstance();
    
            // 使用你定義的方法轉換 JSON
            if (!path.convertPathFromJson(jsonContent)) {
                deletePath(fileName);
                return null;
            }
    
            return path;
        } catch (IOException | InstantiationException | IllegalAccessException |
        InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
} 