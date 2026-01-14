package io.github.kosyakmakc.socialBridge.DatabasePlatform;

public record DatabaseTransaction(DatabaseContext context) implements IDatabaseTransaction {
    @Override
    public DatabaseContext getDatabaseContext() {
        return context;
    }
}
