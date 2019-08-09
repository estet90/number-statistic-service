package ru.kononov.numberstatisticservice.api;

import ru.kononov.numberstatisticservice.inmemorystorage.logic.ImMemoryNumberStorage;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        var storage = new ImMemoryNumberStorage();
        var server = new Server(storage);
        server.start();
    }

}
