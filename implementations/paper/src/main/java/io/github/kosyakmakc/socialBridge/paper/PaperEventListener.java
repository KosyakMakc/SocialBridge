package io.github.kosyakmakc.socialBridge.paper;

import java.util.concurrent.CompletableFuture;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.kosyakmakc.socialBridge.ISocialBridge;

public class PaperEventListener implements Listener {
    private final SocialBridgePaper minecraftPlatform;
    private final ISocialBridge bridge;

    public PaperEventListener(ISocialBridge bridge) {
        this.bridge = bridge;
        this.minecraftPlatform = (SocialBridgePaper) bridge.getMinecraftPlatform();

        minecraftPlatform.getServer().getPluginManager().registerEvents(this, minecraftPlatform);
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().updateCommands();

        CompletableFuture.runAsync(() -> {
            minecraftPlatform.getPlayerJoinEvent().invoke(new BukkitMinecraftUser(event.getPlayer(), bridge));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var playerUUID = event.getPlayer().getUniqueId();
        CompletableFuture.runAsync(() -> {
            minecraftPlatform
                .tryGetUser(playerUUID)
                .thenAccept(player -> minecraftPlatform.getPlayerLeaveEvent().invoke(player));
        });
    }
}
