package io.github.kosyakmakc.socialBridge.Configurations;

import io.github.kosyakmakc.socialBridge.DefaultModule;
import io.github.kosyakmakc.socialBridge.SocialBridge;
import io.github.kosyakmakc.socialBridge.TestEnvironment.HeadlessMinecraftPlatform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationsTests {
    @ParameterizedTest
    @CsvSource({
        "__Test__Create1, 1",
        "__Test__Create2, a",
        "__Test__Create3, $",
    })
    void CheckCreates(String name, String value) throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, name);
            return cell.write(value);
        }).join();
        
        var result = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, name);
            return cell.read();
        }).join();
        
        Assertions.assertEquals(value, result);
    }
    
    @ParameterizedTest
    @CsvSource({
        "__Test__Change1, 1",
        "__Test__Change1, a",
        "__Test__Change2, $",
        "__Test__Change2, $",
    })
    void CheckChanges(String name, String value) throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, name);
            return cell.write(value);
        }).join();
        
        var result = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, name);
            return cell.read();
        }).join();
        
        Assertions.assertEquals(value, result);
    }
    
    @Test
    void CheckNotExisted() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        
        var result = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());
            return cell.read();
        }).join();
        
        Assertions.assertNull(result);
    }
    
    @Test
    void CheckIsEmpty() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        
        var result = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, "__Test__" + UUID.randomUUID().toString());
            return cell.isEmpty();
        }).join();
        
        Assertions.assertTrue(result);
    }
    
    @Test
    void CheckWriteNull() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        String paramName = "__Test__Null_" + UUID.randomUUID().toString();
        
        // Write null value
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, paramName);
            return cell.write(null);
        }).join();
        
        // Check isEmpty returns false (cell exists with null value)
        var isEmpty = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, paramName);
            return cell.isEmpty();
        }).join();
        
        Assertions.assertFalse(isEmpty);
        
        // Check read returns null
        var result = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, paramName);
            return cell.read();
        }).join();
        
        Assertions.assertNull(result);
    }
    
    @Test
    void CheckClear() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();
        String paramName = "__Test__Clear_" + UUID.randomUUID().toString();
        
        // Write value
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, paramName);
            return cell.write("test");
        }).join();
        
        // Clear value
        SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, paramName);
            return cell.clear();
        }).join();
        
        // Check isEmpty returns true
        var isEmpty = SocialBridge.INSTANCE.doTransaction(transaction -> {
            var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, paramName);
            return cell.isEmpty();
        }).join();
        
        Assertions.assertTrue(isEmpty);
    }

    @Test
    void CheckNullModuleId() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var exception = assertThrows(CompletionException.class, () -> {
            SocialBridge.INSTANCE.doTransaction(transaction -> {
                var cell = transaction.getConfigurationCell(null, "test");
                return cell.read();
            }).join();
        });

        // Unwrap CompletionException -> SQLException -> NullPointerException
        Throwable cause = exception.getCause();
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        Assertions.assertInstanceOf(NullPointerException.class, cause);
        Assertions.assertEquals("moduleId must not be null", cause.getMessage());
    }

    @Test
    void CheckNullParameterName() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var exception = assertThrows(CompletionException.class, () -> {
            SocialBridge.INSTANCE.doTransaction(transaction -> {
                var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, null);
                return cell.read();
            }).join();
        });

        // Unwrap CompletionException -> SQLException -> NullPointerException
        Throwable cause = exception.getCause();
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        Assertions.assertInstanceOf(NullPointerException.class, cause);
        Assertions.assertEquals("parameterName must not be null", cause.getMessage());
    }

    @Test
    void CheckEmptyParameterName() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var exception = assertThrows(CompletionException.class, () -> {
            SocialBridge.INSTANCE.doTransaction(transaction -> {
                var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, "");
                return cell.read();
            }).join();
        });

        // Unwrap CompletionException -> SQLException -> IllegalArgumentException
        Throwable cause = exception.getCause();
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        Assertions.assertInstanceOf(IllegalArgumentException.class, cause);
        Assertions.assertEquals("parameterName must not be blank", cause.getMessage());
    }

    @Test
    void CheckBlankParameterName() throws SQLException, IOException {
        HeadlessMinecraftPlatform.Init();

        var exception = assertThrows(CompletionException.class, () -> {
            SocialBridge.INSTANCE.doTransaction(transaction -> {
                var cell = transaction.getConfigurationCell(DefaultModule.MODULE_ID, "   ");
                return cell.read();
            }).join();
        });

        // Unwrap CompletionException -> SQLException -> IllegalArgumentException
        Throwable cause = exception.getCause();
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        Assertions.assertInstanceOf(IllegalArgumentException.class, cause);
        Assertions.assertEquals("parameterName must not be blank", cause.getMessage());
    }
}
