package io.github.kosyakmakc.socialBridge.Modules;

import java.util.Collection;

import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.IMinecraftCommand;

public interface ISocialModuleWithMinecraftCommands {
    Collection<IMinecraftCommand> getMinecraftCommands();
}
