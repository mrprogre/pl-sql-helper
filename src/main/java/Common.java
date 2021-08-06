import org.apache.tika.Tika;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.event.FocusEvent;
import java.util.stream.Stream;

public class Common extends Gui {
    static long trayTimeForMessage = 6000;
    static String[] devProdValues = new String[countLines(Main.configPath) - 1];
    static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";

    // Считывание всех строк из файла в двумерный массив строк
    static String[][] getConfig() {
        int rowsCount = countLines(Main.configPath);
        String[][] lines = new String[rowsCount][];

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(Main.configPath), StandardCharsets.UTF_8))) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null && i < rowsCount) {
                lines[i++] = line.split(";");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // Запись сред разработки в комбобокс
    static void addEnv(){
        int rowsCount = countLines(Main.configPath) - 1;
        String [][] config = getConfig();

        // Среды разработки для комбобокса
//        for (int i = 0; i < rowsCount - 1; i++) {
//            devProdValues[i] = config[i][0];
//        }
        for (int i = 0; i < rowsCount; i++) {
            devProdValues[i] = config[i + 1][0];
        }

    }

    // статус выполнения программы
    static void notification(String status) {
        (new Thread(() -> {
            try {
                Gui.statusLbl.setText("");
                Thread.sleep(300L);
                Gui.statusLbl.setText(status);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();
    }

    // Уведомление в трее
    static void trayMessage(String pMessage) {
        if (SystemTray.isSupported()) {
            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Close");

            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(Common.class.getResource("icons/message.png"));
            TrayIcon trayIcon = new TrayIcon(image, "PL/SQL monitor message", popup);
            trayIcon.setImageAutoSize(true);
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            trayIcon.displayMessage("PL/SQL monitor", pMessage, TrayIcon.MessageType.INFO);
            try {
                Thread.sleep(trayTimeForMessage);
                systemTray.remove(trayIcon);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            exitItem.addActionListener(e -> systemTray.remove(trayIcon));
            popup.add(exitItem);
        }
    }

    // добавляем название таблиц в комбобокс после подключения к конкретной среде
    static void addItemsToCombobox() {
        for (int i = 0; i < Oracle.user_tables.size(); i++) {
            tableNamesBox.addItem(Oracle.user_tables.get(i));
        }
    }

    // добавляем название избранных таблиц в комбобокс
    static void addItemsToFavCombobox() {
        int rowsCount;
        if (isSelectFavouriteTab && Oracle.isConnectedToVPN) {
            if (tableNamesBox.getItemCount() > 0) tableNamesBox.removeAllItems();
            rowsCount = Common.countLines(Main.favoritesTabPath);
            String[][] favoriteTabNames = Common.getLinesFromFile2(rowsCount);
            String env = (String) devProd.getSelectedItem();

            for (String[] f : favoriteTabNames) {
                for (int j = 0; j < 1; j++) {
                    assert env != null;
                    if (env.equals("dev") && f[1].equals("dev")) {
                        tableNamesBox.addItem(f[0]);
                    } else if (env.equals("test") && f[1].equals("test")) {
                        tableNamesBox.addItem(f[0]);
                    } else if (env.equals("prod") && f[1].equals("prod")) {
                        tableNamesBox.addItem(f[0]);
                    }
                }
            }
        } else {
            if (tableNamesBox.getItemCount() > 0) tableNamesBox.removeAllItems();
            Common.addItemsToCombobox();
        }
    }

    // Анимация поиска в таблице лога
    static void Searching(AtomicBoolean isSearchFinished) {
        Thread thr = new Thread(() -> {
            while (!isSearchFinished.get()) {
                try {
                    if (isSearchFinished.get()) {
                        return;
                    }
                    statusLbl.setText("searching");
                    Thread.sleep(500L);
                    if (isSearchFinished.get()) {
                        return;
                    }
                    statusLbl.setText("searching.");
                    Thread.sleep(500L);
                    if (isSearchFinished.get()) {
                        return;
                    }
                    statusLbl.setText("searching..");
                    Thread.sleep(500L);
                    if (isSearchFinished.get()) {
                        return;
                    }
                    statusLbl.setText("searching.");
                    Thread.sleep(500L);
                    if (isSearchFinished.get()) {
                        return;
                    }
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            }
        });
        thr.start();
    }

    // Подсказка для поля where
    static class JTextFieldHintListener implements FocusListener {
        private final String hintText;
        private final JTextField textField;

        public JTextFieldHintListener(JTextField jTextField, String hintText) {
            this.textField = jTextField;
            this.hintText = hintText;
            jTextField.setText(hintText);
            jTextField.setForeground(Color.GRAY);
        }

        @Override
        public void focusGained(FocusEvent e) {
            String temp = textField.getText();
            if (temp.equals(hintText)) {
                textField.setText("");
                textField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            String temp = textField.getText();
            if (temp.equals("")) {
                textField.setForeground(Color.GRAY);
                textField.setText(hintText);
            }
        }
    }

    // Определение типа столбца для корректной сортировки любой таблицы
    static Class[] typeClass(ArrayList<String> p_type_list) {
        Class[] column_types = new Class[p_type_list.size()];
        try {
            for (int i = 0; i < p_type_list.size(); i++) {
                switch (p_type_list.get(i)) {
                    case "NUMBER":
                    case "RAW":
                        column_types[i] = Integer.class;
                        break;
                    case "FLOAT":
                        column_types[i] = Float.class;
                        break;
                    case "BLOB":
                    case "CLOB":
                        //column_types[i] = Clob.class;
                        //column_types[i] = Blob.class;
                        column_types[i] = JButton.class;
                        break;
                    case "DATE":
                        column_types[i] = Date.class;
                        break;
                    default:
                        column_types[i] = String.class;
                        break;
                }
            }
        } catch (Exception r) {
            r.printStackTrace();
        }
        return column_types;
    }

    // Скачивание BLOB файла
    static void getBlobFromTable(String p_file_path, Blob p_plob) {
        try {
            InputStream is_blob = p_plob.getBinaryStream();

            Tika tika = new Tika();
            String mime_type_full = tika.detect(is_blob);

            String mimeType = null;
            if (mime_type_full.contains("image/")) mimeType = mime_type_full.replace("image/", "");
            if (mime_type_full.contains("application/")) mimeType = mime_type_full.replace("application/", "");
            if (mime_type_full.contains("text/")) mimeType = mime_type_full.replace("text/", "");
            if (mime_type_full.contains("audio/")) mimeType = mime_type_full.replace("audio/", "");
            if (mime_type_full.contains("video/")) mimeType = mime_type_full.replace("video/", "");

            switch (Objects.requireNonNull(mimeType)) {
                case "x-tika-ooxml":
                    mimeType = "xlsx";
                    break;
                case "x-tika-msoffice":
                    mimeType = "xls";
                    break;
                case "octet-stream":
                case "plain":
                    mimeType = "txt";
                    break;
            }
            FileOutputStream fos = new FileOutputStream(p_file_path + "." + mimeType);

            int b;
            while ((b = is_blob.read()) != -1) {
                fos.write(b);
            }
            is_blob.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Скачивание CLOB файла
    static void getBlobFromTable(String p_file_path, Clob p_clob) {
        try {
            InputStream is_clob = p_clob.getAsciiStream();
            Tika tika = new Tika();
            String mime_type_full = tika.detect(is_clob);

            String mimeType = null;
            if (mime_type_full.contains("image/")) mimeType = mime_type_full.replace("image/", "");
            if (mime_type_full.contains("application/")) mimeType = mime_type_full.replace("application/", "");
            if (mime_type_full.contains("text/")) mimeType = mime_type_full.replace("text/", "");
            if (mime_type_full.contains("audio/")) mimeType = mime_type_full.replace("audio/", "");
            if (mime_type_full.contains("video/")) mimeType = mime_type_full.replace("video/", "");

            switch (Objects.requireNonNull(mimeType)) {
                case "x-tika-ooxml":
                    mimeType = "xlsx";
                    break;
                case "x-tika-msoffice":
                    mimeType = "xls";
                    break;
                case "octet-stream":
                case "plain":
                    mimeType = "txt";
                    break;
            }
            System.out.println(mimeType);

            BufferedWriter file_writer = new BufferedWriter(new FileWriter(p_file_path + "." + mimeType));
            BufferedReader breader;
            final int BUFFER_SIZE = 4096;
            breader = new BufferedReader(p_clob.getCharacterStream());
            int length = -1;
            long size = 0;
            char[] buf = new char[BUFFER_SIZE];

            while ((length = breader.read(buf, 0, BUFFER_SIZE)) != -1) {
                file_writer.write(buf, 0, length);
                size += length;
            }
            breader.close();
            file_writer.close();
        } catch (SQLException | IOException exc) {
            exc.printStackTrace();
        }

    }

    // Считывание всех строк из файла в массив строк
    static String[] getLinesFromFile(int linesAmount) {
        String[] lines = new String[linesAmount];

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(Main.favoritesTabPath), StandardCharsets.UTF_8))) {
            String line;
            int i = 0;

            while ((line = reader.readLine()) != null && i < linesAmount) {
                lines[i++] = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    // Считывание всех строк из файла в диалоговое окно
    static void setDialogFavTabText(String p_file) {
        String path = "";
        if (p_file.equals("fav")) path = Main.favoritesTabPath;
        else if (p_file.equals("log")) path = Main.logPath;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder allTab = new StringBuilder();
            int i = 1;

            while ((line = reader.readLine()) != null) {
                allTab.append(i).append(") ").append(line).append("\n");
                i++;
            }
            Dialogs.favTabsList.setText(allTab.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Считывание всех строк из файла в двумерный массив строк
    static String[][] getLinesFromFile2(int linesAmount) {
        String[][] lines = new String[linesAmount][];

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(Main.favoritesTabPath), StandardCharsets.UTF_8))) {
            String line;
            int i = 0;

            while ((line = reader.readLine()) != null && i < linesAmount) {
                lines[i++] = line.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(Arrays.deepToString(lines));
        //System.out.println(lines[0][0] + lines[0][1]);
        return lines;
    }

    // Подсчет количества строк и символов в файле
    static int countLines(String path) {
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(path));
            int cnt;
            while (true) {
                if (reader.readLine() == null) break;
            }
            cnt = reader.getLineNumber();
            reader.close();
            return cnt;
        } catch (IOException io) {
            io.printStackTrace();
        }
        return 0;
    }

    // Есть ли таблица в избранном
    static void isTabInFavorites() {
        int rowsCount = Common.countLines(Main.favoritesTabPath);
        String[][] favTab = Common.getLinesFromFile2(rowsCount);
        String tabName = (String) tableNamesBox.getSelectedItem();
        String env = (String) devProd.getSelectedItem();

        for (int i = 0; i < rowsCount; i++) {
            if (favTab[i][0].equals(tabName) && Objects.equals(env, "dev") && favTab[i][1].equals("dev")) {
                starBtn.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(Common.class.getResource("icons/yes.png"))));
                isAddToFavorites = true;
            } else if (favTab[i][0].equals(tabName) && Objects.equals(env, "test") && favTab[i][1].equals("test")) {
                starBtn.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(Common.class.getResource("icons/yes.png"))));
                isAddToFavorites = true;
            } else if (favTab[i][0].equals(tabName) && Objects.equals(env, "prod") && favTab[i][1].equals("prod")) {
                starBtn.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage(Common.class.getResource("icons/yes.png"))));
                isAddToFavorites = true;
            } else isAddToFavorites = false;
        }
    }

    // Удаление таблицы из избранного
    static void delTabFromFavorites() throws IOException {
        String tabName = (String) tableNamesBox.getSelectedItem();
        String env = (String) devProd.getSelectedItem();

        Path input = Paths.get(Main.favoritesTabPath);
        Path temp = Files.createTempFile("temp", ".txt");
        Stream<String> lines = Files.lines(input);
        try (BufferedWriter writer = Files.newBufferedWriter(temp)) {
            lines
                    .filter(line -> {
                        assert tabName != null;
                        return !line.startsWith(tabName + "," + env);
                    })
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        Files.move(temp, input, StandardCopyOption.REPLACE_EXISTING);

        if (favouriteTabCheckBox.getState()) {
            addItemsToFavCombobox();
        }
        isTabInFavorites();
    }

    // Проверка является ли текст цифрой
    static boolean isDigit(String s) {
        return s.matches("[-+]?\\d+");
    }

    // Получить номер дисплея на котором находится приложение
    static int getDisplay(Gui gui) {
        GraphicsConfiguration config = gui.getGraphicsConfiguration();
        GraphicsDevice myScreen = config.getDevice();
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allScreens = env.getScreenDevices();

        int myScreenIndex = 0;
        for (int i = 0; i < allScreens.length; i++) {
            if (allScreens[i].equals(myScreen)) {
                myScreenIndex = i;
                break;
            }
        }
        //System.out.println("window is on screen" + myScreenIndex);
        return myScreenIndex + 1;
    }

    //Конвертация клавиш
    static String convert(String message) {
        message = message.toLowerCase(Locale.ROOT);
        boolean result = message.matches(".*\\p{InCyrillic}.*");
        char[] ru = {'й','ц','у','к','е','н','г','ш','щ','з','х','ъ','ф','ы','в','а','п','р','о','л','д','ж','э', 'я','ч', 'с','м','и','т','ь','б', 'ю','.'};
        char[] en = {'q','w','e','r','t','y','u','i','o','p','[',']','a','s','d','f','g','h','j','k','l',';','"','z','x','c','v','b','n','m',',','.','/'};
        StringBuilder builder = new StringBuilder();

        if (result) {
            for (int i = 0; i < message.length(); i++) {
                for (int j = 0; j < ru.length; j++ ) {
                    if (message.charAt(i) == ru[j]) {
                        builder.append(en[j]);
                    }
                }
            }
        } else {
            for (int i = 0; i < message.length(); i++) {
                for (int j = 0; j < en.length; j++ ) {
                    if (message.charAt(i) == en[j]) {
                        builder.append(ru[j]);
                    }
                }
            }
        }
        return builder.toString().toUpperCase();
    }

}
