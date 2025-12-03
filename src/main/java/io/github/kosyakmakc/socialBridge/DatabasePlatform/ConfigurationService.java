package io.github.kosyakmakc.socialBridge.DatabasePlatform;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRow;
import io.github.kosyakmakc.socialBridge.IConfigurationService;
import io.github.kosyakmakc.socialBridge.ISocialBridge;

import java.sql.SQLException;
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

    public CompletableFuture<String> get(String parameter, String defaultValue) {
        return bridge.queryDatabase(databaseContext -> {
            try {
                var record = databaseContext.configurations.queryForId(parameter);
                if (record != null) {
                    return record.getValue();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return defaultValue;
        });
    }

    public CompletableFuture<Boolean> set(String parameter, String value) {
        return bridge.queryDatabase(databaseContext -> {
            try {
                var record = databaseContext.configurations.queryForId(parameter);
                if (record != null) {
                    record.setValue(value);
                    databaseContext.configurations.update(record);
                } else {
                    var newRecord = new ConfigRow(parameter, value);
                    databaseContext.configurations.create(newRecord);
                }

                logger.info("database configuration change: " + parameter + "=" + value);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Integer> getDatabaseVersion() {
        return get(DATABASE_VERSION, "")
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
