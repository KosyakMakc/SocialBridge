package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.Modules.IModuleBase;

/**
 * Use scoped configuration service in ITransaction instance or IMinecraftPlatform.getCell()
 */
@Deprecated
public interface IConfigurationService {
    /**
     * Use getCell() api instead of this method
     */
    @Deprecated
    CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue, ITransaction transaction);

    /**
     * Use getCell() api instead of this method
     */
    @Deprecated
    CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value, ITransaction transaction);

    // Usefull utils functions

    /**
     * Use getCell() api instead of this method
     */
    @Deprecated
    default CompletableFuture<String> get(IModuleBase module, String parameter, String defaultValue, ITransaction transaction) {
        return get(module.getId(), parameter, defaultValue, transaction);
    };

    /**
     * Use getCell() api instead of this method
     */
    @Deprecated
    default CompletableFuture<Boolean> set(IModuleBase module, String parameter, String value, ITransaction transaction) {
        return set(module.getId(), parameter, value, transaction);
    };
}
