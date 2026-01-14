package io.github.kosyakmakc.socialBridge.DatabasePlatform;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface IDatabaseConsumer<T> {
    CompletableFuture<T> accept(IDatabaseTransaction transaction);
}
