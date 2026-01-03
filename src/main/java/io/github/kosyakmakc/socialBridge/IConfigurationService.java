package io.github.kosyakmakc.socialBridge;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IConfigurationService {
    CompletableFuture<String> get(ISocialModule module, String parameter, String defaultValue);
    CompletableFuture<Boolean> set(ISocialModule module, String parameter, String value);

    CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue);
    CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value);
}
