package io.github.kosyakmakc.socialBridge.DatabasePlatform.Tables;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ConfigRow.TABLE_NAME)
public class MinecraftPlayerData implements IDatabaseTable {
    public static final String TABLE_NAME = "config";

    public static final String ID_FIELD_NAME = "id";
    public static final String SERVER_INSTANCE_ID_FIELD_NAME = "server_instance_id";
    public static final String PLAYER_ID_FIELD_NAME = "player_id";
    public static final String NAME_FIELD_NAME = "name";
    public static final String LOCALE_FIELD_NAME = "locale";
    public static final String CREATED_AT_FIELD_NAME = "created_at";
    public static final String LAST_SEEN_AT_FIELD_NAME = "last_seen_at";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private long id;
    @DatabaseField(columnName = SERVER_INSTANCE_ID_FIELD_NAME)
    private UUID serverInstanceId;
    @DatabaseField(columnName = PLAYER_ID_FIELD_NAME)
    private UUID playerId;
    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;
    @DatabaseField(columnName = LOCALE_FIELD_NAME)
    private String locale;
    @DatabaseField(columnName = CREATED_AT_FIELD_NAME)
    private Date createdAt;
    @DatabaseField(columnName = LAST_SEEN_AT_FIELD_NAME)
    private Date lastSeenAt;

    public MinecraftPlayerData() {

    }

    public MinecraftPlayerData(UUID serverInstanceId, UUID playerId, String name, String locale) {
        this.serverInstanceId = serverInstanceId;
        this.playerId = playerId;
        this.name = name;
        this.locale = locale;
        createdAt = Date.from(Instant.now());
    }

    public long getId() {
        return id;
    }

    public UUID getServerInstanceId() {
        return serverInstanceId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastSeenAt() {
        return lastSeenAt;
    }

    public void updateLastSeen() {
        lastSeenAt = Date.from(Instant.now());
    }
}
