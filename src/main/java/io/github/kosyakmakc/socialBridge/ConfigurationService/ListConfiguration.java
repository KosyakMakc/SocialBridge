package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import io.github.kosyakmakc.socialBridge.ITransaction;
import io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables.ConfigRowList;

public class ListConfiguration implements IListConfiguration {
    private final UUID moduleId;
    private final String parameterName;
    private final ITransaction transaction;

    private boolean sizeLoaded = false;
    private int sizeCache = -1;
    private boolean dataLoaded = false;
    private List<String> dataCache = new LinkedList<>();

    public ListConfiguration(UUID moduleId, String parameterName, ITransaction transaction) {
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
    public CompletableFuture<Integer> add(String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }

    @Override
    public CompletableFuture<Integer> insertAt(Integer index, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertAt'");
    }

    @Override
    public CompletableFuture<Boolean> updateAt(Integer index, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAt'");
    }

    @Override
    public CompletableFuture<Integer> removeAt(Integer index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAt'");
    }

    @Override
    public CompletableFuture<String> get(Integer index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public CompletableFuture<Integer> indexOf(String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'indexOf'");
    }

    @Override
    public CompletableFuture<Integer> clear() {
        return CompletableFuture.supplyAsync(() -> {
            var databaseContext = transaction.getDatabaseContext();

            try {
                var query = databaseContext.getDaoTable(ConfigRowList.class).deleteBuilder();
                query.where()
                    .eq(ConfigRowList.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowList.PARAMETER_FIELD_NAME, parameterName);

                var isDeleted = query.delete();

                dataCache.clear();
                dataLoaded = true;
                sizeCache = 0;
                sizeLoaded = true;

                return isDeleted;
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
                var records = databaseContext.getDaoTable(ConfigRowList.class)
                    .queryBuilder()
                    .where()
                        .eq(ConfigRowList.MODULE_FIELD_NAME, moduleId)
                        .and()
                        .eq(ConfigRowList.PARAMETER_FIELD_NAME, parameterName)
                    .countOf();

                if (records > (long) Integer.MAX_VALUE) {
                    throw new RuntimeException("Maximum List size exceeded, please store fewer entries");
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
    public CompletableFuture<Void> iterate(Function<Entry<Integer, String>, CompletableFuture<Void>> entityHandler) {
        if (dataLoaded) {
            var counter = new AtomicInteger();
            var iterator = dataCache
                .stream()
                .map(value -> (Entry<Integer, String>) new AbstractMap.SimpleImmutableEntry<>(counter.getAndIncrement(), value))
                .iterator();
            iterate(iterator, entityHandler);
        }

        var databaseContext = transaction.getDatabaseContext();

        try {
            var iterator = databaseContext.getDaoTable(ConfigRowList.class)
                .queryBuilder()
                .orderBy(ConfigRowList.INDEX_FIELD_NAME, true)
                .where()
                    .eq(ConfigRowList.MODULE_FIELD_NAME, moduleId)
                    .and()
                    .eq(ConfigRowList.PARAMETER_FIELD_NAME, parameterName)
                .iterator();

            var newData = new LinkedList<String>();

            var stream = StreamSupport
                            .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                            .map(entity -> {
                                var entry = new AbstractMap.SimpleImmutableEntry<>(entity.getIndex(), entity.getValue());
                                newData.add(entry.getValue());
                                return (Entry<Integer, String>) entry;
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

    private CompletableFuture<Void> iterate(Iterator<Entry<Integer, String>> iterator, Function<Entry<Integer, String>, CompletableFuture<Void>> entityHandler) {
        if (iterator.hasNext()) {
            var entry = iterator.next();
            return entityHandler
                .apply(entry)
                .thenCompose(Void -> iterate(iterator, entityHandler));
        }
        return CompletableFuture.completedFuture(null);
    }

}
