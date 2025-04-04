package io.github.cy3902.mcroguelike.manager.file;

import io.github.cy3902.mcroguelike.abstracts.FileProvider;
import io.github.cy3902.mcroguelike.abstracts.DirectoryFileProvider;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理器實現類
 * 用於管理多個文件提供者
 */
public class FileManagerImpl implements FileManager {
    private final Plugin plugin;
    private final Map<String, FileProvider<?>> providers;

    public FileManagerImpl(Plugin plugin) {
        this.plugin = plugin;
        this.providers = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void registerFileProvider(String name, FileProvider<T> provider) {
        providers.put(name, provider);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> FileProvider<T> getFileProvider(String name) {
        return (FileProvider<T>) providers.get(name);
    }

    @Override
    public void reloadAll() {
        for (FileProvider<?> provider : providers.values()) {
            provider.reload();
        }
    }

    @Override
    public void saveAll() {
        for (FileProvider<?> provider : providers.values()) {
            try {
                // 使用反射調用save方法，避免類型不匹配問題
                Object value = provider.getClass().getMethod("load").invoke(provider);
                provider.getClass().getMethod("save", value.getClass()).invoke(provider, value);
            } catch (Exception e) {
                plugin.getLogger().severe("Error saving file: " + provider.getFile().getName());
                e.printStackTrace();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> loadAllFromDirectory(String directory) {
        for (FileProvider<?> provider : providers.values()) {
            if (provider instanceof DirectoryFileProvider) {
                return ((DirectoryFileProvider<T>) provider).loadAllFromDirectory(directory);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void saveToDirectory(String directory, List<T> items) {
        for (FileProvider<?> provider : providers.values()) {
            if (provider instanceof DirectoryFileProvider) {
                ((DirectoryFileProvider<T>) provider).saveToDirectory(directory, items);
                return;
            }
        }
    }
} 