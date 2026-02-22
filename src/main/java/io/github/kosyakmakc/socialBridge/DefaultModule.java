package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.English;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.Russian;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IMinecraftPlatform;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.Modules.SocialModule;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class DefaultModule extends SocialModule {
    public static final UUID MODULE_ID = UUID.fromString("dcab3770-b24e-44bb-b9a9-19edf96b9986");
    public static final String MODULE_NAME = "socialbridge";

    public DefaultModule(IMinecraftPlatform loader) {
        super(
            loader,
            loader.getSocialBridgeVersion(),
            loader.getSocialBridgeVersion(),
            MODULE_ID,
            MODULE_NAME
        );

        addTranslationSource(new English());
        addTranslationSource(new Russian());
    }

    @Override
    public CompletableFuture<Boolean> enable(ISocialBridge bridge) {
        var result = super.enable(bridge);
        bridge.getMinecraftPlatform().getPlayerJoinEvent().addHandler(new Consumer<MinecraftUser>() {

            @Override
            public void accept(MinecraftUser t) {
                Logger.getGlobal().info("CONNECTED " + t.getName());
            }
            
        });
        bridge.getMinecraftPlatform().getPlayerLeaveEvent().addHandler(new Consumer<MinecraftUser>() {

            @Override
            public void accept(MinecraftUser t) {
                Logger.getGlobal().info("LEAVE " + t.getName());
            }
            
        });
        return result;
    }
}
