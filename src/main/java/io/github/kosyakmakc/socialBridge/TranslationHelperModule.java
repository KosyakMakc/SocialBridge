package io.github.kosyakmakc.socialBridge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandBase;
import io.github.kosyakmakc.socialBridge.Commands.MinecraftCommands.MinecraftCommandExecutionContext;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.IMinecraftPlatform;
import io.github.kosyakmakc.socialBridge.Modules.ITranslationsModule;
import io.github.kosyakmakc.socialBridge.Modules.SocialModule;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;

public class TranslationHelperModule extends SocialModule {
    public static final UUID MODULE_ID = UUID.fromString("fe09e5e8-a50f-4fa4-95a9-9e28dfbf515f");
    public static final String MODULE_NAME = "translationHelper";

    public TranslationHelperModule(IMinecraftPlatform loader) {
        super(
            loader,
            loader.getSocialBridgeVersion(),
            loader.getSocialBridgeVersion(),
            MODULE_ID,
            MODULE_NAME
        );

        addMinecraftCommand(new MinecraftCommandBase("reset", MessageKey.EMPTY, "SocialBridge.TranslationHelper.reset") {
            @Override
            public void execute(MinecraftCommandExecutionContext context, List<Object> args) {
                getBridge().doTransaction(transaction -> {
                    var tasks = new LinkedList<CompletableFuture<Boolean>>();
                    var localizationService = getBridge().getLocalizationService();
                    var translationModules = getBridge()
                                                .getModules()
                                                .stream()
                                                .filter(x -> x instanceof ITranslationsModule)
                                                .map(x -> (ITranslationsModule) x);
                    var iterator = translationModules.iterator();

                    while (iterator.hasNext()) {
                        var module = iterator.next();
                        for (var translationSource : module.getTranslations()) {
                            for (var translation : translationSource.getRecords()) {
                                tasks.add(localizationService.setMessage(
                                    translationSource.getLanguage(),
                                    new MessageKey(module.getId(), translation.key()),
                                    translation.localization(),
                                    transaction));
                            }
                        }
                    }

                    return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                    .thenAccept(Void -> {
                        context.getSender().sendMessage("ok", new HashMap<>());
                    })
                    .exceptionally(err -> {
                        err.printStackTrace();
                        context.getSender().sendMessage("error", new HashMap<>());
                        return null;
                    });
                });
            }
        });
    }
}
