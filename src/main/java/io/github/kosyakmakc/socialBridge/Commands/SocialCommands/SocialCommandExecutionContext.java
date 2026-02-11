package io.github.kosyakmakc.socialBridge.Commands.SocialCommands;

import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialMessage;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.ISocialPlatform;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;

public abstract class SocialCommandExecutionContext {
    private ISocialMessage message;

    public SocialCommandExecutionContext (ISocialMessage message) {
        this.message = message;
    }

    public ISocialMessage getSocialMessage() {
        return message;
    }
    

    public abstract String getFullMessage();

    public abstract String getMessage();

    public abstract ISocialPlatform getSocialPlatform();

    public CompletableFuture<SocialUser> getSender() {
        return message.getAuthor();
    }
}
