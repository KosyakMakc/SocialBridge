package io.github.kosyakmakc.socialBridge.ConfigurationService;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRow;
import io.github.kosyakmakc.socialBridge.DefaultModule;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.ISocialBridge;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ConfigurationService implements IConfigurationService {
    public static final String DATABASE_VERSION = "DATABASE_VERSION";

    private final ISocialBridge bridge;
    private final Logger logger;

    public ConfigurationService(ISocialBridge bridge) {
        this.bridge = bridge;
        this.logger = Logger.getLogger(bridge.getLogger().getName() + '.' + ConfigurationService.class.getSimpleName());
    }

    @Override
    public CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue, ITransaction transaction) {
        return transaction == null
            ? bridge.doTransaction(transaction2 -> getFromDatabase(moduleId, parameter, defaultValue, transaction2))
            : getFromDatabase(moduleId, parameter, defaultValue, transaction);
    }

    private CompletableFuture<String> getFromDatabase(UUID moduleId, String parameter, String defaultValue, ITransaction transaction) {
        return new CellConfiguration(moduleId, parameter, transaction).get().thenApply(value -> value != null ? value : defaultValue);
    }

    @Override
    public CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value, ITransaction transaction) {
        if (parameter.isBlank()) {
            throw new RuntimeException("Empty parameter name is not allowed");
        }

        return transaction == null
            ? bridge.doTransaction(transaction2 -> set(moduleId, parameter, value, transaction2))
            : setToDatabase(moduleId, parameter, value, transaction);
    }

    private CompletableFuture<Boolean> setToDatabase(UUID moduleId, String parameter, String value, ITransaction transaction) {
        if (parameter.isBlank()) {
            throw new RuntimeException("Empty parameter name is not allowed");
        }

        try {
            var databaseContext = transaction.getDatabaseContext();
            var records = databaseContext.getDaoTable(ConfigRow.class).queryBuilder()
                        .where()
                            .eq(ConfigRow.MODULE_FIELD_NAME, moduleId)
                            .and()
                            .eq(ConfigRow.PARAMETER_FIELD_NAME, parameter)
                        .query();
            if (records.size() > 0) {
                var record = records.getFirst();
                record.setValue(value);
                databaseContext.getDaoTable(ConfigRow.class).update(record);
            } else {
                var newRecord = new ConfigRow(moduleId, parameter, value);
                databaseContext.getDaoTable(ConfigRow.class).create(newRecord);
            }

            var module = bridge.getModule(moduleId);
            if (module == null) {
                logger.info("configuration change in module(id=" + moduleId + "): " + parameter + '=' + value);
            }
            else {
                logger.info("configuration change in module(" + module.getName() + "): " + parameter + '=' + value);
            }

            return CompletableFuture.completedFuture(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(false);
    }

    @Deprecated
    public CompletableFuture<Integer> getDatabaseVersion(ITransaction transaction) {
        return get(DefaultModule.MODULE_ID, DATABASE_VERSION, "", transaction)
               .thenApply(rawVersion -> {
                   try {
                       return Integer.parseInt(rawVersion);
                   }
                   catch (NumberFormatException err) {
                       return -1;
                   }
               });
    }
}
