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
    private boolean cacheLoaded = false;
    private boolean existsInStorage = false;
    
    public ConfigurationCellScoped(UUID moduleId, String parameterName, ITransaction transaction) {
        this.moduleId = moduleId;
        this.parameterName = parameterName;
        this.transaction = transaction;
    }
    
    /**
     * Ensures cache is loaded from storage. This method is called by all public methods
     * to guarantee single database access regardless of which method is called first.
     */
    private CompletableFuture<Void> ensureCacheLoaded() {
        if (cacheLoaded) {
            return CompletableFuture.completedFuture(null);
        }
        
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
            cacheLoaded = true;
            return CompletableFuture.completedFuture(null);
        } catch (SQLException e) {
            e.printStackTrace();
            existsInStorage = false;
            cachedValue = null;
            cacheLoaded = true;
            return CompletableFuture.completedFuture(null);
        }
    }
    
    @Override
    public CompletableFuture<String> read() {
        return ensureCacheLoaded().thenApply(v -> cachedValue);
    }
    
    @Override
    public CompletableFuture<Boolean> write(String value) {
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
        return ensureCacheLoaded().thenApply(v -> !existsInStorage);
    }
    
    @Override
    public CompletableFuture<Boolean> clear() {
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
