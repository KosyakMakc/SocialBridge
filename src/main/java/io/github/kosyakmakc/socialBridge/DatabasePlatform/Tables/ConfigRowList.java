package io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables;

import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ConfigRowList.TABLE_NAME)
public class ConfigRowList implements IDatabaseTable {
    public static final String TABLE_NAME = "config_list";

    public static final String ID_FIELD_NAME = "id";
    public static final String MODULE_FIELD_NAME = "module_uuid";
    public static final String PARAMETER_FIELD_NAME = "parameter";
    public static final String INDEX_FIELD_NAME = "index";
    public static final String VALUE_FIELD_NAME = "value";

    public static final String FULL_KEY_INDEX_NAME = "full_key_idx";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int id;

    @DatabaseField(columnName = MODULE_FIELD_NAME, uniqueIndexName = FULL_KEY_INDEX_NAME)
    private UUID module;

    @DatabaseField(columnName = PARAMETER_FIELD_NAME, uniqueIndexName = FULL_KEY_INDEX_NAME)
    private String parameter;

    @DatabaseField(columnName = INDEX_FIELD_NAME, uniqueIndexName = FULL_KEY_INDEX_NAME)
    private int index;

    @DatabaseField(columnName = VALUE_FIELD_NAME)
    private String value;

    public ConfigRowList() {

    }

    public ConfigRowList(UUID module, String parameter, int index, String value) {
        this.module = module;
        this.parameter = parameter;
        this.index = index;
        this.value = value;
    }

    public UUID getModule() {
        return module;
    }

    public String getParameter() {
        return parameter;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
