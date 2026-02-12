package io.github.kosyakmakc.socialBridge.paper;

import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class BukkitMinecraftUser extends MinecraftUser {
    private final ISocialBridge socialBridge;
    private final Logger logger;
    private final Player player;

    public BukkitMinecraftUser(Player player, ISocialBridge socialBridge) {
        super();
        this.socialBridge = socialBridge;
        this.logger = Logger.getLogger(socialBridge.getLogger().getName() + '.' + BukkitMinecraftUser.class.getSimpleName());
        this.player = player;
    }

    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getId() {
        return player.getUniqueId();
    }

    public String getLocale() {
        return player.locale().getLanguage();
    }

    @Override
    public CompletableFuture<Boolean> hasPermission(String permission) {
        return CompletableFuture.completedFuture(player.hasPermission(permission));
    }

    @Override
    public CompletableFuture<Boolean> sendMessage(String message, HashMap<String, String> placeholders) {
        var builder = MiniMessage.builder()
                                 .tags(TagResolver.builder()
                                                  .resolver(StandardTags.defaults())
                                                  .build());

        for (var placeholderKey : placeholders.keySet()) {
            builder.editTags(x -> x.resolver(Placeholder.component(placeholderKey, Component.text(placeholders.get(placeholderKey)))));
        }

        var builtMessage = builder.build().deserialize(message);
        player.sendMessage(builtMessage);
        logger.info("message to '" + this.getName() + "' - " + MiniMessage.miniMessage().serialize(builtMessage));

        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> sendMessage(MessageKey messageKey, String locale, HashMap<String, String> placeholders, ITransaction transaction) {
        return socialBridge
            .getLocalizationService()
            .getMessage(locale, messageKey, transaction)
            .thenCompose(messageTemplate -> sendMessage(messageTemplate, placeholders));
    }
}
