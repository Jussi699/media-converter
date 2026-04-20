package model.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorLogger {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLogger.class);

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    public static void log(int errorCode, Level level, String message, Throwable t) {
        String logMessage = String.format("[%d] %s", errorCode, message);

        switch (level) {
            case DEBUG -> logger.debug(logMessage, t);
            case INFO -> logger.info(logMessage, t);
            case WARN -> logger.warn(logMessage, t);
            case ERROR -> logger.error(logMessage, t);
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }
}
