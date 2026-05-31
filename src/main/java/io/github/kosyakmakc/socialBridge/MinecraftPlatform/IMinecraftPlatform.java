package io.github.kosyakmakc.socialBridge.MinecraftPlatform;

import io.github.kosyakmakc.socialBridge.IConfigurationCell;
import io.github.kosyakmakc.socialBridge.IConfigurationService;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.Modules.IMinecraftModule;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Minecraft platform abstraction.
 * 
 * NOTE: Still extends IConfigurationService for backward compatibility.
 * New code should use getConfigurationCell() instead.
 */
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

    AsyncEvent<MinecraftUser> getPlayerJoinEvent();
    AsyncEvent<MinecraftUser> getPlayerLeaveEvent();

    CompletableFuture<Boolean> sendBroadcaseMessage(String message, HashMap<String, String> placeholders);
    CompletableFuture<Boolean> sendBroadcaseMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders, ITransaction transaction);

    CompletableFuture<Void> connectModule(IMinecraftModule module);
    
    /**
     * Get a configuration cell for the given module and parameter.
     * The cell is backed by platform storage (e.g., YAML) and cached.
     * 
     * @param moduleId the module UUID
     * @param parameterName the parameter name
     * @return IConfigurationCell instance
     */
    IConfigurationCell getConfigurationCell(UUID moduleId, String parameterName);
}
