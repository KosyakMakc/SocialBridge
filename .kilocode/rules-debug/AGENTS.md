# Debug Mode Rules (Non-Obvious Only)

## Critical Debugging Patterns

1. **Transaction Rollback**: If `markSuccess()` is not called on `BridgeTransaction`, the transaction is rolled back on close. Check for missing `markSuccess()` calls.

2. **Module Connection State**: `throwIfConnected()` prevents modification after `connectModule()`. If you get this error, you need to disconnect the module first, modify it, then reconnect.

3. **AsyncEvent Handlers**: Handlers run asynchronously via `CompletableFuture.runAsync()`. Exceptions in handlers are caught and printed - they won't propagate to the caller.

4. **Localization Fallback Chain**: When debugging missing translations, check: requested locale → default locale ("en") → key name. The cache is in-memory only and lost on restart.

5. **Paper Command Registration**: Commands are registered via `LifecycleEvents.COMMANDS` using the external plugin's lifecycle manager. If commands don't appear, check that the module's loader is a `JavaPlugin`.

## Common Error Messages

- `"Please fill this module before connecting to SocialBridge"` - Module was modified after connection
- `"Social bridge MUST BE single instance"` - `SocialBridge.Init()` called twice
- `"This transaction is closed"` - Using `BridgeTransaction` after close
- `"Duplication module name detected"` - Two modules with same name

## Debugging Tips

- Use `HeadlessMinecraftPlatform` for unit testing - it uses H2 in-memory database
- Check `BridgeTransaction.isSuccess` field to verify if transaction was marked for commit
- Module validation happens in `ValidateAndThrowModule()` - check patterns for naming rules
