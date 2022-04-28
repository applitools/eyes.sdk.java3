package coverage.drivers.appium;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

public abstract class AbstractNativeBuilder {
    public Capabilities prepareCaps(Capabilities initial, String app, String orientation, boolean legacy) {
        MutableCapabilities appCap = new MutableCapabilities();
        if(legacy) {
            appCap.setCapability("app", app);
            appCap.setCapability("deviceOrientation", orientation);
        } else {
            appCap.setCapability("appium:app", app);
            MutableCapabilities sauce = (MutableCapabilities) initial.getCapability("sauce:options");
            sauce.setCapability("deviceOrientation", orientation);
        }
        return initial.merge(appCap);
    }
}
