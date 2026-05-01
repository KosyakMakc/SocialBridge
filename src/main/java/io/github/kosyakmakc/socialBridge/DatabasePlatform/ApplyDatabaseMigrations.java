package io.github.kosyakmakc.socialBridge.DatabasePlatform;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.Migrations.IMigration;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Migrations.v1_InitialSetup;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Migrations.v2_ExpandConfigurationStorage;
import io.github.kosyakmakc.socialBridge.DefaultModule;
import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.ConfigurationService.ConfigurationService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ApplyDatabaseMigrations implements Consumer<ISocialBridge> {
    /**
     * list of migration for database, MUST be sorted from MIN to MAX versions
     */
    public final IMigration[] migrations = new IMigration[] {
            new v1_InitialSetup(),
            new v2_ExpandConfigurationStorage()
    };

    @Override
    public void accept(ISocialBridge bridge) {
        var logger = bridge.getLogger();
        var databaseVersionReference = new AtomicInteger();
        
        bridge.doTransaction(transaction -> {
            var accessor = transaction.getConfigurationService().getCell(DefaultModule.MODULE_ID, ConfigurationService.DATABASE_VERSION);
            return accessor
                .get()
                .thenAccept(value -> {
                    try {
                        databaseVersionReference.set(Integer.parseInt(value));
                    }
                    catch (NumberFormatException err) {
                        databaseVersionReference.set(-1);
                    }
            });
        }).join();

        var databaseVersion = databaseVersionReference.get();
        var latestDatabaseVersion = Arrays.stream(migrations).max(Comparator.comparingInt(IMigration::getVersion)).get().getVersion();
        
        if (databaseVersion < latestDatabaseVersion) {
            logger.info("current database version is outdated, version: " + databaseVersion);
            for (var migration : migrations) {
                if (migration.getVersion() > databaseVersion) {
                    logger.info("applying migration \"" + migration.getName() + "\" (version: " + migration.getVersion() + ").");
                    bridge.doTransaction(transaction -> {
                        migration.accept(transaction).join();

                        var accessor = transaction.getConfigurationService().getCell(DefaultModule.MODULE_ID, ConfigurationService.DATABASE_VERSION);
                        accessor.set(Integer.toString(migration.getVersion())).join();
                        return CompletableFuture.completedFuture(null);
                    }).join();
                }
            }
            logger.info("current database updated.");
        }
        else if (databaseVersion == latestDatabaseVersion) {
            logger.info("current database version is actual.");
        }
        else {
            logger.warning("Current database version is higher than bundled in plugin.");
            logger.warning("You can continue to use it at your own risk.");
        }
    }
}
