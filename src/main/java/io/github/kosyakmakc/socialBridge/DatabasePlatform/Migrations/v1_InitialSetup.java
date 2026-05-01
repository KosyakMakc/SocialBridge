package io.github.kosyakmakc.socialBridge.DatabasePlatform.Migrations;

import com.j256.ormlite.table.TableUtils;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRow;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.Localization;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class v1_InitialSetup implements IMigration {
    @Override
    public String getName() { return "InitialSetup"; }

    @Override
    public int getVersion() { return 1; }

    @Override
    public CompletableFuture<Void> accept(ITransaction transaction) {
        try {
            var databaseContext = transaction.getDatabaseContext();
            var connectionSource = databaseContext.getConnectionSource();

            TableUtils.createTableIfNotExists(connectionSource, ConfigRow.class);
            TableUtils.createTableIfNotExists(connectionSource, Localization.class);
        }
        catch (SQLException error) {
            throw new RuntimeException(error);
        }

        return CompletableFuture.completedFuture(null);
    }
}
