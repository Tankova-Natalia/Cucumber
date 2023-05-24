package pages;

import helper.CustomWait;
import helper.Range;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * PageObject для страницы Яндекс Маркета
 * @author Наталья Танькова
 */
public class YandexMarket {
    /**
     * Драйвер
     * @author Наталья Танькова
     */
    private WebDriver driver;
    /**
     * Время для явного ожидания в секундах
     * @author Наталья Танькова
     */
    private long timeout;
    /**
     * Максимальное время выполнения цикла в минутах
     * @author Наталья Танькова
     */
    private final long maxWaitInMinutes = 5;
    /**
     * Максимальное время выполнения цикла в секундах
     * @author Наталья Танькова
     */
    private final long maxWaitInSec = maxWaitInMinutes * 60;
    /**
     * Максимальное время выполнения цикла в миллисекундах
     * @author Наталья Танькова
     */
    private final long maxWaitInMilliSec = maxWaitInSec * 1000;
    /**
     * Селектор для кнопки, которая открывает каталог
     * @author Наталья Танькова
     */
    private String catalogButtonSelector = "//*[@id='catalogPopupButton']";
    /**
     * Селектор для элементов из результата поиска
     * @author Наталья Танькова
     */
    private String listSelector = "//*[@data-zone-name='snippet-card']";
    /**
     * Селектор для названия элементов из результатов поиска
     * @author Наталья Танькова
     */
    private String titleSelector = ".//*[@data-zone-name='title']";
    /**
     * Селектор для поля поиска
     * @author Наталья Танькова
     */
    private String searchFieldSelector = "//input[@id='header-search']";
    /**
     * Селектор для кнопки поиска
     * @author Наталья Танькова
     */
    private String searchButtonSelector = "//button[contains(.,'Найти')]";
    /**
     * Селектор для кнопки для перехода на следующую страницу
     * @author Наталья Танькова
     */
    private String nextPageButtonSelector = "//*[contains(@data-auto,'pagination-next')]";
    /**
     * Селектор для элемента, который появляется во время загрузки данных
     * @author Наталья Танькова
     */
    private String preloaderSelector = "//*[contains(@data-grabber,'SearchSerp')]/*[contains(@data-auto, 'preloader')]";
    public YandexMarket(WebDriver driver, int timeout) {
        this.driver = driver;
        this.timeout = timeout;
    }
    /**
     * Открывает каталог.
     * @author Наталья Танькова
     */
    public void openCatalog() {
        driver.findElement(By.xpath(catalogButtonSelector)).click();
    }
    /**
     * Наводит курсор на заданную категорию. Если элемент не найден за maxWaitInMinutes минут, тест не проходит.
     * @author Наталья Танькова
     * @param categoryName название категории
     */
    public void pointOnCategory(String categoryName) {
        String categorySelector = "//*[@data-zone-name='catalog-content']//*[contains(text(),'" + categoryName + "')]";
        long start = System.currentTimeMillis();
        long iterationStart = start;
        while (driver.findElements(By.xpath(categorySelector)).size() == 0){
            if ((System.currentTimeMillis() - iterationStart)/1000  >= timeout){
                driver.get(driver.getCurrentUrl());
                openCatalog();
                iterationStart = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - start >= maxWaitInMilliSec)
                Assert.fail("Время ожидания элемента с селектором " +categorySelector + " превысило " +
                        maxWaitInMinutes + " минут.");
        }
        WebElement category = driver.findElement(By.xpath(categorySelector));
        Actions action = new Actions(driver);
        action.moveToElement(category).build().perform();
    }
    /**
     * Открывает заданную подкатегорию. Проверяет, что подкатегория открыта.
     * @author Наталья Танькова
     * @param subcategory название подкатегории
     */
    public void openSubcategory(String subcategory){
        driver.findElement(By.xpath(
                "//*[@role='tabpanel']//ul//*[contains(text(), '" + subcategory + "')]")).click();
        Assert.assertTrue("Категория " + subcategory + " не открыта",
                driver.getTitle().toLowerCase().contains(subcategory.toLowerCase()));
    }

