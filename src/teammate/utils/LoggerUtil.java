package teammate.utils;

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {

    private static final Logger logger = Logger.getLogger("TeamMateLogger");
    private static FileHandler fileHandler;

    static {

        try {
            fileHandler = new FileHandler("teamMate.log", true);
            fileHandler.setFormatter(new TableFormatter());

            logger.setUseParentHandlers(false);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

        } catch (IOException e) {
            System.err.println("Logging initialization failed: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    // Custom formatter to match screenshot EXACTLY
    private static class TableFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {

            String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(record.getMillis());

            return String.format(
                    "%-19s | %-7s | %-20s | %-15s | %s%n",
                    time,
                    record.getLevel().getName(),
                    record.getSourceClassName().substring(
                            record.getSourceClassName().lastIndexOf('.') + 1),
                    record.getSourceMethodName(),
                    record.getMessage()
            );
        }
    }
}
