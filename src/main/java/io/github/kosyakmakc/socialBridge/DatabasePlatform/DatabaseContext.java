package io.github.kosyakmakc.socialBridge.DatabasePlatform;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRow;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.IDatabaseTable;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.Localization;
import io.github.kosyakmakc.socialBridge.ISocialBridge;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class DatabaseContext {
    public Dao<ConfigRow, String> configurations;
    public Dao<Localization, Integer> localizations;

    @SuppressWarnings("rawtypes")
    private final HashMap<Class, Dao> extensionTables = new HashMap<>();

    private final ISocialBridge bridge;
    private final ConnectionSource connectionSource;
    private final TransactionManager transactionManager;

    public DatabaseContext(ISocialBridge bridge, JdbcConnectionSource connectionSource) throws SQLException {
        this.bridge = bridge;
        var logger = bridge.getLogger();
        logger.info(connectionSource.getUrl());

        logger.info("DatabasePlatform starts...");
        this.connectionSource = connectionSource;
        this.transactionManager = new TransactionManager(connectionSource);

        configurations = DaoManager.createDao(connectionSource, ConfigRow.class);
        localizations = DaoManager.createDao(connectionSource, Localization.class);
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public void withTransaction(Callable<Void> action) throws SQLException {
        transactionManager.callInTransaction(action);
    }

    public <T extends IDatabaseTable, Key> Dao<T, Key> registerTable(Class<? extends IDatabaseTable> tableClass) {
        try {
            var dao = DaoManager.createDao(connectionSource, tableClass);
            extensionTables.put(tableClass, dao);
            return (Dao<T, Key>) dao;
        } catch (SQLException e) {
            bridge.getLogger().log(Level.SEVERE, "Failed register extension table", e);
            return null;
        }
    }

    public <T extends IDatabaseTable, Key> Dao<T, Key> getDaoTable(Class<T> tableClass) {
        var table = extensionTables.getOrDefault(tableClass, null);
        if (table != null) {
            return (Dao<T, Key>) table;
        }
        else {
            return null;
        }
    }
}
