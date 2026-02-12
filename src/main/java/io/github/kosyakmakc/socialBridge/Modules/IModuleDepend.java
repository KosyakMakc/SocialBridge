package io.github.kosyakmakc.socialBridge.Modules;

import java.util.UUID;
import java.util.List;
import java.util.Map.Entry;

import io.github.kosyakmakc.socialBridge.Utils.Version;

public interface IModuleDepend extends IModuleBase {
    List<Entry<UUID, Version>> getDependencies();
}
