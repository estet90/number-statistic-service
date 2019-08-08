package ru.kononov.numberstatisticservice.api;

import ru.kononov.numberstatisticservice.inmemorystorage.logic.ImMemoryNumberStorage;

public class Application {

    public static void main(String[] args) throws Exception {
        var storage = new ImMemoryNumberStorage();
        var server = new Server(storage);
        server.start();
    }

}
