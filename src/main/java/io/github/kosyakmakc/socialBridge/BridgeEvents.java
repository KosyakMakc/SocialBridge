package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialPlatform;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;

public class BridgeEvents {
    public final AsyncEvent<IBridgeModule> moduleJoin = new AsyncEvent<IBridgeModule>();
    public final AsyncEvent<IBridgeModule> moduleLeave = new AsyncEvent<IBridgeModule>();

    public final AsyncEvent<ISocialPlatform> socialPlatformJoin = new AsyncEvent<ISocialPlatform>();
    public final AsyncEvent<ISocialPlatform> socialPlatformLeave = new AsyncEvent<ISocialPlatform>();
}
