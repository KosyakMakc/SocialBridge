package io.github.kosyakmakc.socialBridge.TestEnvironment;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.ConfigurationService.ICellConfiguration;

public class HeadlessCellConfiguration implements ICellConfiguration {
    private final HashMap<String, String> config;
    private final String parameterName;

    public HeadlessCellConfiguration(HashMap<String, String> config, String parameterName) {
        this.config = config;
        this.parameterName = parameterName;
    }

    @Override
    public CompletableFuture<String> get() {
        return CompletableFuture.completedFuture(config.getOrDefault(parameterName, null));
    }

    @Override
    public CompletableFuture<Boolean> set(String value) {
        var oldValue = config.put(parameterName, value);
        return CompletableFuture.completedFuture(oldValue != null);
    }

    @Override
    public CompletableFuture<Boolean> isEmpty() {
        return CompletableFuture.completedFuture(config.containsKey(parameterName));
    }

    @Override
    public CompletableFuture<Boolean> clear() {
        var value = config.remove(parameterName);
        return CompletableFuture.completedFuture(value != null);
    }

}
