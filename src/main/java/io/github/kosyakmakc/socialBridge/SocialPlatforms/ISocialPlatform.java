package io.github.kosyakmakc.socialBridge.SocialPlatforms;

import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.util.HashMap;

public interface ISocialPlatform {
    void setAuthBridge(ISocialBridge authBridge);
    String getPlatformName();
    Version getCompabilityVersion();

    void sendMessage(SocialUser telegramUser, String message, HashMap<String, String> placeholders);

    void Start();
}
