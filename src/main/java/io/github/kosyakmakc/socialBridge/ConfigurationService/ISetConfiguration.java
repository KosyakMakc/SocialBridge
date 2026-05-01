package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Short-lived and parametrized accessor for Set-based persisted storage
 * Analog of basic Set interface, but fully asynchronous
 */
public interface ISetConfiguration {
    /**
     * Put new entry to storage
     * @param value
     * @return true if value has been added, false if value already existed in Set
     */
    public CompletableFuture<Boolean> put(String value);

    /**
     * Remove entry to storage
     * @param value
     * @return true if value has been removed, false if value not found in Set
     */
    public CompletableFuture<Boolean> remove(String value);

    /**
     * Clear all entries in this storage
     * @return Count of deleted entries
     */
    public CompletableFuture<Integer> clear();

    /**
     * Check is existed provided key in storage
     * @param key
     * @return
     */
    public CompletableFuture<Boolean> isContains(String value);

    /**
     * Count all entries in storage
     * @return
     */
    public CompletableFuture<Integer> size();

    /**
     * Iterate all entries in storage
     */
    public CompletableFuture<Void> iterate(Function<String, CompletableFuture<Void>> entityHandler);

    // Usefull utils functions

    default public CompletableFuture<Boolean> isEmpty() {
        return size().thenApply(size -> size == 0);
    }

    default public CompletableFuture<Void> iterate(Consumer<String> entityHandler) {
        return iterate(entry -> {
            entityHandler.accept(entry);
            return CompletableFuture.completedFuture(null);
        });
    }
}
