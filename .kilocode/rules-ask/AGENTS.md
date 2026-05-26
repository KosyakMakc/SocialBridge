# Ask Mode Rules (Non-Obvious Only)

## Documentation Context

1. **Module System**: Modules implement `IModule` which combines four interfaces:
   - `ISocialModule` - provides social commands
   - `IMinecraftModule` - provides Minecraft commands
   - `ITranslationsModule` - provides localization sources
   - `IModuleDepend` - declares dependencies on other modules

2. **Command Types**:
   - **Minecraft Commands**: Use Brigadier (Paper), registered via `LifecycleEvents.COMMANDS`
   - **Social Commands**: Platform-agnostic, executed via social platform abstraction

3. **Database Tables**:
   - `ConfigRow` - stores module configurations (key-value per module)
   - `Localization` - stores translations (module + language + key)
   - Extension tables via `registerTable()` in `DatabaseContext`

4. **Version System**: 
   - `Version.isCompatible()` has special rules for 0.x versions (alpha)
   - For 0.x: minor=MAJOR, patch=MINOR
   - For 1.x+: standard semver rules

5. **Configuration Sources**:
   - Core: Database `ConfigRow` table
   - Paper: YAML config files (`config.yml`)

## Key Interfaces

- `ISocialBridge` - Main API entry point
- `IMinecraftPlatform` - Server abstraction (Paper, etc.)
- `ISocialPlatform` - Social platform abstraction (Telegram, Discord)
- `IModuleBase` - Base module interface
- `ITransaction` - Database transaction wrapper

## Important Records

- `MessageKey(UUID moduleId, String key)` - Unique localization key
- `Identifier(IdentifierType type, Object value)` - Social platform identifier
- `LocalizationRecord(String key, String localization)` - Translation entry
