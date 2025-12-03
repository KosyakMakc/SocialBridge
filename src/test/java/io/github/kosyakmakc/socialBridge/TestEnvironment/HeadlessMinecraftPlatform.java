package io.github.kosyakmakc.socialBridge.TestEnvironment;

import io.github.kosyakmakc.socialBridge.IBridgeModule;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IMinecraftPlatform;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.Utils.Version;
import io.github.kosyakmakc.socialBridge.SocialBridge;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class HeadlessMinecraftPlatform implements IMinecraftPlatform {
    public static final Version VERSION = new Version("0.3.0");
    private LinkedBlockingQueue<IBridgeModule> registeredModules = new LinkedBlockingQueue<>();

    @Override
    public Path getDataDirectory() {
        return Path.of(System.getProperty("java.io.tmpdir"), "social-bridge", UUID.randomUUID().toString());
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
    public CompletableFuture<String> get(String parameter, String defaultValue) {
        if (Objects.equals(parameter, "connectionString")) {
            return CompletableFuture.completedFuture("jdbc:h2:mem:account");
            // return "jdbc:sqlite:social-bridge.sqlite";
        }
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public CompletableFuture<Boolean> set(String parameter, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    private static boolean isInited = false;
    public static void Init() throws SQLException, IOException {
        if (isInited) {
            return;
        }

        SocialBridge.Init(new HeadlessMinecraftPlatform());
        SocialBridge.INSTANCE.connectModule(new ArgumentsTestModule()).join();
        isInited = true;
    }

    @Override
    public Version getSocialBridgeVersion() {
        return VERSION;
    }

    @Override
    public CompletableFuture<Void> connectModule(IBridgeModule module) {
        registeredModules.add(module);
        return CompletableFuture.completedFuture(null);
    }
}
