package coverage.exceptions;

public class MissingEnvVarException extends RuntimeException {
    public MissingEnvVarException(String variable) {
        super("Environmental variable [" + variable + "] is not set for the execution");
    }
}
