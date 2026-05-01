package io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ConfigRowMap.TABLE_NAME)
public class ConfigRowMap implements IDatabaseTable {
    public static final String TABLE_NAME = "config_map";

    public static final String ID_FIELD_NAME = "id";
    public static final String MODULE_FIELD_NAME = "module_uuid";
    public static final String PARAMETER_FIELD_NAME = "parameter";
    public static final String KEY_FIELD_NAME = "key";
    public static final String VALUE_FIELD_NAME = "value";

    public static final String FULL_KEY_INDEX_NAME = "full_key_idx";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int id;

    @DatabaseField(columnName = MODULE_FIELD_NAME, uniqueIndexName = FULL_KEY_INDEX_NAME)
    private UUID module;

    @DatabaseField(columnName = PARAMETER_FIELD_NAME, uniqueIndexName = FULL_KEY_INDEX_NAME)
    private String parameter;

    @DatabaseField(columnName = KEY_FIELD_NAME, uniqueIndexName = FULL_KEY_INDEX_NAME)
    private String key;

    @DatabaseField(columnName = VALUE_FIELD_NAME)
    private String value;

    public ConfigRowMap() {

    }

    public ConfigRowMap(UUID module, String parameter, String key, String value) {
        this.module = module;
        this.parameter = parameter;
        this.key = key;
        this.value = value;
    }

    public UUID getModule() {
        return module;
    }

    public String getParameter() {
        return parameter;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
