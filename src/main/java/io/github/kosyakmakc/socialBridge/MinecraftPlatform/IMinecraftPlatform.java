package io.github.kosyakmakc.socialBridge.MinecraftPlatform;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.ConfigurationService.ICellConfiguration;
import io.github.kosyakmakc.socialBridge.ConfigurationService.IConfigurationService;
import io.github.kosyakmakc.socialBridge.Modules.IMinecraftModule;
import io.github.kosyakmakc.socialBridge.Modules.IModuleBase;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;
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

    AsyncEvent<MinecraftUser> getPlayerJoinEvent();
    AsyncEvent<MinecraftUser> getPlayerLeaveEvent();

    CompletableFuture<Boolean> sendBroadcaseMessage(String message, HashMap<String, String> placeholders);
    CompletableFuture<Boolean> sendBroadcaseMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders, ITransaction transaction);

    CompletableFuture<Void> connectModule(IMinecraftModule module);

    /** Get Cell contract for specified configuration parameter of module in config file <br/>
     * Be note that this accessor work without transaction model
     * @param moduleId Uuid of social module
     * @param parameter Name of Cell configuration
     * @return
     */
    ICellConfiguration getCell(UUID moduleId, String parameterName);

    // Usefull utils functions

    default ICellConfiguration getCell(IModuleBase module, String parameter) {
        return getCell(module.getId(), parameter);
    };
}
