package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.DatabaseContext;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRowMap;

public class MapConfiguration implements IMapConfiguration {
    private final UUID moduleId;
    private final String parameterName;
    private final ITransaction transaction;

    private boolean sizeLoaded = false;
    private int sizeCache = -1;
    private boolean dataLoaded = false;
    private HashMap<String, String> dataCache = new HashMap<>();

    public MapConfiguration(UUID moduleId, String parameterName, ITransaction transaction) {
        if (parameterName.isBlank()) {
            throw new RuntimeException("Empty parameter name is not allowed");
        }

        if (transaction == null) {
            throw new RuntimeException("Transaction instance required, but got null value");
        }

        if (moduleId == new UUID(0, 0)) {
            throw new RuntimeException("moduleId required, but got empty value");
        }

        this.moduleId = moduleId;
        this.parameterName = parameterName;
        this.transaction = transaction;
    }

    @Override
    public CompletableFuture<String> get(String key) {
        if (dataCache.containsKey(key)) {
            return CompletableFuture.completedFuture(dataCache.get(key));
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var entry = findEntry(databaseContext, moduleId, parameterName, key);
                if (entry != null) {
                    var value = entry.getValue();

                    dataCache.put(key, value);

                    return value;
                }
                else {
                    return null;
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> put(String key, String value) {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var entry = findEntry(databaseContext, moduleId, parameterName, key);
                if (entry != null) {
                    throw new RuntimeException("Failed to add new entry to MapConfiguration, entry with this key(" + key + ") already exists");
                } else {
                    var newRecord = new ConfigRowMap(moduleId, parameterName, key, value);
                    databaseContext.getDaoTable(ConfigRowMap.class).create(newRecord);

                    dataCache.put(key, value);
                    if (sizeLoaded) {
                        sizeCache++;
                    }
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> set(String key, String value) {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var record = findEntry(databaseContext, moduleId, parameterName, key);
                if (record != null) {
                    record.setValue(value);
                    databaseContext.getDaoTable(ConfigRowMap.class).update(record);

                    dataCache.put(key, value);

                    return false;
                } else {
                    var newRecord = new ConfigRowMap(moduleId, parameterName, key, value);
                    databaseContext.getDaoTable(ConfigRowMap.class).create(newRecord);

                    dataCache.put(key, value);
                    if (sizeLoaded) {
                        sizeCache++;
                    }

                    return true;
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> remove(String key) {
        if (dataLoaded && !dataCache.containsKey(key)) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var query = databaseContext.getDaoTable(ConfigRowMap.class).deleteBuilder();
                query.where()
                    .eq(ConfigRowMap.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowMap.PARAMETER_FIELD_NAME, parameterName)
                    .and()
                    .eq(ConfigRowMap.KEY_FIELD_NAME, key);
                var isDeleted = query.delete() == 1;

                if (isDeleted) {
                    dataCache.remove(key);
                    if (sizeLoaded) {
                        sizeCache--;
                    }
                }

                return isDeleted;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Integer> clear() {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var query = databaseContext.getDaoTable(ConfigRowMap.class).deleteBuilder();
                query.where()
                    .eq(ConfigRowMap.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowMap.PARAMETER_FIELD_NAME, parameterName);

                dataCache.clear();
                dataLoaded = true;
                sizeCache = 0;
                sizeLoaded = true;

                return query.delete();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isContains(String key) {
        if (dataCache.containsKey(key)) {
            return CompletableFuture.completedFuture(true);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var records = databaseContext.getDaoTable(ConfigRowMap.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowMap.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowMap.PARAMETER_FIELD_NAME, parameterName)
                        .and()
                        .eq(ConfigRowMap.KEY_FIELD_NAME, key)
                    .countOf();

                if (records > 1) {
                    throw new RuntimeException("Failed unique constraint, detected multiple single entries");
                }

                return records == 1;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Integer> size() {
        if (sizeLoaded) {
            return CompletableFuture.completedFuture(sizeCache);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var records = databaseContext.getDaoTable(ConfigRowMap.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowMap.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowMap.PARAMETER_FIELD_NAME, parameterName)
                    .countOf();

                if (records > (long) Integer.MAX_VALUE) {
                    throw new RuntimeException("Maximum Map size exceeded, please store fewer entries");
                }

                sizeLoaded = true;
                sizeCache = (int) records;

                return sizeCache;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> iterate(Function<Entry<String, String>, CompletableFuture<Void>> entityHandler) {
        if (dataLoaded) {
            iterate(dataCache.entrySet().iterator(), entityHandler);
        }

        var databaseContext = transaction.getDatabaseContext();

        try {
            var iterator = databaseContext.getDaoTable(ConfigRowMap.class)
                .queryBuilder()
                .where()
                    .eq(ConfigRowMap.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowMap.PARAMETER_FIELD_NAME, parameterName)
                .iterator();

            var newData = new HashMap<String, String>();

            var stream = StreamSupport
                            .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                            .map(entity -> {
                                var entry = new AbstractMap.SimpleImmutableEntry<>(entity.getKey(), entity.getValue());
                                newData.put(entry.getKey(), entry.getValue());
                                return (Entry<String, String>) entry;
                            });

            return iterate(stream.iterator(), entityHandler)
                .whenComplete((a, err) -> {
                    try {
                        iterator.close();
                    }
                    catch(Exception ignored) {}

                    if (err == null) {
                        dataCache = newData;
                        dataLoaded = true;
                        sizeCache = newData.size();
                        sizeLoaded = true;
                    }
                });
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Void> iterate(Iterator<Entry<String, String>> iterator, Function<Entry<String, String>, CompletableFuture<Void>> entityHandler) {
        if (iterator.hasNext()) {
            var entry = iterator.next();
            return entityHandler
                .apply(entry)
                .thenCompose(Void -> iterate(iterator, entityHandler));
        }
        return CompletableFuture.completedFuture(null);
    }

    private static ConfigRowMap findEntry(DatabaseContext databaseContext, UUID moduleId, String parameterName, String key) throws SQLException {
        var records = databaseContext.getDaoTable(ConfigRowMap.class)
            .queryBuilder()
            .where()
                .eq(ConfigRowMap.MODULE_FIELD_NAME, moduleId)
                .and()
                .eq(ConfigRowMap.PARAMETER_FIELD_NAME, parameterName)
                .and()
                .eq(ConfigRowMap.KEY_FIELD_NAME, key)
            .query();

        if (records.size() == 0) {
            return null;
        }
        else if (records.size() == 1) {
            return records.getFirst();
        }
        else {
            throw new RuntimeException("Failed unique constraint, detected multiple single entries");
        }
    }

}
