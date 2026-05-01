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
import io.github.kosyakmakc.socialBridge.ConfigurationService.IMapConfiguration;
import io.github.kosyakmakc.socialBridge.TestEnvironment.HeadlessMinecraftPlatform;

public class ConfigurationMapTests {

    @Test
    void CheckIsEmpty() throws SQLException, IOException {
        wrap(map -> {
            Assertions.assertTrue(map.isEmpty().join());
        });
    }

    @Test
    void CheckSingleEntityWrite() throws SQLException, IOException {
        wrap(map -> {
            var origValue = "123";
            var singleKey = "test1";

            map.set(singleKey, origValue).join();
            Assertions.assertEquals(origValue, map.get(singleKey).join());
            Assertions.assertEquals(1, map.size().join());
        });
    }

    @Test
    void CheckSingleEntityIsContains() throws SQLException, IOException {
        wrap(map -> {
            var origValue = "123";
            var singleKey = "test1";

            Assertions.assertFalse(map.isContains(singleKey).join());
            map.set(singleKey, origValue).join();
            Assertions.assertTrue(map.isContains(singleKey).join());
            Assertions.assertEquals(1, map.size().join());
        });
    }

    @Test
    void CheckSingleEntityPut() throws SQLException, IOException {
        wrap(map -> {
            var origValue = "123";
            var singleKey = "test1";

            map.put(singleKey, origValue).join();
            Assertions.assertEquals(origValue, map.get(singleKey).join());
            Assertions.assertEquals(1, map.size().join());
            Assertions.assertThrows(RuntimeException.class, () -> {
                map.put(singleKey, origValue).join();
            });
        });
    }

    @Test
    void CheckMultiplyEntityWrite() throws SQLException, IOException {
        wrap(map -> {
            var keyPart = "test";
            var count = 5;

            for (var i = 1; i < count + 1; i++) {
                map.set(keyPart + i, Integer.toString(i)).join();
            }
            for (var i = 1; i < count + 1; i++) {
                Assertions.assertEquals(Integer.toString(i), map.get(keyPart + i).join());
            }
            Assertions.assertEquals(count, map.size().join());
        });
    }

    @Test
    void CheckSingleEntityRemove() throws SQLException, IOException {
        wrap(map -> {
            var origValue = "123";
            var singleKey = "test1";

            map.set(singleKey, origValue).join();

            var origValue2 = "456";
            var singleKey2 = "test2";

            map.set(singleKey2, origValue2).join();
            Assertions.assertEquals(origValue, map.get(singleKey).join());
            Assertions.assertEquals(2, map.size().join());

            map.remove(singleKey2).join();
            Assertions.assertEquals(origValue, map.get(singleKey).join());
            Assertions.assertEquals(1, map.size().join());
        });
    }

    @Test
    void CheckEntityOverwrite() throws SQLException, IOException {
        wrap(map -> {
            var value = "123";
            var value2 = "123";
            var singleKey = "test";

            map.set(singleKey, value).join();
            Assertions.assertEquals(value, map.get(singleKey).join());
            Assertions.assertEquals(1, map.size().join());

            map.set(singleKey, value2).join();
            Assertions.assertEquals(value2, map.get(singleKey).join());
            Assertions.assertEquals(1, map.size().join());
        });
    }

    @Test
    void CheckClear() throws SQLException, IOException {
        wrap(map -> {
            var keyPart = "test";
            var count = 5;

            for (var i = 1; i < count + 1; i++) {
                map.set(keyPart + i, Integer.toString(i)).join();
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
                map.set(keyPart + i, Integer.toString(i)).join();
            }

            var counter = new AtomicInteger();
            map.iterate(entry -> {
                counter.incrementAndGet();

                Assertions.assertEquals(Integer.toString(counter.get()), entry.getValue());
            }).join();
            Assertions.assertEquals(count, counter.get());

            counter.set(0);

            map.iterate(entry -> {
                return CompletableFuture.supplyAsync(() -> {
                    counter.incrementAndGet();
                    Assertions.assertEquals(Integer.toString(counter.get()), entry.getValue());
                    return null;
                });
            }).join();
            Assertions.assertEquals(count, counter.get());
        });
    }

    void wrap(Consumer<IMapConfiguration> action) throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            return CompletableFuture.supplyAsync(() -> {
                var service = transaction.getConfigurationService();
                var map = service.getMap(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());
                action.accept(map);
                return null;
            });
        }).join();
    }
}
