package io.github.kosyakmakc.socialBridge.MinecraftPlatform;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public abstract class MinecraftUser {
    public abstract UUID getId();
    public abstract String getName();
    public abstract String getLocale();
    public abstract CompletableFuture<Boolean> hasPermission(String permission);

    public abstract CompletableFuture<Boolean> sendMessage(String message, HashMap<String, String> placeholders);
    public abstract CompletableFuture<Boolean> sendMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders);
}
