package io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "config")
public class ConfigRow implements IDatabaseTable {
    @DatabaseField(columnName = "parameter", id = true)
    private String parameter;

    @DatabaseField(columnName = "value")
    private String value;

    public ConfigRow() {

    }

    public ConfigRow(String parameter, String value) {
        this.parameter = parameter;
        this.value = value;
    }

    public String getParameter() {
        return parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
