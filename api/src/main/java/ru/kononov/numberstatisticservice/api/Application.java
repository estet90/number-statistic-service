package ru.kononov.numberstatisticservice.api;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        DaggerApplicationComponent.builder().build().server().start();
    }

}
