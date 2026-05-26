package io.github.kosyakmakc.socialBridge.DatabasePlatform;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRow;
import io.github.kosyakmakc.socialBridge.IConfigurationCellScoped;
import io.github.kosyakmakc.socialBridge.ITransaction;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Scoped configuration cell for database-backed configuration.
 * 
 * Three-state handling:
 * - Not existed: read() => null, isEmpty() => true
 * - Null written: read() => null, isEmpty() => false
 * - Value written: read() => value, isEmpty() => false
 * 
 * Cache is loaded once on first access and reused for all subsequent calls.
 */
public class ConfigurationCellScoped implements IConfigurationCellScoped {
    private final UUID moduleId;
    private final String parameterName;
    private final ITransaction transaction;
    
    // Cache state - all fields are filled atomically on first access
    private String cachedValue = null;
    private volatile boolean cacheLoaded = false;
    private boolean existsInStorage = false;
    private volatile CompletableFuture<Void> loadingFuture = null;
    
    public ConfigurationCellScoped(UUID moduleId, String parameterName, ITransaction transaction) {
        this.moduleId = moduleId;
        this.parameterName = parameterName;
        this.transaction = transaction;
    }
    
    /**
     * Checks if the transaction is closed and throws IllegalStateException if so.
     * This prevents using configuration cells after the transaction has been closed.
     */
    private void throwIfClosed() {
        if (transaction.isClosed()) {
            throw new IllegalStateException("Cannot access configuration cell after transaction is closed");
        }
    }
    
    /**
     * Ensures cache is loaded from storage. This method is called by all public methods
     * to guarantee single database access regardless of which method is called first.
     * Uses a shared CompletableFuture so all callers wait for the same loading operation.
     */
    private CompletableFuture<Void> ensureCacheLoaded() {
        throwIfClosed();
        
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
                    var records = transaction.getDatabaseContext().configurations.queryBuilder()
                                .where()
                                    .eq(ConfigRow.MODULE_FIELD_NAME, moduleId)
                                    .and()
                                    .eq(ConfigRow.PARAMETER_FIELD_NAME, parameterName)
                                .query();
                    if (records.size() > 0) {
                        existsInStorage = true;
                        cachedValue = records.getFirst().getValue();
                    } else {
                        existsInStorage = false;
                        cachedValue = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    existsInStorage = false;
                    cachedValue = null;
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
        throwIfClosed();
        return ensureCacheLoaded().thenApply(v -> cachedValue);
    }
    
    @Override
    public CompletableFuture<Boolean> write(String value) {
        throwIfClosed();
        return writeToDatabase(value).thenApply(success -> {
            if (success) {
                cachedValue = value;
                cacheLoaded = true;
                existsInStorage = true;
            }
            return success;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isEmpty() {
        throwIfClosed();
        return ensureCacheLoaded().thenApply(v -> !existsInStorage);
    }
    
    @Override
    public CompletableFuture<Boolean> clear() {
        throwIfClosed();
        return deleteFromDatabase().thenApply(success -> {
            if (success) {
                cachedValue = null;
                cacheLoaded = true;
                existsInStorage = false;
            }
            return success;
        });
    }
    
    private CompletableFuture<Boolean> writeToDatabase(String value) {
        try {
            var databaseContext = transaction.getDatabaseContext();
            var records = databaseContext.configurations.queryBuilder()
                        .where()
                            .eq(ConfigRow.MODULE_FIELD_NAME, moduleId)
                            .and()
                            .eq(ConfigRow.PARAMETER_FIELD_NAME, parameterName)
                        .query();
            if (records.size() > 0) {
                var record = records.getFirst();
                record.setValue(value);
                databaseContext.configurations.update(record);
            } else {
                var newRecord = new ConfigRow(moduleId, parameterName, value);
                databaseContext.configurations.create(newRecord);
            }
            return CompletableFuture.completedFuture(true);
        } catch (SQLException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }
    
    private CompletableFuture<Boolean> deleteFromDatabase() {
        try {
            var databaseContext = transaction.getDatabaseContext();
            var records = databaseContext.configurations.queryBuilder()
                        .where()
                            .eq(ConfigRow.MODULE_FIELD_NAME, moduleId)
                            .and()
                            .eq(ConfigRow.PARAMETER_FIELD_NAME, parameterName)
                        .query();
            if (records.size() > 0) {
                databaseContext.configurations.delete(records.getFirst());
            }
            return CompletableFuture.completedFuture(true);
        } catch (SQLException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }
}
