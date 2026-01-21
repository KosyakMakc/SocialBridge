package io.github.kosyakmakc.socialBridge.Commands;

import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.Modules.ISocialModuleBase;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICommand {
    CompletableFuture<Void> enable(ISocialModuleBase bridge);
    CompletableFuture<Void> disable();

    @SuppressWarnings("rawtypes")
    List<CommandArgument> getArgumentDefinitions();

    String getLiteral();
    MessageKey getDescription();
}
