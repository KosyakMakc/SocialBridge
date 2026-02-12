package io.github.kosyakmakc.socialBridge.TestEnvironment;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.LocalizationService;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.Identifier;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.IdentifierType;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public class HeadlessSocialUser extends SocialUser {
    public static final HeadlessSocialUser Alex = new HeadlessSocialUser(HeadlessSocialPlatform.INSTANCE, "Alex");

    private static int HeadlessGlobalCounter = 1;
    private final Identifier id;
    private final String name;

    public HeadlessSocialUser(HeadlessSocialPlatform platform, String name) {
        super(platform);
        id = new Identifier(IdentifierType.Integer, HeadlessGlobalCounter);
        HeadlessGlobalCounter++;

        this.name = name;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLocale() {
        return LocalizationService.defaultLocale;
    }

    @Override
    public CompletableFuture<Boolean> sendMessage(String message, HashMap<String, String> placeholders) {
        // TO DO build template
        var params = placeholders.entrySet().stream().map(entry -> entry.getKey() + '=' + entry.getValue()).collect(Collectors.joining("; "));
        Logger.getGlobal().info("[social message to: " + getName() + "] " + message + "(" + params + ")");
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> sendMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders, ITransaction transaction) {
        return getPlatform()
            .getBridge()
            .getLocalizationService()
            .getMessage(locale, messageKey, transaction)
            .thenCompose(messageTemplate -> sendMessage(messageTemplate, placeholders));
    }

}
