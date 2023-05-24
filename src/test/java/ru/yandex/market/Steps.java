package ru.yandex.market;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

/**
 * Шаги
 * @author Наталья Танькова
 */
public class Steps {
    WebDriver chromeDriver;
    protected int timeout = 10;

    /**
     * Открывает браузер.
     * @author Наталья Танькова
     */
    @Step("Открыть хром")
    public void открытьХром(){
        System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
        chromeDriver=new ChromeDriver();
        chromeDriver.manage().window().maximize();
        chromeDriver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }
    /**
     * Закрывает браузер.
     * @author Наталья Танькова
     */
    @Step("Закрыть хром")
    public void закрытьХром(){
        chromeDriver.quit();
    }
}
