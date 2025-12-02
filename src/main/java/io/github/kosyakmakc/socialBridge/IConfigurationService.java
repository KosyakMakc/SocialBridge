package io.github.kosyakmakc.socialBridge;

public interface IConfigurationService {
    String get(String parameter, String defaultValue);
    boolean set(String parameter, String value);
}
