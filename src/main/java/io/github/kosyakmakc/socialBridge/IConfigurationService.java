package io.github.kosyakmakc.socialBridge;

import java.util.concurrent.CompletableFuture;

public interface IConfigurationService {
    CompletableFuture<String> get(String parameter, String defaultValue);
    CompletableFuture<Boolean> set(String parameter, String value);
}
