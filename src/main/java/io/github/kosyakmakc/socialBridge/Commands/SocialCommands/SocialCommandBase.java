package io.github.kosyakmakc.socialBridge.Commands.SocialCommands;

import io.github.kosyakmakc.socialBridge.Commands.Arguments.ArgumentFormatException;
import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.SocialPlatforms.SocialUser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class SocialCommandBase implements ISocialCommand {
    private final String commandName;
    @SuppressWarnings("rawtypes")
    private final List<CommandArgument> argumentDefinition;
    private ISocialBridge authBridge;

    public SocialCommandBase(String commandName) {
        this(commandName, new ArrayList<>());
    }

    @SuppressWarnings("rawtypes")
    public SocialCommandBase(String commandName, List<CommandArgument> argumentDefinition) {
        this.commandName = commandName;
        this.argumentDefinition = Collections.unmodifiableList(argumentDefinition);
    }

    @Override
    public String getLiteral() {
        return commandName;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<CommandArgument> getArgumentDefinitions() {
        return argumentDefinition;
    }

    @Override
    public void init(ISocialBridge authBridge) {
        this.authBridge = authBridge;
    }

    public abstract void execute(SocialUser sender, List<Object> args);

    @Override
    public void handle(SocialUser sender, StringReader argsReader) throws ArgumentFormatException {
        if (authBridge == null) {
            getBridge().getLogger().info(this.getClass().getName() + " - initialization failed, skip handling");
            return;
        }

        var arguments = new LinkedList<>();

        for (var argument : getArgumentDefinitions()) {
            var valueItem = argument.getValue(argsReader);
            arguments.add(valueItem);
        }

        execute(sender, arguments);
    }

    protected ISocialBridge getBridge() {
        return authBridge;
    }
}
