package io.github.kosyakmakc.socialBridge;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.IDatabaseTransaction;

public interface IConfigurationService {
    CompletableFuture<String> get(ISocialModule module, String parameter, String defaultValue);
    CompletableFuture<String> get(ISocialModule module, String parameter, String defaultValue, IDatabaseTransaction transaction);
    CompletableFuture<Boolean> set(ISocialModule module, String parameter, String value);
    CompletableFuture<Boolean> set(ISocialModule module, String parameter, String value, IDatabaseTransaction transaction);

    CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue);
    CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue, IDatabaseTransaction transaction);
    CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value);
    CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value, IDatabaseTransaction transaction);
}
