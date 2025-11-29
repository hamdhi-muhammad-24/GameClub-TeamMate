//package teammate.utils;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.logging.Formatter;
//import java.util.logging.LogRecord;
//
//public class LogFormatter extends Formatter {
//
//    private static final DateTimeFormatter TS_FORMAT =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    @Override
//    public String format(LogRecord record) {
//
//        String timestamp = LocalDateTime.now().format(TS_FORMAT);
//
//        String level = record.getLevel().getName();
//        String className = record.getSourceClassName();
//        String method = record.getSourceMethodName();
//        String message = record.getMessage();
//
//        return String.format("%s | %-5s | %-15s | %-20s | %s%n",
//                timestamp,
//                level,
//                simpleClassName(className),
//                method,
//                message
//        );
//    }
//
//    private String simpleClassName(String fullName) {
//        if (fullName == null) return "";
//        int dot = fullName.lastIndexOf('.');
//        return (dot == -1) ? fullName : fullName.substring(dot + 1);
//    }
//}
