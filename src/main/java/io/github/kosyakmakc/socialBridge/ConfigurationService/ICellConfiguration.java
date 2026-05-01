package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.concurrent.CompletableFuture;

/**
 * Short-lived and parametrized accessor for single cell persisted storage
 */
public interface ICellConfiguration {
    /**
     * Get value from storage
     * @return value from storage or null if Empty
     */
    CompletableFuture<String> get();

    /**
     * Set value to storage, if value existed it will be overwritten
     * @param value data to save in storage
     * @return true if value is new, false if overwritten
     */
    CompletableFuture<Boolean> set(String value);

    /**
     * Check is storage is Empty
     * @return true if storage is empty, false if storage have any value (include Null value)
     */
    CompletableFuture<Boolean> isEmpty();

    /**
     * Clear storage and make it empty
     * @return true if any items was been removed, false if storage already is empty
     */
    CompletableFuture<Boolean> clear();
}
