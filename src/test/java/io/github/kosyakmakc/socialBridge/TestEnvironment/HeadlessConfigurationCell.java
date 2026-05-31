package io.github.kosyakmakc.socialBridge.TestEnvironment;

import io.github.kosyakmakc.socialBridge.IConfigurationCell;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Headless implementation of IConfigurationCell for testing.
 * Uses in-memory HashMap for storage.
 * 
 * Three-state handling:
 * - Not existed: read() => null, isEmpty() => true
 * - Null written: read() => null, isEmpty() => false
 * - Value written: read() => value, isEmpty() => false
 * 
 * Cache is loaded once on first access and reused for all subsequent calls.
 */
public class HeadlessConfigurationCell implements IConfigurationCell {
    private final UUID moduleId;
    private final String parameterName;
    private final HeadlessMinecraftPlatform platform;
    
    // Cache state - all fields are filled atomically on first access
    private String cachedValue = null;
    private volatile boolean cacheLoaded = false;
    private boolean existsInStorage = false;
    private volatile CompletableFuture<Void> loadingFuture = null;
    
    public HeadlessConfigurationCell(UUID moduleId, String parameterName, HeadlessMinecraftPlatform platform) {
        this.moduleId = moduleId;
        this.parameterName = parameterName;
        this.platform = platform;
    }
    
    /**
     * Ensures cache is loaded from storage. This method is called by all public methods
     * to guarantee single storage access regardless of which method is called first.
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
                    var value = platform.getConfigValue(moduleId, parameterName);
                    if (value != null || platform.hasConfigKey(moduleId, parameterName)) {
                        existsInStorage = true;
                        cachedValue = value;
                    } else {
                        existsInStorage = false;
                        cachedValue = null;
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
            platform.setConfigValue(moduleId, parameterName, value);
            cachedValue = value;
            cacheLoaded = true;
            existsInStorage = true;
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isEmpty() {
        return ensureCacheLoaded().thenApply(v -> !existsInStorage);
    }
    
    @Override
    public CompletableFuture<Boolean> clear() {
        return CompletableFuture.supplyAsync(() -> {
            platform.removeConfigKey(moduleId, parameterName);
            cachedValue = null;
            cacheLoaded = true;
            existsInStorage = false;
            return true;
        });
    }
}
