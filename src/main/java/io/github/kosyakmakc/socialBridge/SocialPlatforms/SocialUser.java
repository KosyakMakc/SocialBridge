package io.github.kosyakmakc.socialBridge.SocialPlatforms;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public abstract class SocialUser {
    private final ISocialPlatform platform;

    public SocialUser(ISocialPlatform platform) {
        this.platform = platform;
    }

    public ISocialPlatform getPlatform() {
        return platform;
    }

    public abstract Identifier getId();
    public abstract String getName();
    public abstract String getLocale();

    public abstract CompletableFuture<Boolean> sendMessage(String message, HashMap<String, String> placeholders);
    public abstract CompletableFuture<Boolean> sendMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders);

}
