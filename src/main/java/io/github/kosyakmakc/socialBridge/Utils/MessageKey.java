package io.github.kosyakmakc.socialBridge.Utils;

import java.util.UUID;

import io.github.kosyakmakc.socialBridge.DefaultModule;

public record MessageKey(UUID moduleId, String key) {
    public static final MessageKey INTERNAL_SERVER_ERROR = new MessageKey(DefaultModule.MODULE_ID, "internal_server_error");
    public static final MessageKey INVALID_ARGUMENT = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument");
    public static final MessageKey INVALID_ARGUMENT_ARE_EMPTY = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_are_empty");
    public static final MessageKey INVALID_ARGUMENT_NOT_A_BOOLEAN = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_not_a_boolean");
    public static final MessageKey INVALID_ARGUMENT_NOT_A_INTEGER = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_not_a_integer");
    public static final MessageKey INVALID_ARGUMENT_NOT_A_LONG = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_not_a_long");
    public static final MessageKey INVALID_ARGUMENT_NOT_A_FLOAT = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_not_a_float");
    public static final MessageKey INVALID_ARGUMENT_NOT_A_DOUBLE = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_not_a_double");
    public static final MessageKey INVALID_ARGUMENT_MIN_MAX_ERROR = new MessageKey(DefaultModule.MODULE_ID, "invalid_argument_min_max");

    public static final MessageKey EMPTY = new MessageKey(DefaultModule.MODULE_ID, "empty");
}
