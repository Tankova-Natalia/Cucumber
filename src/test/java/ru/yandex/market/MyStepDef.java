package ru.yandex.market;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import pages.YandexMarket;
import pages.YandexSearch;

import java.util.List;

/**
 * Реализация шагов
 * @author Наталья Танькова
 */
public class MyStepDef extends Steps{
    /**
     * Переходит на сайт.
     * @author Наталья Танькова
     * @param url адрес сайта
     */
    @Given("перейти на сайт '(.*)'")
    public void перейти_на_сайт(String url) {
        открытьХром();
        chromeDriver.get(url);
    }

    /**
     * Закрывает браузер.
     * @author Наталья Танькова
     */
    @Then("закончить работу")
    public void закончитьРаботу() {
        закрытьХром();
    }

    /**
     * Открывает сервис.
     * @author Наталья Танькова
     * @param serviceName название сервиса
     */
    @Then("открыть '(.*)'")
    public void открытьСервис(String serviceName){
        YandexSearch searchPage = new YandexSearch(chromeDriver);
        searchPage.showAllServices();
        searchPage.openService(serviceName);
    }

    /**
     * Открывает каталог.
     * @author Наталья Танькова
     */
    @Then("открыть каталог")
    public void открытьКаталог(){
        YandexMarket market = new YandexMarket(chromeDriver,timeout);
        market.openCatalog();
    }

    /**
     * Наводит курсор на категорию.
     * @author Наталья Танькова
     * @param categoryName название категории
     */
    @Then("навести курсор на категорию '(.*)'")
    public void навестиКурсорНаКатегорию(String categoryName){
        YandexMarket market = new YandexMarket(chromeDriver,timeout);
        market.pointOnCategory(categoryName);
    }

    /**
     * Открывает подкатегорию.
     * @author Наталья Танькова
     * @param subcategoryName название подкатегории
     */
    @Then("открыть подкатегорию '(.*)'")
    public void открытьПодкатегорию(String subcategoryName){
        YandexMarket market = new YandexMarket(chromeDriver,timeout);
        market.openSubcategory(subcategoryName);
    }

    /**
     * Устанавливает фильтры.
     * @author Наталья Танькова
     * @param filterName название фильтра
     * @param values список значений
     */
    @Then("установить фильтры '(.*)'")
    public void установитьФильтры(String filterName, List<String> values){
        YandexMarket market = new YandexMarket(chromeDriver, timeout);
        market.setFilter(filterName, values);
    }

    /**
     * Ожидает загрузки результатов поиска.
     * @author Наталья Танькова
     */
    @Then("дождаться результатов поиска")
    public void дождатьсяРезультатовПоиска(){
        YandexMarket market = new YandexMarket(chromeDriver, timeout);
        market.waitForDataRefresh();
    }

    /**
     * Проверяет, что результаты содержат значения.
     * @author Наталья Танькова
     * @param values список значений
     */
    @Then("проверить что результаты содержат")
    public void проверитьЧтоРезультатыСодержат(List<String> values){
        YandexMarket market = new YandexMarket(chromeDriver, timeout);
        market.checkGoodsTitle(values);
    }
}
