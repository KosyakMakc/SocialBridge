package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.Modules.IModuleBase;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @deprecated Use ITransaction.getConfigurationCell() for database config
 * or IMinecraftPlatform.getConfigurationCell() for platform config instead.
 */
@Deprecated
public interface IConfigurationService {
    /**
     * @deprecated Use transaction.getConfigurationCell(moduleId, parameterName).read() instead
     */
    @Deprecated
    CompletableFuture<String> get(IModuleBase module, String parameter, String defaultValue, ITransaction transaction);
    
    /**
     * @deprecated Use transaction.getConfigurationCell(moduleId, parameterName).write(value) instead
     */
    @Deprecated
    CompletableFuture<Boolean> set(IModuleBase module, String parameter, String value, ITransaction transaction);

    /**
     * @deprecated Use transaction.getConfigurationCell(moduleId, parameterName).read() instead
     */
    @Deprecated
    CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue, ITransaction transaction);
    
    /**
     * @deprecated Use transaction.getConfigurationCell(moduleId, parameterName).write(value) instead
     */
    @Deprecated
    CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value, ITransaction transaction);
}
