package io.github.kosyakmakc.socialBridge.paper;

import io.github.kosyakmakc.socialBridge.IConfigurationCell;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Paper-specific implementation of IConfigurationCell.
 * Uses JavaPlugin.getConfig() for YAML access.
 * 
 * Three-state handling:
 * - Not existed: read() => null, isEmpty() => true
 * - Null written: read() => null, isEmpty() => false
 * - Value written: read() => value, isEmpty() => false
 * 
 * Cache is loaded once on first access and reused for all subsequent calls.
 */
public class PaperConfigurationCell implements IConfigurationCell {
    private final UUID moduleId;
    private final String parameterName;
    private final JavaPlugin plugin;
    
    // Cache state - all fields are filled atomically on first access
    private String cachedValue = null;
    private volatile boolean cacheLoaded = false;
    private boolean existsInStorage = false;
    private volatile CompletableFuture<Void> loadingFuture = null;
    
    public PaperConfigurationCell(UUID moduleId, String parameterName, JavaPlugin plugin) {
        this.moduleId = moduleId;
        this.parameterName = parameterName;
        this.plugin = plugin;
    }
    
    /**
     * Ensures cache is loaded from storage. This method is called by all public methods
     * to guarantee single config access regardless of which method is called first.
     * Uses a shared CompletableFuture so all callers wait for the same loading operation.
     */
    private CompletableFuture<Void> ensureCacheLoaded() {
        // Fast path: if loading is already complete, return immediately
        if (cacheLoaded) {
            return CompletableFuture.completedFuture(null);
        }
        
        // Fast path: if another thread is already loading, wait on the same future
        CompletableFuture<Void> existingFuture = loadingFuture;
        if (existingFuture != null) {
            return existingFuture;
        }
        
        synchronized (this) {
            if (cacheLoaded) {
                return CompletableFuture.completedFuture(null);
            }
            
            existingFuture = loadingFuture;
            if (existingFuture != null) {
                return existingFuture;
            }
            
            // Create the loading future - all concurrent callers will wait on this same future
            CompletableFuture<Void> newFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    var config = plugin.getConfig();
                    var moduleSection = config.getConfigurationSection("module-" + moduleId.toString());
                    if (moduleSection == null) {
                        existsInStorage = false;
                        cachedValue = null;
                    } else {
                        // Check if the key exists in the section (even if value is null)
                        existsInStorage = moduleSection.contains(parameterName);
                        cachedValue = moduleSection.getString(parameterName);
                    }
                } finally {
                    cacheLoaded = true;
                    loadingFuture = null;
                }
                return null;
            });
            
            loadingFuture = newFuture;
            return newFuture;
        }
    }
    
    @Override
    public CompletableFuture<String> read() {
        return ensureCacheLoaded().thenApply(v -> cachedValue);
    }
    
    @Override
    public CompletableFuture<Boolean> write(String value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var config = plugin.getConfig();
                var moduleSection = config.getConfigurationSection("module-" + moduleId.toString());
                if (moduleSection == null) {
                    moduleSection = config.createSection("module-" + moduleId.toString());
                }
                moduleSection.set(parameterName, value);
                plugin.saveConfig();
                cachedValue = value;
                cacheLoaded = true;
                existsInStorage = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isEmpty() {
        return ensureCacheLoaded().thenApply(v -> !existsInStorage);
    }
    
    @Override
    public CompletableFuture<Boolean> clear() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var config = plugin.getConfig();
                var moduleSection = config.getConfigurationSection("module-" + moduleId.toString());
                if (moduleSection != null) {
                    moduleSection.set(parameterName, null);
                    plugin.saveConfig();
                }
                cachedValue = null;
                cacheLoaded = true;
                existsInStorage = false;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
