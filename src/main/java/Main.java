import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class Main {
    static String directoryPath = "C:\\Users\\Public\\Documents\\PLSQL_Log_Monitor\\";
    static String favoritesTabPath = directoryPath + "favorites.txt";
    static String logPath = directoryPath + "log.txt";
    static String configPath = directoryPath + "config.txt";
    public static final Logger LOGGER = Logger.getLogger("");

    // Создание файлов для избранного и лога
    static {
        File directory = new File(directoryPath);
        File fav_file = new File(favoritesTabPath);
        File log_file = new File(logPath);
        File config_file = new File(configPath);

        try {
            if (!directory.exists()) directory.mkdirs();
            if (!fav_file.exists()) fav_file.createNewFile();
            if (!log_file.exists()) log_file.createNewFile();
            if (!config_file.exists()) config_file.createNewFile();

            // запись лога в файл
            Handler handler = new FileHandler(logPath, true);
            handler.setLevel(Level.ALL);
            handler.setEncoding("UTF-8");
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Приложение запущено");
        new Gui();
        Common.notification("first you need to connect to VPN");
    }

}
