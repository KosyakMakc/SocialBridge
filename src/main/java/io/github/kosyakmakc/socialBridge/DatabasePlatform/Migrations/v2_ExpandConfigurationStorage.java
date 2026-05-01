package io.github.kosyakmakc.socialBridge.DatabasePlatform.Migrations;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import com.j256.ormlite.table.TableUtils;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRowList;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRowMap;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRowSet;

public class v2_ExpandConfigurationStorage implements IMigration {
    @Override
    public String getName() { return "ExpandConfigurationStorage"; }

    @Override
    public int getVersion() { return 2; }

    @Override
    public CompletableFuture<Void> accept(ITransaction transaction) {
        try {
            var databaseContext = transaction.getDatabaseContext();
            var connectionSource = databaseContext.getConnectionSource();

            TableUtils.createTableIfNotExists(connectionSource, ConfigRowMap.class);
            TableUtils.createTableIfNotExists(connectionSource, ConfigRowList.class);
            TableUtils.createTableIfNotExists(connectionSource, ConfigRowSet.class);
        }
        catch (SQLException error) {
            throw new RuntimeException(error);
        }

        return CompletableFuture.completedFuture(null);
    }
}
