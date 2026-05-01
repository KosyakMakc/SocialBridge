package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.UUID;

import io.github.kosyakmakc.socialBridge.ITransaction;

public class ScopedConfigurationService implements IScopedConfigurationService {
    private final ITransaction transaction;

    public ScopedConfigurationService(ITransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public ICellConfiguration getCell(UUID moduleId, String parameter) {
        return new CellConfiguration(moduleId, parameter, transaction);
    }

    @Override
    public IMapConfiguration getMap(UUID moduleId, String parameter) {
        return new MapConfiguration(moduleId, parameter, transaction);
    }

    @Override
    public ISetConfiguration getSet(UUID moduleId, String parameter) {
        return new SetConfiguration(moduleId, parameter, transaction);
    }

    @Override
    public IListConfiguration getList(UUID moduleId, String parameter) {
        return new ListConfiguration(moduleId, parameter, transaction);
    }

}
