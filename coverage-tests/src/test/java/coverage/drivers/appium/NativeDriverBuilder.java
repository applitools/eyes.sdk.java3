package coverage.drivers.appium;

import coverage.drivers.browsers.DriverBuilder;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.util.HashMap;

public class NativeDriverBuilder extends DriverBuilder {

    private String device;
    private String app;
    protected static HashMap<String, NativeBuilder> nativeBuilders = new HashMap<String, NativeBuilder>()
    {{
        put("Samsung Galaxy S8", new SamsungGalaxyS8());
        put("iPhone XS", new IPhoneXS());
        put("Pixel 3 XL", new Pixel3XL());
    }};

    public NativeDriverBuilder device(String device) {
        this.device = device;
        return this;
    }

    public NativeDriverBuilder app(String app) {
        this.app = app;
        return this;
    }

    public WebDriver build() throws MalformedURLException {
        NativeBuilder builder = nativeBuilders.get(device);
        return builder.build(app);
    }

}
