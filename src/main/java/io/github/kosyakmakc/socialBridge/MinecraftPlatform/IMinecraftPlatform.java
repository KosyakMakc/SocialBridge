package io.github.kosyakmakc.socialBridge.MinecraftPlatform;

import io.github.kosyakmakc.socialBridge.IConfigurationService;
import io.github.kosyakmakc.socialBridge.Modules.IMinecraftModule;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public interface IMinecraftPlatform extends IConfigurationService, IModuleLoader {
    String getPlatformName();
    UUID getId();
    UUID getInstanceId();

    java.nio.file.Path getDataDirectory() throws IOException;
    Version getSocialBridgeVersion();

    Logger getLogger();

    CompletableFuture<MinecraftUser> tryGetUser(UUID minecraftId);
    CompletableFuture<MinecraftUser> tryGetUser(String playerName);
    CompletableFuture<List<MinecraftUser>> getOnlineUsers();

    CompletableFuture<Boolean> sendBroadcaseMessage(String message, HashMap<String, String> placeholders);
    CompletableFuture<Boolean> sendBroadcaseMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders);

    CompletableFuture<Void> connectModule(IMinecraftModule module);
}
