import com.applitools.eyes.*;
import com.applitools.eyes.playwright.ClassicRunner;
import com.applitools.eyes.playwright.visualgrid.BrowserType;
import com.applitools.eyes.playwright.Eyes;
import com.applitools.eyes.playwright.Target;
import com.applitools.eyes.selenium.ClassicRunner0;
import com.microsoft.playwright.*;
import org.testng.annotations.*;

public class PlaywrightDemo {

    // Shared between all tests in this class.
    private static Playwright playwright;
    private static Browser browser;

    // New instance for each test method.
    private BrowserContext context;
    private Page page;
    private Eyes eyes;


    @BeforeTest
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();

//        eyes = new Eyes(new VisualGridRunner(new RunnerOptions().testConcurrency(5)));
//        setUFGConfiguration(eyes);
        eyes = new Eyes(new ClassicRunner());
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setHostOS("myHostOS");
    }

    private void setUFGConfiguration(Eyes eyes) {
        eyes.setConfiguration(eyes.getConfiguration()
                .addBrowser(1400, 700, BrowserType.CHROME)
                .addBrowser(1200, 900, BrowserType.FIREFOX)
        );
    }

    @BeforeMethod
    public void beforeEach() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod
    public void afterEach() {
        context.close();
    }

    @AfterTest
    public void teardown() {
        playwright.close();
    }

    @Test
    public void test() {
        page.navigate("https://demo.applitools.com");

        eyes.open(page, "Playwright Java", "Test Playwright Java"
                 , new RectangleSize(1400, 700));
        eyes.check(Target.window().fully(true));
        TestResults res = eyes.close(true);
        System.out.println(res);
    }


}