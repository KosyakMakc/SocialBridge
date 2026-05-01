package io.github.kosyakmakc.socialBridge.Configurations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.kosyakmakc.socialBridge.DefaultModule;
import io.github.kosyakmakc.socialBridge.SocialBridge;
import io.github.kosyakmakc.socialBridge.ConfigurationService.ISetConfiguration;
import io.github.kosyakmakc.socialBridge.TestEnvironment.HeadlessMinecraftPlatform;

public class ConfigurationSetTests {

    @Test
    void CheckIsEmpty() throws SQLException, IOException {
        wrap(map -> {
            Assertions.assertTrue(map.isEmpty().join());
        });
    }

    @Test
    void CheckSingleEntityWrite() throws SQLException, IOException {
        wrap(set -> {
            var origValue = "123";

            var isAdded = set.put(origValue).join();
            var isAdded2 = set.put(origValue).join();
            Assertions.assertEquals(1, set.size().join());
            Assertions.assertTrue(isAdded);
            Assertions.assertFalse(isAdded2);
        });
    }

    @Test
    void CheckSingleEntityIsContains() throws SQLException, IOException {
        wrap(map -> {
            var origValue = "123";

            Assertions.assertFalse(map.isContains(origValue).join());
            map.put(origValue).join();
            Assertions.assertTrue(map.isContains(origValue).join());
            Assertions.assertEquals(1, map.size().join());
        });
    }

    @Test
    void CheckMultiplyEntityWrite() throws SQLException, IOException {
        wrap(map -> {
            var keyPart = "test";
            var count = 5;

            for (var i = 1; i < count + 1; i++) {
                map.put(keyPart + i).join();
            }
            Assertions.assertEquals(count, map.size().join());
        });
    }

    @Test
    void CheckSingleEntityRemove() throws SQLException, IOException {
        wrap(map -> {
            var origValue = "123";

            map.put(origValue).join();

            var origValue2 = "456";

            map.put(origValue2).join();
            Assertions.assertEquals(2, map.size().join());

            var isRemoved = map.remove(origValue2).join();
            Assertions.assertEquals(1, map.size().join());
            Assertions.assertTrue(isRemoved);

            var isRemoved2 = map.remove(origValue2).join();
            var isRemoved3 = map.remove(origValue).join();
            Assertions.assertEquals(0, map.size().join());
            Assertions.assertFalse(isRemoved2);
            Assertions.assertTrue(isRemoved3);
        });
    }

    @Test
    void CheckClear() throws SQLException, IOException {
        wrap(map -> {
            var keyPart = "test";
            var count = 5;

            for (var i = 1; i < count + 1; i++) {
                map.put(keyPart + i).join();
            }
            map.clear().join();
            Assertions.assertEquals(0, map.size().join());
        });
    }

    @Test
    void CheckIterate() throws SQLException, IOException {
        wrap(map -> {
            var keyPart = "test";
            var count = 5;

            for (var i = 1; i < count + 1; i++) {
                map.put(keyPart + i).join();
            }

            var counter = new AtomicInteger();
            map.iterate(entry -> {
                counter.incrementAndGet();

                Assertions.assertEquals(keyPart + counter.get(), entry);
            }).join();
            Assertions.assertEquals(count, counter.get());

            counter.set(0);

            map.iterate(entry -> {
                return CompletableFuture.supplyAsync(() -> {
                    counter.incrementAndGet();
                    Assertions.assertEquals(keyPart + counter.get(), entry);
                    return null;
                });
            }).join();
            Assertions.assertEquals(count, counter.get());
        });
    }

    void wrap(Consumer<ISetConfiguration> action) throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var map = service.getSet(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());
                action.accept(map);
                return null;
            });
        }).join();
    }
}
