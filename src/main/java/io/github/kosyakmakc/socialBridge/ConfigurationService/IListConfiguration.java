package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IListConfiguration {

    /**
     * Add new entry to end of list in storage
     * @param value
     * @return Index of added entry
     */
    public CompletableFuture<Integer> add(String value);

    /**
     * Insert new entry to specified index position and move all entries after index to +1 position
     * @param value
     * @return Count of updated entries (1 + moved entries)
     */
    public CompletableFuture<Integer> insertAt(Integer index, String value);

    /**
     * Update existed entry at specified index position and change his value
     * @param index position in list
     * @param value new value
     * @return
     */
    public CompletableFuture<Boolean> updateAt(Integer index, String value);

    /**
     * Remove entry at specified index in storage and move all entries after index position to -1 position
     * @param index position in list
     * @return Count of updated entries (1 + moved entries)
     */
    public CompletableFuture<Integer> removeAt(Integer index);

    /**
     * Get entry at specified index
     * @param index position in list
     * @return value in storage
     */
    public CompletableFuture<String> get(Integer index);

    /**
     * Search entry in storage and get index of first match
     * @param value value to search
     * @return index of first match or -1 if not found
     */
    public CompletableFuture<Integer> indexOf(String value);

    /**
     * Clear all entries in this storage
     * @return Count of deleted entries
     */
    public CompletableFuture<Integer> clear();

    /**
     * Count all entries in storage
     * @return
     */
    public CompletableFuture<Integer> size();

    /**
     * Iterate all entries in storage
     */
    public CompletableFuture<Void> iterate(Function<Entry<Integer, String>, CompletableFuture<Void>> entityHandler);

    // Usefull utils functions

    default public CompletableFuture<Boolean> Contains(String value) {
        return indexOf(value).thenApply(index -> index != -1);
    }

    default public CompletableFuture<Boolean> isEmpty() {
        return size().thenApply(size -> size == 0);
    }

    default public CompletableFuture<Void> iterate(Consumer<Entry<Integer, String>> entityHandler) {
        return iterate(entry -> {
            entityHandler.accept(entry);
            return CompletableFuture.completedFuture(null);
        });
    }
}
