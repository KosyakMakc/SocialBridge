package io.github.kosyakmakc.socialBridge.Configurations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.kosyakmakc.socialBridge.DefaultModule;
import io.github.kosyakmakc.socialBridge.SocialBridge;
import io.github.kosyakmakc.socialBridge.TestEnvironment.HeadlessMinecraftPlatform;

public class ConfigurationCellTests {
    @ParameterizedTest
    @CsvSource({
        "__Test__Create1, 1",
        "__Test__Create2, a",
        "__Test__Create3, $",
    })
    void CheckCreates(String name, String value) throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        var bridge = SocialBridge.INSTANCE;
        bridge.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var cell = service.getCell(DefaultModule.MODULE_ID, name);
                
                cell.set(value).join();
                Assertions.assertEquals(value, cell.get().join());
                return null;
            });
        }).join();
    }

    @ParameterizedTest
    @CsvSource({
        "__Test__Change1, 1",
        "__Test__Change1, a",
        "__Test__Change2, $",
        "__Test__Change2, $",
    })
    void CheckChanges(String name, String value) throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        var bridge = SocialBridge.INSTANCE;
        bridge.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var cell = service.getCell(DefaultModule.MODULE_ID, name);
                
                cell.set(value).join();
                Assertions.assertEquals(value, cell.get().join());
                return null;
            });
        }).join();
    }

    @Test
    void CheckNotExisted() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var bridge = SocialBridge.INSTANCE;
        bridge.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var cell = service.getCell(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());

                Assertions.assertTrue(cell.isEmpty().join());
                return null;
            });
        }).join();
    }

    @Test
    void CheckGetOnEmptyCell() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var bridge = SocialBridge.INSTANCE;
        bridge.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var cell = service.getCell(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());
                
                Assertions.assertNull(cell.get().join());
                return null;
            });
        }).join();
    }

    @Test
    void CheckDropCell() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var bridge = SocialBridge.INSTANCE;
        bridge.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var cell = service.getCell(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());
                
                cell.set("asd").join();
                Assertions.assertFalse(cell.isEmpty().join());
                cell.clear().join();
                Assertions.assertTrue(cell.isEmpty().join());
                return null;
            });
        }).join();
    }

    @Test
    void CheckDropEmptyParameterName() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var bridge = SocialBridge.INSTANCE;
        Assertions.assertThrows(RuntimeException.class, () -> {
            bridge.doTransaction(transaction -> {
                return CompletableFuture.supplyAsync(() -> {
                    var service = transaction.getConfigurationService();
                    service.getCell(DefaultModule.MODULE_ID, "");
                    return null;
                });
            }).join();
        });
    }

}
