# Code Mode Rules (Non-Obvious Only)

## Critical Patterns

1. **Module State Guard**: All `addXxx()` methods in `SocialModule` call `throwIfConnected()` - you CANNOT modify modules after `connectModule()`. Plan all commands/translations before connecting.

2. **Transaction Pattern**: `doTransaction()` creates a `BridgeTransaction` and passes it to the consumer. Transaction is automatically committed on success or rolled back on exception:
   ```java
   bridge.doTransaction(transaction -> {
       var dbContext = transaction.getDatabaseContext();
       // ... database operations ...
       transaction.markSuccess(); // Required for commit
       return result;
   });
   ```
   - If `markSuccess()` is not called, transaction is rolled back
   - If exception is thrown, transaction is rolled back
   - `BridgeTransaction` implements `AutoCloseable` - use try-with-resources for cleanup

3. **Configuration Access Pattern**: Use scoped accessors instead of deprecated `IConfigurationService`:
   ```java
   // Database config (transaction-scoped)
   var cell = transaction.getConfigurationCell(moduleId, parameterName);
   var value = cell.read(); // returns null if not exists
   
   // Platform config (platform-scoped)
   var cell = platform.getConfigurationCell(moduleId, parameterName);
   var value = cell.read(); // returns null if not exists
   ```

4. **Three-State Configuration**: Configuration cells have three states:
   - Not existed: `read()` => null, `isEmpty()` => true
   - Null written: `read()` => null, `isEmpty()` => false
   - Value written: `read()` => value, `isEmpty()` => false

5. **AsyncEvent Thread Safety**: `AsyncEvent` uses `CopyOnWriteArraySet` - handlers are invoked asynchronously via `CompletableFuture.runAsync()`. Don't block handlers.

6. **Version Compatibility Quirk**: For 0.x versions, `minor` field is actually MAJOR and `patch` is MINOR. This is intentional for alpha versioning.

7. **Paper Command Registration**: Commands are registered via `LifecycleEvents.COMMANDS` using the external plugin's lifecycle manager, not the main plugin's.

## Import Conventions

- Use `var` for all local variables (Java 21)
- Records for data: `MessageKey`, `Identifier`, `LocalizationRecord`
- `CompletableFuture` for all async operations

## Common Mistakes

- Forgetting `transaction.markSuccess()` before closing `BridgeTransaction`
- Adding commands to module after `connectModule()` - will throw `RuntimeException`
- Using raw SQL instead of ORMLite DAOs - use `registerTable()` for extension tables
- Not handling `CompletableFuture` properly - always chain with `thenCompose`/`thenApply`
- Using deprecated `IConfigurationService` instead of new scoped accessors
