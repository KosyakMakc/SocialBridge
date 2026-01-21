package io.github.kosyakmakc.socialBridge.Modules;

import java.util.Collection;

import io.github.kosyakmakc.socialBridge.DatabasePlatform.DefaultTranslations.ITranslationSource;

public interface ISocialModuleWithTranslations {
    Collection<ITranslationSource> getTranslations();
}
