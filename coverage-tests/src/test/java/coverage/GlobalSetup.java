package coverage;

import com.applitools.connectivity.RestClient;
import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.*;
import coverage.exceptions.MissingEnvVarException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class GlobalSetup {

    protected WebDriver driver;
    protected WebDriver eyesDriver;
    protected static BatchInfo batch;
    protected static String apiKey;
    public static boolean useDocker;
    public static boolean CI;
    public static String EG_URL;

    public WebDriver getDriver() {
        return eyesDriver == null ? driver : eyesDriver;
    }
    @BeforeSuite
    public void globalSetup() {
        String name = System.getenv("APPLITOOLS_BATCH_NAME");
        if (name == null) name = "JAVA coverage tests";
        batch = new BatchInfo(name);
        String id = System.getenv("APPLITOOLS_BATCH_ID");
        if (id != null) batch.setId(id);
        apiKey = System.getenv("APPLITOOLS_API_KEY");
        EG_URL = System.getenv("EXECUTION_GRID_URL");
        String CI = System.getenv("CI");
        GlobalSetup.CI = CI != null && CI.equals("true");
        useDocker = !GlobalSetup.CI;
        String envVar = System.getenv("USE_SELENIUM_DOCKER");
        if (envVar != null && envVar.equals("false")) {
            useDocker = false;
        }
        if(!useDocker) {
            String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
            if(chromeDriverPath == null) throw new MissingEnvVarException("CHROME_DRIVER_PATH");
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            String firefoxDriverPath = System.getenv("FIREFOX_DRIVER_PATH");
            if(firefoxDriverPath == null) throw new MissingEnvVarException("FIREFOX_DRIVER_PATH");
            System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
        }


    }

    @AfterSuite
    public void closeBatch() {
        try {
            String server = "eyesapi.applitools.com";
            String url = "https://" + server + "/api/sessions/batches/" + batch.getId() + "/close/bypointerid/?apiKey=" + apiKey;
            URI requestUrl = UriBuilder.fromUri(url).build();
            RestClient client = new RestClient(new Logger(), requestUrl, ServerConnector.DEFAULT_CLIENT_TIMEOUT);
            if (System.getenv("APPLITOOLS_USE_PROXY") != null) {
                client.setProxy(new ProxySettings("http://127.0.0.1", 8888));
            }
            int statusCode = client.sendHttpRequest(requestUrl.toString(), HttpMethod.DELETE).getStatusCode();
            System.out.println(statusCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}