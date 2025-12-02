package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.ConfigurationService;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.IDatabaseConsumer;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.LocalizationService;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IMinecraftPlatform;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialPlatform;
import io.github.kosyakmakc.socialBridge.Utils.Version;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Logger;

public interface ISocialBridge {
    Logger getLogger();
    Version getVersion();

    LocalizationService getLocalizationService();
    ConfigurationService getConfigurationService();
    void queryDatabase(IDatabaseConsumer action) throws SQLException;

    boolean registerSocialPlatform(ISocialPlatform socialPlatform);
    Collection<ISocialPlatform> getSocialPlatforms();
    <T extends ISocialPlatform> T getSocialPlatform(Class<T> tClass);

    IMinecraftPlatform getMinecraftPlatform();

    boolean registerModule(IBridgeModule module);
    Collection<IBridgeModule> getModules();
    <T extends IBridgeModule> T getModule(Class<T> tClass);

    void Start();
}
