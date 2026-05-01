package io.github.kosyakmakc.socialBridge.ConfigurationService;

import java.util.UUID;

import io.github.kosyakmakc.socialBridge.Modules.IModuleBase;

/**
 * Transaction scoped configuration service
 */
public interface IScopedConfigurationService {

    /** get short-lived and databased Cell contract for specified parameter of module
     * @param moduleId Uuid of social module
     * @param parameter Name of Cell configuration
     * @return
     */
    ICellConfiguration getCell(UUID moduleId, String parameter);

    /** get short-lived and databased Map contract for specified parameter of module
     * @param moduleId Uuid of social module
     * @param parameter Name of Map configuration
     * @return
     */
    IMapConfiguration getMap(UUID moduleId, String parameter);

    /** get short-lived and databased Set contract for specified parameter of module
     * @param moduleId Uuid of social module
     * @param parameter Name of Set configuration
     * @return
     */
    ISetConfiguration getSet(UUID moduleId, String parameter);

    /**
     * get short-lived and databased List contract for specified parameter of module
     * @param moduleIdUuid of social module
     * @param parameter Name of List configuration
     * @return
     */
    IListConfiguration getList(UUID moduleId, String parameter);

    // Usefull utils functions

    default ICellConfiguration getCell(IModuleBase module, String parameter) {
        return getCell(module.getId(), parameter);
    };

    default IMapConfiguration getMap(IModuleBase module, String parameter) {
        return getMap(module.getId(), parameter);
    };

    default ISetConfiguration getSet(IModuleBase module, String parameter) {
        return getSet(module.getId(), parameter);
    };
}
