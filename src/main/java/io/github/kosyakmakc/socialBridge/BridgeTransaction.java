package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.ConfigurationCellScoped;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DatabaseContext;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;

import java.util.Objects;
import java.util.UUID;

public class BridgeTransaction implements ITransaction, AutoCloseable {
    private final AsyncEvent<Boolean> closeEvent = new AsyncEvent<>();
    private DatabaseContext databaseContext;
    private boolean isSuccess = false;
    private boolean closed = false;

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

    public void markSuccess() {
        isSuccess = true;
    }

    @Override
    public void close() throws Exception {
        closeEvent.invoke(isSuccess).join();
        databaseContext = null;
        closed = true;
    }

    @Override
    public AsyncEvent<Boolean> getCloseEvent() {
        return closeEvent;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public IConfigurationCellScoped getConfigurationCell(UUID moduleId, String parameterName) {
        Objects.requireNonNull(moduleId, "moduleId must not be null");
        Objects.requireNonNull(parameterName, "parameterName must not be null");
        if (parameterName.isBlank()) {
            throw new IllegalArgumentException("parameterName must not be blank");
        }
        return new ConfigurationCellScoped(moduleId, parameterName, this);
    }
}
