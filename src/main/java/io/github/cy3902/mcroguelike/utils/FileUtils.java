package io.github.cy3902.mcroguelike.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 工具類別，用於處理與檔案和資源相關的操作。
 */
public class FileUtils {

    /**
     * 從插件的 JAR 檔案中複製資源資料夾到指定的目的地資料夾。
     *
     * @param plugin        插件實例
     * @param resourcePath  要複製的資源路徑
     * @param destination   目標資料夾
     * @throws IOException  如果複製過程中出現錯誤，則拋出 IOException
     */
    public static void copyResourceFolder(JavaPlugin plugin, String resourcePath, File destination) throws IOException {
        // 如果目標資料夾不存在，則創建它
        if (!destination.exists()) {
            destination.mkdirs();
        }

        // 獲取 JAR 檔案的路徑
        String jarPath = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        jarPath = java.net.URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        // 使用 JarFile 讀取 JAR 檔案
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // 檢查資源路徑是否匹配
                if (entryName.startsWith(resourcePath) && !entry.isDirectory()) {
                    File destFile = new File(destination, entryName.substring(resourcePath.length()));
                    // 創建目標文件的父目錄
                    destFile.getParentFile().mkdirs();
                    // 複製文件
                    try (InputStream in = jar.getInputStream(entry);
                         FileOutputStream out = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    /**
     * 從插件的資源中複製單個檔案到指定的目的地檔案。
     *
     * @param plugin        插件實例
     * @param resourcePath  要複製的資源路徑
     * @param destination   目標檔案
     * @throws IOException  如果複製過程中出現錯誤，則拋出 IOException
     */
    public static void copyResourceFile(JavaPlugin plugin, String resourcePath, File destination) throws IOException {
        try (InputStream resourceStream = plugin.getResource(resourcePath)) {
            if (resourceStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            try (FileOutputStream outStream = new FileOutputStream(destination)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = resourceStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }
            }
        }
    }

    /**
     * 測試方法，用於輸出資源文件的位置和內容
     *
     * @param plugin 插件實例
     */
    public static void testResourceFiles(JavaPlugin plugin) {
        try {
            // 輸出 JAR 文件路徑
            String jarPath = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            jarPath = java.net.URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
            plugin.getLogger().info("JAR 文件路徑: " + jarPath);

            // 使用 JarFile 讀取 JAR 文件
            try (JarFile jar = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jar.entries();
                boolean foundResources = false;

                plugin.getLogger().info("JAR 文件中的資源:");
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    // 檢查是否為 MCRogueLike 目錄下的文件
                    if (entryName.startsWith("MCRogueLike/")) {
                        foundResources = true;
                        plugin.getLogger().info("  - " + entryName);
                    }
                }

                if (!foundResources) {
                    plugin.getLogger().warning("未找到 MCRogueLike 目錄下的任何資源文件!");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("測試資源文件時出錯: " + e.getMessage());
            e.printStackTrace();
        }
    }
}