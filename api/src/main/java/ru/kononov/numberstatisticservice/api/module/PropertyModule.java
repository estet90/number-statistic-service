package ru.kononov.numberstatisticservice.api.module;

import dagger.Module;
import dagger.Provides;
import ru.kononov.numberstatisticservice.api.util.PropertyResolver;

import javax.inject.Singleton;

@Module
public class PropertyModule {

    @Provides
    @Singleton
    PropertyResolver propertyResolver() {
        return new PropertyResolver("application.properties");
    }

}
