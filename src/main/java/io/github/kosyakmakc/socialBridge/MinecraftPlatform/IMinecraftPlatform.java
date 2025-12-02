package io.github.kosyakmakc.socialBridge.MinecraftPlatform;

import io.github.kosyakmakc.socialBridge.IConfigurationService;
import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public interface IMinecraftPlatform extends IConfigurationService {
    void setAuthBridge(ISocialBridge authBridge);

    java.nio.file.Path getDataDirectory() throws IOException;
    Version getSocialBridgeVersion();

    Logger getLogger();

    MinecraftUser getUser(UUID minecraftId);
}
