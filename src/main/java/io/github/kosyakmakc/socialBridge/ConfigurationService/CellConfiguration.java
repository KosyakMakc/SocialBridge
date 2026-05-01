package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DatabaseContext;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRow;

public class CellConfiguration implements ICellConfiguration {
    private final UUID moduleId;
    private final String parameterName;
    private final ITransaction transaction;

    private boolean valueLoaded = false;
    private String valueCache = null;

    public CellConfiguration(UUID moduleId, String parameterName, ITransaction transaction) {
        if (parameterName.isBlank()) {
            throw new RuntimeException("Empty parameter name is not allowed");
        }

        if (transaction == null) {
            throw new RuntimeException("Transaction instance required, but got null value");
        }

        if (moduleId == new UUID(0, 0)) {
            throw new RuntimeException("moduleId required, but got empty value");
        }

        this.moduleId = moduleId;
        this.parameterName = parameterName;
        this.transaction = transaction;
    }

    @Override
    public CompletableFuture<String> get() {
        if (valueLoaded) {
            return CompletableFuture.completedFuture(valueCache);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();
            
            try {
                var record = findRow(databaseContext, moduleId, parameterName);
                if (record != null) {
                    var value = record.getValue();

                    valueLoaded = true;
                    valueCache = value;

                    return value;
                }
            } catch (SQLException e) {
                // skip first sql query to empty database
                if (!e.getMessage().equals("[SQLITE_ERROR] SQL error or missing database (no such table: config)")) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> set(String value) {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var record = findRow(databaseContext, moduleId, parameterName);
                if (record != null) {
                    record.setValue(value);
                    databaseContext.getDaoTable(ConfigRow.class).update(record);

                    valueLoaded = true;
                    valueCache = value;

                    return false;
                } else {
                    var newRecord = new ConfigRow(moduleId, parameterName, value);
                    databaseContext.getDaoTable(ConfigRow.class).create(newRecord);

                    valueLoaded = true;
                    valueCache = value;

                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isEmpty() {
        if (valueLoaded) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var record = findRow(databaseContext, moduleId, parameterName);
                return record == null;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> clear() {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var record = findRow(databaseContext, moduleId, parameterName);
                if (record != null) {
                    var deletedItems = databaseContext.getDaoTable(ConfigRow.class).delete(record);
                    if (deletedItems == 1) {
                        valueLoaded = false;
                        valueCache = null;

                        return true;
                    }
                    else {
                        throw new RuntimeException("delete in database failed");
                    }
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    private static ConfigRow findRow(DatabaseContext databaseContext, UUID moduleId, String parameterName) throws SQLException {
        var records = databaseContext.getDaoTable(ConfigRow.class).queryBuilder()
            .where()
                .eq(ConfigRow.MODULE_FIELD_NAME, moduleId)
                .and()
                .eq(ConfigRow.PARAMETER_FIELD_NAME, parameterName)
            .query();

        if (records.size() == 0) {
            return null;
        }
        else if (records.size() == 1) {
            return records.getFirst();
        }
        else {
            throw new RuntimeException("Failed unique constraint, detected multiple single entries");
        }
    }

}
