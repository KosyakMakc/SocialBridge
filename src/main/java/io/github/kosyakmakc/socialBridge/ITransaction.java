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
     * Get a scoped configuration cell for the given module and parameter.
     * The cell is scoped to this transaction - cached values are guaranteed
     * not dirty within the transaction lifetime.
     * 
     * @param moduleId the module UUID
     * @param parameterName the parameter name
     * @return IConfigurationCellScoped instance
     */
    IConfigurationCellScoped getConfigurationCell(UUID moduleId, String parameterName);
}
