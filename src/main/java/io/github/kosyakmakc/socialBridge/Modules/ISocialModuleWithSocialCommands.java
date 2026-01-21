package io.github.kosyakmakc.socialBridge.Modules;

import java.util.Collection;

import io.github.kosyakmakc.socialBridge.Commands.SocialCommands.ISocialCommand;

public interface ISocialModuleWithSocialCommands {
    Collection<ISocialCommand> getSocialCommands();
}
