package io.github.kosyakmakc.socialBridge;

import java.util.concurrent.CompletableFuture;

/**
 * Singleton configuration accessor for platform-backed configuration (e.g., YAML).
 * Values are cached within the platform lifetime.
 * 
 * Implementation is platform-specific and provided by each Minecraft platform.
 * 
 * Three-state handling:
 * - Not existed: read() => null, isEmpty() => true
 * - Null written: read() => null, isEmpty() => false
 * - Value written: read() => value, isEmpty() => false
 */
public interface IConfigurationCell {
    /**
     * Read the configuration value.
     * @return CompletableFuture with value, or null if not exists or null value written
     */
    CompletableFuture<String> read();
    
    /**
     * Write the configuration value.
     * @param value the value to write (can be null to write null)
     * @return CompletableFuture with true if successful
     */
    CompletableFuture<Boolean> write(String value);
    
    /**
     * Check if the configuration cell is empty (not exists in storage).
     * @return CompletableFuture with true if not exists, false if exists (even with null value)
     */
    CompletableFuture<Boolean> isEmpty();
    
    /**
     * Clear the configuration value (delete from storage).
     * @return CompletableFuture with true if successful
     */
    CompletableFuture<Boolean> clear();
}
