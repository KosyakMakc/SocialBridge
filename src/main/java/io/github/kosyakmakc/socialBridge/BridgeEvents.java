package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialPlatform;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;

public class BridgeEvents {
    public final AsyncEvent<ISocialModule> moduleConnect = new AsyncEvent<ISocialModule>();
    public final AsyncEvent<ISocialModule> moduleDisconnect = new AsyncEvent<ISocialModule>();

    public final AsyncEvent<ISocialPlatform> socialPlatformConnect = new AsyncEvent<ISocialPlatform>();
    public final AsyncEvent<ISocialPlatform> socialPlatformDisconnect = new AsyncEvent<ISocialPlatform>();
}
