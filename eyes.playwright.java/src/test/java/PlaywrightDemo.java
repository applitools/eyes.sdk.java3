import com.applitools.eyes.TestResults;
import com.applitools.eyes.playwright.Eyes;
import com.applitools.eyes.playwright.Target;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.*;

public class PlaywrightDemo {

    private Browser browser;
    private Page page;
    private Eyes eyes;

    @BeforeTest
    public void setup() {
        try (Playwright playwright = Playwright.create()) {
            browser = playwright.chromium().launch();
            page = browser.newPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod
    public void beforeEach() {
        page.navigate("https://demo.applitools.com");
    }

    @AfterTest
    public void teardown() {
        browser.close();
    }

    @Test
    public void test() {
        eyes = new Eyes();

        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.open(page, "Playwright Java", "Test Playwright Java");
        eyes.check(Target.window());
        TestResults res = eyes.close(true);
        System.out.println(res);
    }
}
