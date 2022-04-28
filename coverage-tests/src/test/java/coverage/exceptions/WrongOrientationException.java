package coverage.exceptions;

import com.applitools.eyes.visualgrid.model.ScreenOrientation;

public class WrongOrientationException extends RuntimeException {
    public WrongOrientationException(String variable) {
        super("Wrong orientation was used [" + variable + "], allowed values are:" + ScreenOrientation.LANDSCAPE+ " | " + ScreenOrientation.PORTRAIT);
    }
}
