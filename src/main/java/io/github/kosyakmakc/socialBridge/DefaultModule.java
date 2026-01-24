package io.github.kosyakmakc.socialBridge;

import io.github.kosyakmakc.socialBridge.Commands.Arguments.CommandArgument;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.English;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.Russian;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IMinecraftPlatform;
import io.github.kosyakmakc.socialBridge.Modules.SocialModule;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DefaultModule extends SocialModule {
    public static final UUID MODULE_ID = UUID.fromString("dcab3770-b24e-44bb-b9a9-19edf96b9986");
    public static final String MODULE_NAME = "socialbridge";

    public DefaultModule(IMinecraftPlatform loader) {
        super(loader, loader.getSocialBridgeVersion(), MODULE_ID, MODULE_NAME);
        addTranslationSource(new English());
        addTranslationSource(new Russian());

        addMinecraftCommand(new MinecraftCommandBase(
            "word",
            MessageKey.EMPTY,
            "perm",
            List.of(CommandArgument.ofWord("food", new String[] { "apple", "banana "}))) {

                @Override
                public void execute(MinecraftCommandExecutionContext context, List<Object> args) {
                    context.getSender().sendMessage("aaa", new HashMap<>());
                }
                
            });
    }
}
