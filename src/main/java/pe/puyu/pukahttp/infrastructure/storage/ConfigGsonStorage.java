package pe.puyu.pukahttp.infrastructure.storage;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class ConfigGsonStorage {

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm_dd|MM|yyyy");
    }

    public static String makeFileNameTo(LocalDateTime date, String id) {
        return String.format("%s-%s.json", date.format(getDateTimeFormatter()), id);
    }
}
