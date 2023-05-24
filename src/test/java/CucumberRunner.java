import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Класс, который запускает тесты
 * @author Наталья Танькова
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        plugin = {"io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm", "pretty", "json:target/cucumber-report/report.json"},
        features="src/test/java/features",
        glue = "ru/yandex/market",
        tags = "@correct"
)
public class CucumberRunner {
}
