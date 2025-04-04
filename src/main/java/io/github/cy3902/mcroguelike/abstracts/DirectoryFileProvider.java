package io.github.cy3902.mcroguelike.abstracts;

import java.io.File;
import java.util.List;

/**
 * 目錄文件提供者接口
 * 用於處理目錄中的文件操作
 * @param <T> 配置對象的類型
 */
public interface DirectoryFileProvider<T> {
    /**
     * 從目錄加載所有對象
     * @param directory 目錄名
     * @return 對象列表
     */
    List<T> loadAllFromDirectory(String directory);

    /**
     * 保存所有對象到目錄
     * @param directory 目錄名
     * @param items 對象列表
     */
    void saveToDirectory(String directory, List<T> items);

    /**
     * 從文件加載對象
     * @param file 文件
     * @return 對象
     */
    T loadFromFile(File file);

    /**
     * 保存對象到文件
     * @param file 文件
     * @param item 對象
     */
    void saveToFile(File file, T item);
} 