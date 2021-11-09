import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Oracle {
    Common common = new Common();
    static Connection connect;
    static AtomicBoolean isStop = new AtomicBoolean(false);
    static boolean isConnectedToVPN = false;
    static String column_name;
    static ArrayList<String> user_tables = new ArrayList<>();
    static ArrayList<String> headers = new ArrayList<>();
    static ArrayList<String> types = new ArrayList<>();
    static ArrayList<Integer> colLengths = new ArrayList<>();
    static Set<String> blobs = new LinkedHashSet<>();
    static ArrayList<String> row_ids = new ArrayList<>();
    static boolean isSelectOrGoFinished;
    AtomicBoolean isSearchFinished;
    double searchTime;
    double timeStart;
    int amount;
    int max;
    int max2;
    int column_count;
    int columnCount;
    int data_type_count;
    String data_type;
    //config.txt
    private final static String[][] config = Common.getConfig();
    private final String driver = config[0][0];
    static final String logTableFromConfig = config[1][0].replace("log_table=", "");
    private final String column_id = config[2][0].replace("column_id=", "");
    private final String column_date = config[3][0].replace("column_date=", "");
    private final String column_text = config[4][0].replace("column_text=", "");

    // открытие соединения
    void open() {
        try {
            int index = Gui.devProd.getSelectedIndex() + 5;
            Class.forName(driver);

            //dev
            if (index == 5) {
                connect = DriverManager.getConnection(config[index][1].trim(), config[index][2], config[index][3]);
                Common.notification("connected to " + config[index][0].toUpperCase());
                isConnectedToVPN = true;
                Gui.textWhereClause.setText("WHERE fl_date > SYSDATE - 1/1440 AND fl_text LIKE '%sabre%'");
                //test
            } else if (index == 6) {
                connect = DriverManager.getConnection(config[index][1].trim(), config[index][2], config[index][3]);
                Common.notification("connected to " + config[index][0].toUpperCase());
                isConnectedToVPN = true;
                Gui.textWhereClause.setText("WHERE fl_date > SYSDATE - 1/1440 " +
                        "AND fl_text LIKE '%шибк%' " +
                        "AND fl_text not like '%отправк%' " +
                        "AND fl_text not like '%остоянн%'");
                //prod
            } else if (index == 7) {
                connect = DriverManager.getConnection(config[index][1].trim(), config[index][2], config[index][3]);
                Common.notification("connected to " + config[index][0].toUpperCase());
                isConnectedToVPN = true;
                Gui.textWhereClause.setText("WHERE fl_date > SYSDATE - 1/1440 AND (fl_text LIKE '%шибк%' OR fl_text LIKE '%DEBUG%')");
                //akr
            } else if (index == 8) {
                connect = DriverManager.getConnection(config[index][1].trim(), config[index][2], config[index][3]);
                Common.notification("connected to " + config[index][0].toUpperCase());
                isConnectedToVPN = true;
                Gui.textWhereClause.setText("WHERE fl_date > SYSDATE - 1/1440 AND fl_text LIKE '%шибк%'");
            }
            if (isConnectedToVPN) {
                getUserTables();
                Common.addItemsToCombobox();
                Common.isTabInFavorites();
            }
        } catch (Exception e) {
            Common.notification("check VPN or input correct password");
        }
    }

    // закрытие соединения
    void close() {
        try {
            if (isConnectedToVPN) connect.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // выборка данных из лога
    void select() {
        int modelRowCount = Gui.model.getRowCount();
        try {
            if (isConnectedToVPN) {
                timeStart = (double) System.currentTimeMillis();
                isSearchFinished = new AtomicBoolean(false);
                //String where_clause = Gui.textWhereClause.getText();
                if (Gui.isSelect) {
                    Common.notification("auto update data");
                    Statement statement = connect.createStatement();
                    //String sql = "select max(fl_id) from fs_log where fl_date > sysdate - " + where_clause;
                    String sql = "select max(" + column_id + ") from " + logTableFromConfig + " where " + column_date + " > sysdate - 1/1440";
                    ResultSet rs_id = statement.executeQuery(sql);
                    if (rs_id.next()) {
                        max = rs_id.getInt(1);
                    }
                    rs_id.close();
                    statement.close();

                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException var15) {
                        var15.printStackTrace();
                    }

                    Statement statement2 = connect.createStatement();
                    //String sql2 = "select max(fl_id) from fs_log where fl_date > sysdate - " + where_clause;
                    String sql2 = "select max(" + column_id + ") from " + logTableFromConfig + " where " + column_date + " > sysdate - 1/1440";
                    ResultSet rs_id2 = statement2.executeQuery(sql2);
                    if (rs_id2.next()) {
                        max2 = rs_id2.getInt(1);
                    }

                    statement2.close();
                    if (max != max2) {
                        String sqlQuery = Gui.JStatement.getText() + " " + logTableFromConfig + " " + Gui.textWhereClause.getText();
                        //System.out.println("1 " + sqlQuery);
                        PreparedStatement st = connect.prepareStatement(sqlQuery);
                        ResultSet rs = st.executeQuery();

                        while (rs.next()) {
                            if (isStop.get()) {
                                return;
                            }
                            amount = Gui.model.getRowCount();
                            String fl_date = rs.getString(column_date);
                            String fl_text = rs.getString(column_text);
                            int fl_id = rs.getInt(column_id);
                            if (fl_id > max) {
                                Object[] row = new Object[]{amount + 1, fl_date, fl_text};
                                Gui.model.addRow(row);
                            }
                        }
                        st.close();
                        isSearchFinished.set(true);

                        if ((Gui.model.getRowCount() > modelRowCount) && Gui.isGuiInTray.get()) {
                            common.trayMessage();
                        }
                    }
                } else {
                    common.Searching(isSearchFinished);
                    String sqlQuery = Gui.JStatement.getText() + " " + logTableFromConfig + " " + Gui.textWhereClause.getText();
                    PreparedStatement st = connect.prepareStatement(sqlQuery);
                    //System.out.println("2 " + sqlQuery);
                    ResultSet rs = st.executeQuery();

                    while (rs.next()) {
                        if (isStop.get()) {
                            return;
                        }

                        amount = Gui.model.getRowCount() + 1;
                        String fl_date = rs.getString(column_date);
                        String fl_text = rs.getString(column_text);
                        Object[] row = new Object[]{amount, fl_date, fl_text};
                        Gui.model.addRow(row);
                    }
                    st.close();
                    isSearchFinished.set(true);
                    double timeEnd = (double) System.currentTimeMillis();
                    searchTime = (timeEnd - timeStart) / 1000.0D;
                    DecimalFormat f = new DecimalFormat("#0.00");
                    Common.notification("search completed in " + f.format(searchTime) + " s.");
                }
            } else {
                isStop.set(true);
                Common.notification("no connection to VPN");
            }
        } catch (NullPointerException var16) {
            isStop.set(true);
            isSearchFinished.set(true);
            Common.notification("nullPointerException");
        } catch (SQLException var17) {
            isStop.set(true);
            isSearchFinished.set(true);
            Common.notification("VPN connection closed");
        }
    }

    //Список всех таблиц текущего пользователя
    void getUserTables() {
        try {
            PreparedStatement st = connect.prepareStatement("SELECT table_name FROM user_tables ORDER BY table_name");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String tables = rs.getString("table_name");
                user_tables.add(tables);
            }
            rs.close();
            st.close();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    // Анализ данных на уникальность
    synchronized void selectUniqueItems() {
        double selectivity;
        try {
            List<String> columns = new ArrayList<>();
            if (types.size() > 0) types.clear();
            PreparedStatement st = connect.prepareStatement("SELECT column_name, data_type from user_tab_columns WHERE upper(table_name) = '" +
                    Gui.tableNamesBox.getSelectedItem() + "'");
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                column_name = rs.getString("column_name");
                columns.add(column_name);
                data_type = rs.getString("data_type");
                types.add(data_type);
            }
            st.close();

            data_type_count = 0;
            for (String s : columns) {
                if (types.get(data_type_count).equals("BLOB") || types.get(data_type_count).equals("CLOB")) s = null;
                PreparedStatement st_count = connect.prepareStatement("select count(distinct " + s + "), count(" + s + ") from " + Gui.tableNamesBox.getSelectedItem());
                if (s == null) s = headers.get(data_type_count);
                ResultSet rs_count = st_count.executeQuery();

                while (rs_count.next()) {
                    double distinct_rows_count = rs_count.getDouble(1);
                    double all_rows_count = rs_count.getDouble(2);

                    selectivity = distinct_rows_count / all_rows_count;
                    if (distinct_rows_count == 0.0 && all_rows_count == 0.0) selectivity = 0.0;

                    Object[] row = new Object[]{
                            s,
                            types.get(data_type_count),
                            (int) distinct_rows_count,
                            selectivity
                    };
                    Gui.uniqueAnalysisModel.addRow(row);
                    data_type_count++;
                }
                st_count.close();
            }
            isSelectOrGoFinished = true;
            Gui.progressBar.setValue(100);
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    // Выборка имён столбцов и их типов
    synchronized void tableInfo() {
        try {
            if (headers.size() > 0) headers.clear();
            if (types.size() > 0) types.clear();
            Statement st_tab = connect.createStatement();
            String sql_tab = "SELECT * FROM user_tab_columns WHERE table_name = '" + Gui.tableNamesBox.getSelectedItem() + "' order by column_id";
            ResultSet rs_tab = st_tab.executeQuery(sql_tab);
            while (rs_tab.next()) {
                column_name = rs_tab.getString(2);
                headers.add(column_name);
                data_type = rs_tab.getString("data_type");
                types.add(data_type);
                column_count++;

                // Количество символов в максимальном значении столбца
                Statement stColLength = connect.createStatement();
                String sqlColLength = "select max(length(" + column_name + ")) from " + Gui.tableNamesBox.getSelectedItem();
                ResultSet rsColLength = stColLength.executeQuery(sqlColLength);
                while (rsColLength.next()) {
                    colLengths.add(rsColLength.getInt(1));
                }
            }
            st_tab.close();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    // Селект из выбранной таблицы
    synchronized void selectFromTable() {
        try {
            if (isConnectedToVPN) {
                PreparedStatement st = connect.prepareStatement("SELECT t.*, t.rowid FROM " + Gui.tableNamesBox.getSelectedItem()
                        + " t "
                        + Gui.whereSelect.getText().replace("where clause", ""));
                ResultSet rs = st.executeQuery();
                columnCount = rs.getMetaData().getColumnCount() - 1;
                if (blobs.size() > 0) blobs.clear();
                if (row_ids.size() > 0) row_ids.clear();
                while (rs.next()) {
                    if (isStop.get()) {
                        return;
                    }
                    String row_id = rs.getString("ROWID");
                    row_ids.add(row_id);

                    Object[] row = new Object[columnCount];
                    Arrays.setAll(row, x -> {
                        try {
                            switch (Oracle.types.get(x)) {
                                case "NUMBER":
                                case "INTEGER":
                                    return rs.getInt(x + 1);
                                case "BLOB":
                                    if (rs.getBlob(x + 1) == null) {
                                        return "-";
                                    }
                                    return "blob";
                                case "CLOB":
                                    if (rs.getClob(x + 1) == null) {
                                        return "-";
                                    }
                                    return "clob";
                                default:
                                    return rs.getString(x + 1);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    Gui.executeModel.addRow(row);
                }
                st.close();
            } else {
                isStop.set(true);
            }
            Gui.lblWhere.setText("SELECT * FROM " + Gui.tableNamesBox.getSelectedItem() + " (" + Gui.executeModel.getRowCount() + " rows)");
            isSelectOrGoFinished = true;
            Gui.progressBar.setValue(100);
        } catch (SQLException | NullPointerException s) {
            s.printStackTrace();
            isStop.set(true);
        }
    }

    // аэропорт по коду
    String getAirport(String pIata) {
        String result = "";
        if (isConnectedToVPN) {
            try {
                PreparedStatement st = connect.prepareStatement("SELECT s.name_eng, s.city_eng, s.gmt_offset, s.country_eng, a.airp_cntr_id " +
                        "FROM sb_airp_and_city s \n" +
                        "JOIN sb_airp a on a.airp_code = s.iata_code " +
                        "WHERE iata_code = upper('" + pIata + "')");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    String country = rs.getString("COUNTRY_ENG");
                    String city = rs.getString("CITY_ENG");
                    String name_eng = rs.getString("NAME_ENG");
                    String gmt_offset = rs.getString("GMT_OFFSET");
                    String airp_cntr_id = rs.getString("AIRP_CNTR_ID");

                    result = country + " (" + airp_cntr_id + "), " + city + ", " + name_eng + ", " + gmt_offset;
                }
                st.close();
                rs.close();
            } catch (SQLException sql) {
                sql.printStackTrace();
            }
        }
        return result;
    }

    // страна по коду
    String getAirport(int pAirpId) {
        String result = "";
        if (isConnectedToVPN) {
            try {
                PreparedStatement st = connect.prepareStatement("select cntr_name_a, listagg(''''||a.airp_code||'''', ',') " +
                        "WITHIN GROUP (order by a.airp_code) airps\n" +
                        "from sb_cntr s \n" +
                        "join sb_airp a on a.airp_cntr_id = s.cntr_id\n" +
                        "and a.USE_IN_OKPPD = 1\n" +
                        "where s.cntr_id = " + pAirpId + "\n" +
                        "group by cntr_name_a");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    String country = rs.getString(1);
                    String airports = rs.getString(2);

                    result = country;
                    Gui.iataResultLbl.setToolTipText(airports.replace("'", "") + " (airports in clipboard)");

                    // копируем список аэропортов в системный буфер.. для airp_code in ()
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(airports), null);
                }
                st.close();
                rs.close();
            } catch (SQLException sql) {
                sql.printStackTrace();
            }
        }
        return result;
    }
}
