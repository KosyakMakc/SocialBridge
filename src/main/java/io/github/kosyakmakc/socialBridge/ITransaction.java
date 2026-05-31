package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.DatabaseContext;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;

import java.util.UUID;

public interface ITransaction {
    DatabaseContext getDatabaseContext();
    /**
     * Event container, which handle commit\rollback transactions
     * <br>
     * true - commit, false - rollback
     * @return Event container
     */
    AsyncEvent<Boolean> getCloseEvent();
    
    /**
     * Check if the transaction is closed.
     * After closure, no operations can be performed on this transaction
     * or any configuration cells created from it.
     * 
     * @return true if the transaction is closed
     */
    boolean isClosed();
    
    /**
     * Get a scoped configuration cell for the given module and parameter.
     * The cell is scoped to this transaction - cached values are guaranteed
     * not dirty within the transaction lifetime.
     * 
     * @param moduleId the module UUID
     * @param parameterName the parameter name
     * @return IConfigurationCellScoped instance
     * @throws IllegalStateException if the transaction is closed
     */
    IConfigurationCellScoped getConfigurationCell(UUID moduleId, String parameterName);
}
