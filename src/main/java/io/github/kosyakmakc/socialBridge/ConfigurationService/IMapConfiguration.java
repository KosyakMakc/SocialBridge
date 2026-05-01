package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Short-lived and parametrized accessor for Map-based persisted storage
 * Analog of basic Map interface, but fully asynchronous
 */
public interface IMapConfiguration {
    /**
     * Get value from storage by provided key
     * @param key
     * @return
     */
    public CompletableFuture<String> get(String key);

    /**
     * Put new entry to storage, if key existed exception will be thrown
     * @param key
     * @param value
     * @return
     */
    public CompletableFuture<Void> put(String key, String value);

    /**
     * Set entry to storage, if key existed it will be overwritten
     * @param key
     * @param value
     * @return true if entry is new, false if overwritten
     */
    public CompletableFuture<Boolean> set(String key, String value);

    /**
     * Clear entry with provided key in this storage
     * @param key
     * @return true if entry was removed, false if entry already empty
     */
    public CompletableFuture<Boolean> remove(String key);

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
    public CompletableFuture<Boolean> isContains(String key);

    /**
     * Count all entries in storage
     * @return
     */
    public CompletableFuture<Integer> size();

    /**
     * Iterate all entries in storage
     */
    public CompletableFuture<Void> iterate(Function<Entry<String, String>, CompletableFuture<Void>> entityHandler);

    // Usefull utils functions

    default public CompletableFuture<Void> put(Entry<String, String> entry) {
        return put(entry.getKey(), entry.getValue());
    }

    default public CompletableFuture<Boolean> set(Entry<String, String> entry) {
        return set(entry.getKey(), entry.getValue());
    }

    default public CompletableFuture<Boolean> isEmpty() {
        return size().thenApply(size -> size == 0);
    }

    default public CompletableFuture<Void> iterate(Consumer<Entry<String, String>> entityHandler) {
        return iterate(entry -> {
            entityHandler.accept(entry);
            return CompletableFuture.completedFuture(null);
        });
    }
}
