package coverage.drivers.appium;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import coverage.drivers.browsers.DriverBuilder;
import coverage.exceptions.WrongOrientationException;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.util.HashMap;

public class NativeDriverBuilder extends DriverBuilder {

    private String device;
    private String app;
    private String orientation = ScreenOrientation.PORTRAIT.value();
    private boolean legacy = false;
    protected static HashMap<String, NativeBuilder> nativeBuilders = new HashMap<String, NativeBuilder>()
    {{
        put("Samsung Galaxy S8", new SamsungGalaxyS8());
        put("iPhone XS", new IPhoneXS());
        put("iPhone 12", new IPhone12());
        put("Pixel 3 XL", new Pixel3XL());
        put("Pixel 3a XL", new Pixel3aXL());
    }};

    public NativeDriverBuilder device(String device) {
        this.device = device;
        return this;
    }

    public NativeDriverBuilder app(String app) {
        this.app = app;
        return this;
    }

    public NativeDriverBuilder orientation(String orientation) {
        if(orientation.equals(ScreenOrientation.LANDSCAPE.value())){
            this.orientation = orientation;
        } else if(orientation.equals(ScreenOrientation.PORTRAIT.value())) {
            this.orientation = orientation;
        } else {
            throw new WrongOrientationException(orientation);
        }
        return this;
    }

    public NativeDriverBuilder legacy(boolean legacy) {
        this.legacy = legacy;
        return this;
    }

    public WebDriver build() throws MalformedURLException {
        NativeBuilder builder = nativeBuilders.get(device);
        return builder.build(app, orientation, legacy);
    }

}
