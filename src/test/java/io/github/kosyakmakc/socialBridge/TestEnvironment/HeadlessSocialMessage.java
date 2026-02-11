package io.github.kosyakmakc.socialBridge.TestEnvironment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialAttachment;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialMessage;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialPlatform;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.Identifier;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.IdentifierType;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public class HeadlessSocialMessage implements ISocialMessage {
    private static int HeadlessGlobalCounter = 1;
    private static final Identifier GlobalChannel = new Identifier(IdentifierType.Integer, 1);
    private final Identifier messageId;
    private final String textMessage;
    private final SocialUser sender;

    public HeadlessSocialMessage(SocialUser sender, String textMessage) {
        messageId = new Identifier(IdentifierType.Integer, HeadlessGlobalCounter);
        HeadlessGlobalCounter++;

        this.sender = sender;
        this.textMessage = textMessage;
    }

    @Override
    public Identifier getChannelId() {
        return GlobalChannel;
    }

    @Override
    public Identifier getId() {
        return messageId;
    }

    @Override
    public CompletableFuture<SocialUser> getAuthor() {
        return CompletableFuture.completedFuture(sender);
    }

    @Override
    public String getStringMessage() {
        return textMessage;
    }

    @Override
    public Collection<ISocialAttachment> getAttachments() {
        return List.of();
    }

    @Override
    public ISocialPlatform getSocialPlatform() {
        return sender.getPlatform();
    }

    @Override
    public CompletableFuture<Boolean> sendReply(String message, HashMap<String, String> placeholders) {
        // TO DO build template
        Logger.getGlobal().info("[social reply to: " + getStringMessage() + " (" + sender.getName() + ")] " + message);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> sendReply(MessageKey messageKey, String locale, HashMap<String, String> placeholders) {
        return sender
            .getPlatform()
            .getBridge()
            .getLocalizationService()
            .getMessage(locale, messageKey, null)
            .thenCompose(messageTemplate -> sendReply(messageTemplate, placeholders));
    }
}
