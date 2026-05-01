package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.ConfigurationService.IScopedConfigurationService;
import io.github.kosyakmakc.socialBridge.ConfigurationService.ScopedConfigurationService;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DatabaseContext;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;

public class BridgeTransaction implements ITransaction, AutoCloseable {
    private final AsyncEvent<Boolean> closeEvent = new AsyncEvent<>();
    private DatabaseContext databaseContext;
    private boolean isSuccess = false;

    public BridgeTransaction(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    @Override
    public DatabaseContext getDatabaseContext() {
        if (databaseContext == null) {
            throw new RuntimeException("This transaction is closed");
        }
        return databaseContext;
    }

    @Override
    public IScopedConfigurationService getConfigurationService() {
        return new ScopedConfigurationService(this);
    }

    public void markSuccess() {
        isSuccess = true;
    }

    @Override
    public void close() throws Exception {
        closeEvent.invoke(isSuccess).join();
        databaseContext = null;
    }

    @Override
    public AsyncEvent<Boolean> getCloseEvent() {
        return closeEvent;
    }
}
