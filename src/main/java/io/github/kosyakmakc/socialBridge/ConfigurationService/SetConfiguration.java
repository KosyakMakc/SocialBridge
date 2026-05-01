package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRowSet;

public class SetConfiguration implements ISetConfiguration {
    private final UUID moduleId;
    private final String parameterName;
    private final ITransaction transaction;

    private boolean sizeLoaded = false;
    private int sizeCache = -1;
    private boolean dataLoaded = false;
    private Set<String> dataCache = new HashSet<>();

    public SetConfiguration(UUID moduleId, String parameterName, ITransaction transaction) {
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
    public CompletableFuture<Boolean> put(String value) {
        if (dataCache.contains(value)) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var entry = databaseContext.getDaoTable(ConfigRowSet.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowSet.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowSet.PARAMETER_FIELD_NAME, parameterName)
                        .and()
                        .eq(ConfigRowSet.VALUE_FIELD_NAME, value)
                    .queryForFirst();

                if (entry == null) {
                    var newRecord = new ConfigRowSet(moduleId, parameterName, value);
                    databaseContext.getDaoTable(ConfigRowSet.class).create(newRecord);

                    dataCache.add(value);
                    if (sizeLoaded) {
                        sizeCache++;
                    }

                    return true;
                }
                else {
                    return false;
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> remove(String value) {
        if (dataLoaded && !dataCache.contains(value)) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var entry = databaseContext.getDaoTable(ConfigRowSet.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowSet.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowSet.PARAMETER_FIELD_NAME, parameterName)
                        .and()
                        .eq(ConfigRowSet.VALUE_FIELD_NAME, value)
                    .queryForFirst();

                if (entry != null) {
                    databaseContext.getDaoTable(ConfigRowSet.class).delete(entry);

                    dataCache.remove(value);
                    if (sizeLoaded) {
                        sizeCache--;
                    }

                    return true;
                }
                else {
                    return false;
                }
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
                var query = databaseContext.getDaoTable(ConfigRowSet.class).deleteBuilder();
                query.where()
                    .eq(ConfigRowSet.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowSet.PARAMETER_FIELD_NAME, parameterName);
                var deletedCount = query.delete();

                dataCache.clear();
                dataLoaded = true;
                sizeCache = 0;
                sizeLoaded = true;

                return deletedCount;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> isContains(String value) {
        if (dataCache.contains(value)) {
            return CompletableFuture.completedFuture(true);
        }

        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var records = databaseContext.getDaoTable(ConfigRowSet.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowSet.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowSet.PARAMETER_FIELD_NAME, parameterName)
                        .and()
                        .eq(ConfigRowSet.VALUE_FIELD_NAME, value)
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
                var records = databaseContext.getDaoTable(ConfigRowSet.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowSet.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowSet.PARAMETER_FIELD_NAME, parameterName)
                    .countOf();

                if (records > (long) Integer.MAX_VALUE) {
                    throw new RuntimeException("Maximum Set size exceeded, please store fewer entries");
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
    public CompletableFuture<Void> iterate(Function<String, CompletableFuture<Void>> entityHandler) {
        if (dataLoaded) {
            iterate(dataCache.iterator(), entityHandler);
        }

        var databaseContext = transaction.getDatabaseContext();

        try {
            var iterator = databaseContext.getDaoTable(ConfigRowSet.class)
                .queryBuilder()
                .where()
                    .eq(ConfigRowSet.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowSet.PARAMETER_FIELD_NAME, parameterName)
                .iterator();

                var newData = new HashSet<String>();

                var stream = StreamSupport
                                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                                .map(entity -> {
                                    var value = entity.getValue();
                                    newData.add(value);
                                    return value;
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

    private CompletableFuture<Void> iterate(Iterator<String> iterator, Function<String, CompletableFuture<Void>> entityHandler) {
        if (iterator.hasNext()) {
            var value = iterator.next();
            return entityHandler
                .apply(value)
                .thenCompose(Void -> iterate(iterator, entityHandler));
        }
        return CompletableFuture.completedFuture(null);
    }

}
