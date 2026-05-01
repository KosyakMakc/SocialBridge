package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.ConfigurationService.IScopedConfigurationService;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DatabaseContext;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;

public interface ITransaction {
    DatabaseContext getDatabaseContext();
    IScopedConfigurationService getConfigurationService();
    /**
     * Event container, which handle commit\rollback transactions
     * <br>
     * true - commit, false - rollback
     * @return Event container
     */
    AsyncEvent<Boolean> getCloseEvent();
}
