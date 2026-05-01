package io.github.kosyakmakc.socialBridge.TestEnvironment;

import io.github.kosyakmakc.socialBridge.DefaultModule;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IMinecraftPlatform;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.Modules.IMinecraftModule;
import io.github.kosyakmakc.socialBridge.Utils.AsyncEvent;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;
import io.github.kosyakmakc.socialBridge.Utils.Version;
import io.github.kosyakmakc.socialBridge.SocialBridge;
import io.github.kosyakmakc.socialBridge.ConfigurationService.ICellConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class HeadlessMinecraftPlatform implements IMinecraftPlatform {
    public static final String PLATFORM_NAME = "headless";
    public static final UUID PLATFORM_ID = UUID.fromString("c936579c-da7e-47dd-be85-d93f0558fab1");

    private final AsyncEvent<MinecraftUser> playerJoinEvent = new AsyncEvent<>();
    private final AsyncEvent<MinecraftUser> playerLeaveEvent = new AsyncEvent<>();

    public static final Version VERSION = new Version("0.10.3");
    private LinkedBlockingQueue<IMinecraftModule> registeredModules = new LinkedBlockingQueue<>();
    private HashMap<UUID, HashMap<String, String>> config = new HashMap<>();
    private final UUID instanceId = UUID.randomUUID();

    @Override
    public String getPlatformName() {
        return PLATFORM_NAME;
    }

    @Override
    public UUID getId() {
        return PLATFORM_ID;
    }

    @Override
    public UUID getInstanceId() {
        return instanceId;
    }

    @Override
    public Path getDataDirectory() {
        return Path.of(System.getProperty("java.io.tmpdir"), "SocialBridge", UUID.randomUUID().toString());
    }

    @Override
    public Logger getLogger() {
        return Logger.getGlobal();
    }

    @Override
    public CompletableFuture<MinecraftUser> tryGetUser(UUID minecraftId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<MinecraftUser> tryGetUser(String playerName) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> get(UUID moduleId, String parameter, String defaultValue, ITransaction transaction) {
        var moduleConfig = config.getOrDefault(moduleId, null);
        if (moduleConfig == null) {
            moduleConfig = new HashMap<>();
            config.put(moduleId, moduleConfig);
        }

        var result = moduleConfig.getOrDefault(parameter, defaultValue);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Boolean> set(UUID moduleId, String parameter, String value, ITransaction transaction) {
        var moduleConfig = config.getOrDefault(moduleId, null);
        if (moduleConfig == null) {
            moduleConfig = new HashMap<>();
            config.put(moduleId, moduleConfig);
        }

        moduleConfig.put(parameter, value);
        return CompletableFuture.completedFuture(true);
    }

    private static boolean isInited = false;
    private static final Lock R_LOCK = new ReentrantLock();
    public static void Init() throws SQLException, IOException {
        R_LOCK.lock();
        if (isInited) {
            return;
        }

        var mcPlatform = new HeadlessMinecraftPlatform();
        mcPlatform.set(DefaultModule.MODULE_ID, "connectionString", "jdbc:h2:mem:account", null);

        SocialBridge.Init(mcPlatform);
        isInited = true;
        R_LOCK.unlock();
    }

    @Override
    public Version getSocialBridgeVersion() {
        return VERSION;
    }

    @Override
    public CompletableFuture<Void> connectModule(IMinecraftModule module) {
        registeredModules.add(module);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<MinecraftUser>> getOnlineUsers() {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Boolean> sendBroadcaseMessage(String message, HashMap<String, String> placeholders) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendBroadcaseMessage'");
    }

    @Override
    public CompletableFuture<Boolean> sendBroadcaseMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders, ITransaction transaction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendBroadcaseMessage'");
    }

    @Override
    public AsyncEvent<MinecraftUser> getPlayerJoinEvent() {
        return playerJoinEvent;
    }

    @Override
    public AsyncEvent<MinecraftUser> getPlayerLeaveEvent() {
        return playerLeaveEvent;
    }

    @Override
    public ICellConfiguration getCell(UUID moduleId, String parameterName) {
        var moduleConfig = config.getOrDefault(moduleId, null);
        if (moduleConfig == null) {
            moduleConfig = new HashMap<>();
            config.put(moduleId, moduleConfig);
        }

        return new HeadlessCellConfiguration(config.get(moduleId), parameterName);
    }
}
