package io.github.kosyakmakc.socialBridge.SocialPlatforms;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public interface ISocialMessage {
    Identifier getChannelId(); // how to mark private\public single\group chats?
    Identifier getId();

    ISocialPlatform getSocialPlatform();

    String getStringMessage();
    Collection<ISocialAttachment> getAttachments();

    CompletableFuture<SocialUser> getAuthor();

    CompletableFuture<Boolean> sendReply(String message, HashMap<String, String> placeholders);
    CompletableFuture<Boolean> sendReply(MessageKey messageKey, String locale, HashMap<String, String> placeholders, ITransaction transaction);
}
