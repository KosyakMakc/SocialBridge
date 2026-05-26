# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project Overview

SocialBridge is a Minecraft plugin framework that bridges Minecraft servers with social platforms (Telegram, Discord, etc.). It uses a modular architecture with platform abstraction layers.

**Stack:** Java 21, Gradle, ORMLite ORM, H2 (tests), JUnit 5

## Build & Test Commands

```bash
# Build core library
./gradlew build

# Build Paper implementation
./gradlew :implementations:paper:build

# Run tests (core)
./gradlew test

# Run Paper server for testing
./gradlew :implementations:paper:runFolia
```

## Architecture

### Core Components

- **`SocialBridge`** (`src/main/java/.../SocialBridge.java`) - Singleton orchestrator, manages platforms/modules
- **`ISocialBridge`** - Main API interface
- **`IMinecraftPlatform`** - Abstraction for Minecraft server (Paper, etc.)
- **`ISocialPlatform`** - Abstraction for social platforms (Telegram, Discord)
- **`IModuleBase`** / **`SocialModule`** - Base for all modules

### Configuration System (New)

The configuration system uses scoped accessors instead of a singleton service:

1. **Database Configuration** (`IConfigurationCellScoped`):
   - Created via `ITransaction.getConfigurationCell(moduleId, parameterName)`
   - Transaction-scoped: cached within transaction lifetime, guaranteed not dirty
   - Three-state: not existed, null written, value written
   - Methods: `read()`, `write(value)`, `isEmpty()`, `clear()`

2. **Platform Configuration** (`IConfigurationCell`):
   - Created via `IMinecraftPlatform.getConfigurationCell(moduleId, parameterName)`
   - Platform-scoped: cached within platform lifetime
   - Implementation is platform-specific (e.g., `PaperConfigurationCell` for YAML)
   - Methods: `read()`, `write(value)`, `isEmpty()`, `clear()`

3. **Backward Compatibility**:
   - `IConfigurationService` is deprecated but preserved
   - `IMinecraftPlatform` still extends `IConfigurationService`
   - Old code continues to work with deprecation warnings

### Key Patterns

1. **Module System**: Modules implement `IModule` (combines `ISocialModule`, `IMinecraftModule`, `ITranslationsModule`, `IModuleDepend`)
   - Commands registered via `addMinecraftCommand()` / `addSocialCommand()` BEFORE connecting
   - `throwIfConnected()` prevents modification after connection

2. **Transaction System**: `doTransaction()` wraps database operations with automatic commit/rollback
   - `BridgeTransaction` implements `AutoCloseable` - must use try-with-resources
   - `markSuccess()` required before close for commit
   - Provides `getConfigurationCell()` for database config access

3. **Localization**: `MessageKey` record uses `UUID moduleId + string key`
   - Default locale: `Locale.US.getLanguage()` ("en")
   - Fallback chain: requested locale → default locale → key name

4. **Version Compatibility** (`Version.isCompatible()`):
   - **0.x.x**: minor=MAJOR, patch=MINOR (special alpha rules)
   - **1.x.x**: major must match, minor must be ≥, patch must be ≥ if minor equal

### Command System

- **Minecraft Commands**: `MinecraftCommandBase` with Brigadier integration (Paper)
- **Social Commands**: `SocialCommandBase` for social platform commands
- **Arguments**: Use `CommandArgument.ofXxx()` factory methods (ofBoolean, ofInteger, etc.)
- **Naming**: Module names cannot contain spaces, dashes, dots. Command literals cannot contain spaces.

### Database

- ORMLite with `DaoManager`
- Tables: `ConfigRow`, `Localization`, plus extension tables via `registerTable()`
- Migrations: `ApplyDatabaseMigrations` runs sorted migrations from `Migrations/` directory

## Code Style

- Package: `io.github.kosyakmakc.socialBridge`
- UTF-8 encoding required
- Use `var` for local variables (Java 21)
- Records for simple data: `MessageKey`, `Identifier`, `LocalizationRecord`
- `CompletableFuture` for async operations

## Testing

- Tests use `HeadlessMinecraftPlatform` with H2 in-memory database
- `HeadlessMinecraftPlatform.Init()` required before tests (singleton pattern)
- `ModuleForTest` implements `AutoCloseable` for automatic cleanup
- Test command format: `/ModuleName_CommandName args`

## Important Gotchas

1. **SocialBridge is singleton** - `SocialBridge.Init()` throws if called twice
2. **Module modification** - Cannot add commands/translations after `connectModule()`
3. **Transaction closure** - Always use try-with-resources with `BridgeTransaction`
4. **Alpha versioning** - 0.x versions have inverted minor/patch semantics
5. **Configuration** - Use `IConfigurationCellScoped` (database) or `IConfigurationCell` (platform) instead of deprecated `IConfigurationService`
6. **Command registration** - Paper implementation uses `LifecycleEvents.COMMANDS` with external plugin's lifecycle manager
7. **Three-state config** - Configuration cells have three states: not existed (`read()`=null, `isEmpty()`=true), null written (`read()`=null, `isEmpty()`=false), value written (`read()`=value, `isEmpty()`=false)