    /**
     * Устанавливает диапазон значений указанного фильтра.
     * @author Наталья Танькова
     * @param filterName название фильтра
     * @param min минимальное значение
     * @param max максимальное значение
     */
    public void setFilter(String filterName, double min, double max){
        setFilter(filterName, Range.MIN, min);
        setFilter(filterName, Range.MAX, max);
    }
    /**
     * Устанавливает минимальное или максимальное значение диапазона указанного фильтра.
     * @author Наталья Танькова
     * @param filterName название фильтра
     * @param range какое значение необходимо установить минимальные или максимальное
     * @param value значение
     */
    public void setFilter(String filterName, Range range, double value){
        switch (range.name()){
            case "MIN":
                driver.findElement(By.xpath(
                        "//*[contains(@data-zone-data, '" + filterName  + "')]" +
                                "//input[contains(@id, 'min')]")).sendKeys(Double.toString(value));
                break;
            case "MAX":
                driver.findElement(By.xpath(
                        "//*[contains(@data-zone-data, '" + filterName  + "')]" +
                                "//input[contains(@id, 'max')]")).sendKeys(Double.toString(value));
                break;
        }
    }
    /**
     * Устанавливает перечень значений указанного фильтра типа checkbox.
     * @author Наталья Танькова
     * @param filterName название фильтра
     * @param values список строковых значений
     */
    public void setFilter(String filterName, List<String> values) {
        String showAllSelector = "//*[contains(@data-zone-data, '" + filterName + "')]" +
                "//*[contains(text(),'Ещё') or contains(text(),'Показать всё')]";
        if (driver.findElements(By.xpath(showAllSelector)).size() > 0)
            driver.findElement(By.xpath(showAllSelector)).click();
        for (int i = 0; i < values.size(); i++) {
            WebElement searchField = null;
            String value = values.get(i);
            String searchFieldSelector = "//*[contains(@data-zone-data, '" + filterName + "')]" +
                    "//input[contains(@type,'text')]";
            if (driver.findElements(By.xpath(searchFieldSelector)).size() > 0)
                searchField = driver.findElement(By.xpath(searchFieldSelector));
            if (searchField != null)
                searchField.sendKeys(value);
            String fieldSelector = "//*[contains(@data-zone-data, '" + filterName + "')]" +
                    "//*[contains(text(),'" + value + "')]";
            WebElement field = driver.findElement(By.xpath(fieldSelector));
            String inputSelector = "//*[contains(@data-zone-data, '" + filterName + "')]" +
                    "//input[contains(following-sibling::*,'" + value + "')]";
            WebElement input = driver.findElement(By.xpath(inputSelector));
            if (!input.isSelected())
                field.click();
            if (searchField != null) {
                String clear = "//*[contains(@data-zone-data, '" + filterName + "')]" + "//button[@title='Очистить']";
                driver.findElement(By.xpath(clear)).click();
            }
        }
    }
    /**
     * Переходит на следующую страницу.
     * @author Наталья Танькова
     */
    public void nextPage(){
        driver.findElement(By.xpath(nextPageButtonSelector)).click();
        CustomWait wait = new CustomWait(driver, timeout);
        wait.untilPresenceOfElement(preloaderSelector);
        wait.untilAbsenceOfElement(preloaderSelector);
    }
    /**
     * Возвращает результат поиска.
     * @author Наталья Танькова
     * @return список элементов
     */
    public List<WebElement> getResultList() {
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.xpath("//footer")))
                .build().perform();
        List<WebElement> resultList = driver.findElements(By.xpath(listSelector));
        return resultList;
    }
    /**
     * Возвращает название заданного элемента.
     * @author Наталья Танькова
     * @param element элемент
     * @return название элемента
     */
    public String getTitle(WebElement element){
        return element.findElement(By.xpath(titleSelector)).getText();
    }
    /**
     * Возвращает поле ввода для поиска.
     * @author Наталья Танькова
     * @return поле ввода
     */
    public WebElement getSearchField(){
        return driver.findElement(By.xpath(searchFieldSelector));
    }
    /**
     * Возвращает кнопку для поиска.
     * @author Наталья Танькова
     * @return кнопка для поиска
     */
    public WebElement getSearchButton(){
        return driver.findElement(By.xpath(searchButtonSelector));
    }
    /**
     * Получает заданные фильтры из текущего адреса.
     * @author Наталья Танькова
     * @return карта фильтров, состоящая из названия фильтра и списка значений
     */
    public Map<String, List<String>> getFilters(){
        String url = driver.getCurrentUrl();
        Map<String, List<String>> filters = new HashMap<>();
        String priceFromString = "pricefrom=";
        if(url.contains(priceFromString)) {
            String urlSubstring = url.substring(url.indexOf(priceFromString) + priceFromString.length());
            if (urlSubstring.contains("&"))
                urlSubstring = urlSubstring.substring(0,urlSubstring.indexOf("&"));
            filters.put("Цена, ₽", new ArrayList<>(List.of(urlSubstring)));
        }
        String priceToString = "priceto=";
        if(url.contains(priceToString)) {
            String urlSubstring = url.substring(url.indexOf(priceToString) + priceToString.length());
            if (urlSubstring.contains("&"))
                urlSubstring = urlSubstring.substring(0,urlSubstring.indexOf("&"));
            if (!filters.containsKey("Цена, ₽"))
                filters.put("Цена, ₽", new ArrayList<>(List.of("0")));
            filters.get("Цена, ₽").add(urlSubstring);
        }
        if(filters.containsKey("Цена, ₽") && filters.get("Цена, ₽").size()==1)
            filters.get("Цена, ₽").add(String.valueOf(Long.MAX_VALUE));
        String[] filtersString = url.split("&glfilter=");
        for (int i = 1; i < filtersString.length; i++){
            String filterString = filtersString[i];
            if (filterString.contains("&"))
                filterString = filterString.substring(0,filterString.indexOf("&"));
            String filterId = filterString.substring(0, filterString.indexOf('%'));
            WebElement filterElement = driver.findElement(By.xpath(
                    "//*[@data-zone-name='SearchFilters']//*[@data-filter-id= '"
                            + filterId + "']"));
            String filterName = filterElement.findElement(By.xpath(".//legend")).getText();
            String filterType = filterElement.getAttribute("data-filter-type");
            String valuesString = filterString.substring(filterString.indexOf('A')+1);
            if (filterType.equals("enum")) {
                String[] valuesId = valuesString.split("%2C");
                List<String> list = new ArrayList<>();
                for (int j = 0; j < valuesId.length; j++)
                    list.add(driver.findElement(By.xpath(
                            "//*[@data-filter-value-id='" + valuesId[j] + "']")).getText());
                filters.put(filterName, list);
            }
            if (filterType.equals("range")) {
                String[] values = valuesString.split("~");
                if (values.length == 1)
                    filters.put(filterName, List.of(values[0], String.valueOf(Long.MAX_VALUE)));
                else if (values[0].equals(""))
                    filters.put(filterName, List.of("0", values[1]));
                else
                    filters.put(filterName, List.of(values[0], values[1]));
            }
        }
        return filters;
    }
    /**
     * Проверяет, что результаты поиска соответствуют заданным фильтрам.
     * Для фильтров, заданных диапазоном, в названии фильтра через запятую следует единица измерения. Для таких фильтров
     * в названии и описании товара ищется подстрока, состоящая из числа и единицы измерения. Затем число из подстроки
     * сравнивается с заданным диапазоном.
     * Для фильтров типа checkbox, принимающих перечень значений, в названии и описании товара ищутся заданные значения.
     * Если товар не соответствуют какому-либо фильтру, метод сохраняет название товара и сообщение об ошибке в
     * карту elementsNotMatch.
     * @author Наталья Танькова
     * @param resultList результаты поиска
     * @param filters карта фильтров, состоящая из названия фильтра и списка значений
     * @param elementsNotMatch карта товаров, которые не содержат необходимые значения,
     *                           состоящая из названия товара и списка сообщений об ошибке
     */
    public void checkGoodsMatchFilters(List<WebElement> resultList,
                                       Map<String, List<String>> filters,
                                       Map<String,List<String>> elementsNotMatch){
        resultList.stream().forEach(x->{
            String text = x.getText().toLowerCase();
            for (String filterName: filters.keySet()) {
                List<String> values = filters.get(filterName);
                if (filterName.contains(", ")){
                    String measurementUnit = filterName.substring(filterName.indexOf(", ") +", ".length());
                    String rangeRegex = "\\d* ?\\d+(\\.\\d+)? ?" + measurementUnit;
                    Pattern rangePattern = Pattern.compile(rangeRegex);
                    Matcher rangeMatcher = rangePattern.matcher(text);
                    if (rangeMatcher.find()) {
                        String stringValue = text.substring(rangeMatcher.start(), rangeMatcher.end()).trim().replace(
                                        measurementUnit, "")
                                .replace(" ", "")
                                .replace(",",".");
                        Double value = Double.parseDouble(stringValue);
                        if (value < Double.parseDouble(values.get(0)) || value > Double.parseDouble(values.get(1))) {
                            if (!elementsNotMatch.containsKey(x.findElement(By.xpath(titleSelector)).getText()))
                                elementsNotMatch.put(x.findElement(By.xpath(titleSelector)).getText(),new ArrayList<>());
                            elementsNotMatch.get(x.findElement(By.xpath(titleSelector)).getText()).add(
                                    "Товар  не соответствует фильтру " + filterName.toLowerCase() +
                                            " от " + values.get(0) + " до " + values.get(1));
                        }
                    } else {
                        if (!elementsNotMatch.containsKey(x.findElement(By.xpath(titleSelector)).getText()))
                            elementsNotMatch.put(x.findElement(By.xpath(titleSelector)).getText(),new ArrayList<>());
                        elementsNotMatch.get(x.findElement(By.xpath(titleSelector)).getText()).add(
                                "Товар не соответствует условию: значение " + filterName.toLowerCase() + " не указано");
                    }
                } else {
                    boolean contains = false;
                    for (String value : values)
                        contains = contains || text.contains(value.toLowerCase());
                    if (!contains) {
                        if (!elementsNotMatch.containsKey(x.findElement(By.xpath(titleSelector)).getText()))
                            elementsNotMatch.put(x.findElement(By.xpath(titleSelector)).getText(),new ArrayList<>());
                        elementsNotMatch.get(x.findElement(By.xpath(titleSelector)).getText()).add(
                                "Товар не соответствует условию: фильтр " + filterName.toLowerCase() +
                                        " = " + values);
                    }
                }
            }
        });
    }
    /**
     * Проверяет, содержится ли на странице кнопка для перехода на следующую страницу.
     * @author Наталья Танькова
     * @return логическое значение
     */
    public boolean containsNextPageButton(){
        return driver.findElements(By.xpath("//*[contains(@data-auto,'pagination-next')]")).size() > 0;
    }

    /**
     * Ждет обновления данных.
     * @author Наталья Танькова
     */
    public void waitForDataRefresh(){
        CustomWait wait = new CustomWait(driver, timeout);
        wait.untilPresenceOfElement(preloaderSelector);
        wait.untilAbsenceOfElement(preloaderSelector);
    }
    /**
     * Проверяет, что в названии результатов поиска на одной странице содержатся нужные значения.
     * Если не содержатся, сохраняет названия товаров в список.
     * @author Наталья Танькова
     * @param resultList результаты поиска
     * @param values список значений, которые необходимо искать в названии
     * @param elementsNotContain список товаров, которые не содержат необходимые значения
     */
    public void goodsTitleContains(List<WebElement> resultList, List<String> values, List<String> elementsNotContain){
        resultList.stream().forEach(x->{
                    String text = x.getText().toLowerCase();
                    for (String value : values) {
                        if (!text.contains(value.toLowerCase())) {
                            elementsNotContain.add("\""+x.findElement(By.xpath(titleSelector)).getText()+"\"");
                            break;
                        }
                    }
                });
    }

    /**
     * Проверяет наличие заданных значений в названии товаров на всех страницах.
     * Если они отсутствуют, сохраняет название товара в список. Если после проверки список не пустой, тест не проходит
     * и выводится список товаров, не прошедших проверку.
     * Если время проверки занимает больше, чем maxWaitInMinutes минут, тест не проходит.
     * @author Наталья Танькова
     * @param values список значений, которые необходимо искать в названии
     */
    public void checkGoodsTitle(List<String> values){
        List<WebElement> result = getResultList();
        List<String> elementsNotContain = new ArrayList<>();
        goodsTitleContains(result, values, elementsNotContain);
        long start = System.currentTimeMillis();
        while (driver.findElements(By.xpath("//*[contains(@data-auto,'pagination-next')]")).size()>0){
            nextPage();
            result = getResultList();
            goodsTitleContains(result, values,elementsNotContain);
            if (System.currentTimeMillis() - start >= maxWaitInMilliSec)
                Assert.fail("Проверка \"Название товара содержит значения " + values + "\" " +
                                "занимает больше " + maxWaitInMinutes + " минут");
        }
        Assert.assertEquals("Товары " + elementsNotContain + " не содержат значения " + values,
                elementsNotContain.size(),0);
    }
}