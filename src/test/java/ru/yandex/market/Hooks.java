package ru.yandex.market;

import cucumber.api.java.Before;

import java.io.File;
import java.util.Arrays;

/**
 * Методы, запускаемые до или после сценария
 * @author Наталья Танькова
 */
public class Hooks {
    /**
     * Очищает папку с allure отчетами allure-results.
     * @author Наталья Танькова
     */
    @Before
    public void clearAllureResults(){
        File dir = new File("allure-results");
        File[] files = dir.listFiles();
        Arrays.stream(files).forEach(x -> x.delete());
    }

}
