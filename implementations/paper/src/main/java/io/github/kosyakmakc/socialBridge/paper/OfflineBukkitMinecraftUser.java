package io.github.kosyakmakc.socialBridge.paper;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.destroystokyo.paper.profile.PlayerProfile;

import io.github.kosyakmakc.socialBridge.ISocialBridge;
import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.LocalizationService;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;
import io.github.kosyakmakc.socialBridge.Utils.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.luckperms.api.LuckPerms;

public class OfflineBukkitMinecraftUser extends MinecraftUser {
    private final ISocialBridge socialBridge;
    private final Logger logger;

    private final UUID playerId;
    private final String playerName;

    public OfflineBukkitMinecraftUser(PlayerProfile player, ISocialBridge socialBridge) {
        super();
        this.socialBridge = socialBridge;
        this.logger = Logger.getLogger(socialBridge.getLogger().getName() + '.' + BukkitMinecraftUser.class.getSimpleName());

        this.playerId = player.getId();
        this.playerName = player.getName();
    }

    public String getName() {
        return playerName;
    }

    @Override
    public UUID getId() {
        return playerId;
    }

    public String getLocale() {
        return LocalizationService.defaultLocale;
    }

    @Override
    public CompletableFuture<Boolean> hasPermission(String permission) {
        var provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            return provider
                .getProvider()
                .getUserManager()
                .loadUser(playerId)
                .thenApply(lpUser -> lpUser.getCachedData().getPermissionData().checkPermission(permission).asBoolean()); 
        }

        return CompletableFuture.completedFuture(false);
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
