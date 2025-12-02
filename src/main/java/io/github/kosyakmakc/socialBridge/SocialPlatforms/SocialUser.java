package io.github.kosyakmakc.socialBridge.SocialPlatforms;

import java.util.HashMap;

public abstract class SocialUser {
    private final ISocialPlatform platform;

    public SocialUser(ISocialPlatform platform) {
        this.platform = platform;
    }

    public ISocialPlatform getPlatform() {
        return platform;
    }

    public abstract String getName();

    public abstract void sendMessage(String message, HashMap<String, String> placeholders);

    public abstract String getLocale();

    public abstract SocialUserIdType getIdType();
    public abstract Object getId();
}
