package io.github.kosyakmakc.socialBridge.paper;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.LocalizationService;
import io.github.kosyakmakc.socialBridge.MinecraftPlatform.MinecraftUser;

public class OfflineBukkitMinecraftUser extends MinecraftUser {
    private final OfflinePlayer player;
    
    private final String playerName;

    public OfflineBukkitMinecraftUser(OfflinePlayer player) {
        super();
        this.player = player;
        this.playerName = player.getPlayerProfile().update().join().getName();
    }

    public String getName() {
        return playerName;
    }

    @Override
    public UUID getId() {
        return player.getUniqueId();
    }

    public String getLocale() {
        return LocalizationService.defaultLocale;
    }

    @Override
    public boolean HasPermission(String permission) {
        return false;
    }

    @Override
    public void sendMessage(String message, HashMap<String, String> placeholders) {
        
    }
}
