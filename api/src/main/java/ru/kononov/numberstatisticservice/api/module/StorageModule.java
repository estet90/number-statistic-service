package ru.kononov.numberstatisticservice.api.module;

import dagger.Module;
import dagger.Provides;
import ru.kononov.numberstatisticservice.inmemorystorage.logic.ImMemoryNumberStorage;
import ru.kononov.numberstatisticservice.storageapi.logic.NumberStorage;

import javax.inject.Singleton;

@Module
public class StorageModule {

    @Provides
    @Singleton
    public NumberStorage numberStorage() {
        return new ImMemoryNumberStorage();
    }

}
