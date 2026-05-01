package io.github.kosyakmakc.socialBridge.paper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.ConfigurationService.ICellConfiguration;

public class ConfigCellConfiguration implements ICellConfiguration {
    private final SocialBridgePaper socialBridgePaper;
    private final UUID moduleId;
    private final String parameterName;

    public ConfigCellConfiguration(SocialBridgePaper socialBridgePaper, UUID moduleId, String parameterName) {
        this.socialBridgePaper = socialBridgePaper;
        this.moduleId = moduleId;
        this.parameterName = parameterName;
    }

    @Override
    public CompletableFuture<String> get() {
        return CompletableFuture.supplyAsync(() -> {
            var config = socialBridgePaper.getConfig();

            var moduleSection = config.getConfigurationSection("module-" +  moduleId.toString());
            if (moduleSection == null) {
                return null;
            }

            return moduleSection.getString(parameterName);
        });
    }

    @Override
    public CompletableFuture<Boolean> set(String value) {
        return CompletableFuture.supplyAsync(() -> {
            var config = socialBridgePaper.getConfig();

            var moduleSection = config.getConfigurationSection("module-" + moduleId.toString());
            if (moduleSection == null) {
                moduleSection = config.createSection("module-" + moduleId.toString());
            }

            moduleSection.set(parameterName, value);
            socialBridgePaper.saveConfig();
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> isEmpty() {
        return CompletableFuture.supplyAsync(() -> {
            var config = socialBridgePaper.getConfig();

            var moduleSection = config.getConfigurationSection("module-" +  moduleId.toString());
            if (moduleSection == null) {
                return true;
            }

            return moduleSection.contains(parameterName);
        });
    }

    @Override
    public CompletableFuture<Boolean> clear() {
        return CompletableFuture.supplyAsync(() -> {
            var config = socialBridgePaper.getConfig();

            var moduleSection = config.getConfigurationSection("module-" +  moduleId.toString());
            if (moduleSection == null) {
                return false;
            }

            moduleSection.set(parameterName, null);
            return true;
        });
    }

}
