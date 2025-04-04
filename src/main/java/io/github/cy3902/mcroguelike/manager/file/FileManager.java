package io.github.cy3902.mcroguelike.manager.file;

import java.util.List;

import io.github.cy3902.mcroguelike.abstracts.FileProvider;

/**
 * 文件管理器接口
 * 用於管理多個文件提供者
 */
public interface FileManager {
    /**
     * 註冊文件提供者
     * @param name 提供者名稱
     * @param provider 文件提供者
     * @param <T> 配置對象的類型
     */
    <T> void registerFileProvider(String name, FileProvider<T> provider);

    /**
     * 獲取文件提供者
     * @param name 提供者名稱
     * @param <T> 配置對象的類型
     * @return 文件提供者
     */
    <T> FileProvider<T> getFileProvider(String name);

    /**
     * 重新加載所有文件
     */
    void reloadAll();

    /**
     * 保存所有文件
     */
    void saveAll();

    /**
     * 從目錄加載所有文件
     * @param directory 目錄名
     * @param <T> 配置對象的類型
     * @return 對象列表
     */
    <T> List<T> loadAllFromDirectory(String directory);

    /**
     * 保存所有對象到目錄
     * @param directory 目錄名
     * @param items 對象列表
     * @param <T> 配置對象的類型
     */
    <T> void saveToDirectory(String directory, List<T> items);
} 