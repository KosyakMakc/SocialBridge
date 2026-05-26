# Architect Mode Rules (Non-Obvious Only)

## Architectural Constraints

1. **Singleton Pattern**: `SocialBridge` is a singleton - only one instance per JVM. `SocialBridge.Init()` throws if called twice.

2. **Module Lifecycle**: Modules must be fully configured BEFORE `connectModule()` is called. After connection, modification is prohibited via `throwIfConnected()`.

3. **Platform Abstraction**: 
   - `IMinecraftPlatform` abstracts Minecraft server (Paper, Folia, etc.)
   - `ISocialPlatform` abstracts social platforms (Telegram, Discord, etc.)
   - New platforms implement these interfaces

4. **Transaction Management**: All database operations must go through `doTransaction()` which handles commit/rollback automatically.

5. **AsyncEvent System**: Events use `CopyOnWriteArraySet` for thread-safe handler management. Handlers run asynchronously.

## Extension Points

- **New Social Platform**: Implement `ISocialPlatform`
- **New Module**: Extend `SocialModule` or implement `IModule`
- **New Database Table**: Use `DatabaseContext.registerTable()`
- **New Migration**: Create class in `Migrations/` implementing `IMigration`

## Key Design Decisions

- **Alpha Versioning**: 0.x versions use inverted minor/patch semantics for compatibility
- **Localization**: In-memory cache with database fallback, default locale is "en"
- **Command Registration**: Paper uses external plugin's lifecycle manager for commands
- **Configuration**: Core uses database, Paper uses YAML files

## Performance Considerations

- Localization uses `ConcurrentHashMap` for thread-safe caching
- `AsyncEvent` uses `CopyOnWriteArraySet` - good for read-heavy, write-rare scenarios
- Database operations use single-threaded executor in `DatabaseContext`
