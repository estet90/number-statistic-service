package ru.kononov.numberstatisticservice.api;

import dagger.Component;
import ru.kononov.numberstatisticservice.api.module.PropertyModule;
import ru.kononov.numberstatisticservice.api.module.StorageModule;
import ru.kononov.numberstatisticservice.api.server.Server;

import javax.inject.Singleton;

@Component(modules = {
        StorageModule.class,
        PropertyModule.class
})
@Singleton
public interface ApplicationComponent {

    Server server();

}
