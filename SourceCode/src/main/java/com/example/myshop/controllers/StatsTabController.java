package com.example.myshop.controllers;

import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;


import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;


import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
public class StatsTabController implements Initializable {
    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private HBox datePickerHBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Button okBtn;

    @FXML
    private Label revenue;

    @FXML
    private DatePicker toDatePicker;
    @FXML
    private AreaChart<String, Number> incomeChart;
    @FXML
    private CategoryAxis yAxis;
    @FXML
    private CategoryAxis xAxis;

    @FXML
    private CategoryAxis incomeChartXAxis;
    @FXML
    private PieChart pizzaPieChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());
        incomeChartXAxis.setAnimated(false);
        comboBox.setStyle("-fx-text-fill: #AD8B4A;");
        //ComboBox Thống kê Doanh thu
        comboBox.setItems(FXCollections.observableArrayList("Tất cả", "Tùy chọn","Hôm nay", "Hôm qua", "7 ngày gần đây", "Năm nay"));
        comboBox.setOnAction(event -> {
            if (comboBox.getSelectionModel().getSelectedIndex() == 0) {
                loadRevenueAllTime();
                loadLineChartAllTime("Tất cả");
                loadTopPizzaChartAllTime();
                datePickerHBox.setVisible(false);
            } else if (comboBox.getSelectionModel().getSelectedIndex() == 1) {
                datePickerHBox.setVisible(true);
            } else if (comboBox.getSelectionModel().getSelectedIndex() == 5) { // "This Year" selected
                datePickerHBox.setVisible(false);

                // Calculate and load monthly revenue for "This Year"

                loadRevenueThisYear();

                // Run UI updates on the JavaFX Application Thread
                Platform.runLater(() -> {
                    loadLineChartMonthlyThisYear();
                    loadTopPizzaChartFromTimeToTime(LocalDate.now(), LocalDate.now());
                });
            } else {
                // Handle Today, Yesterday, The Last 7 Days
                datePickerHBox.setVisible(false);

                LocalDate toDate;
                LocalDate fromDate;

                String selectedOption = comboBox.getSelectionModel().getSelectedItem();

                if (selectedOption.equals("Hôm nay")) {
                    toDate = LocalDate.now();
                    fromDate = toDate;
                } else if (selectedOption.equals("Hôm qua")) {
                    System.out.println("Selected Option: Hôm qua");
                    toDate = LocalDate.now().minusDays(1);
                    fromDate = toDate;
                } else if (selectedOption.equals("Năm nay")) {
                    // Handle "This Year"
                    toDate = LocalDate.now();
                    fromDate = LocalDate.of(toDate.getYear(), 1, 1);

                } else {
                    // Handle other cases
                    toDate = LocalDate.now();
                    fromDate = toDate.minusDays(6);
                }

                // Update revenue and chart for the selected time range
                loadRevenueFromDateToDate(fromDate, toDate);
                loadTopPizzaChartFromTimeToTime(fromDate, toDate);



                // Run UI updates on  the JavaFX Application Thread
                Platform.runLater(() -> {
                    loadLineChartFromTimeToTime(fromDate, toDate, selectedOption);

                    //loadTopPizzaChartFromTimeToTime(fromDate, toDate);
                });
            }
        });


        okBtn.setOnAction(event -> {
            loadRevenueFromDateToDate(fromDatePicker.getValue(), toDatePicker.getValue());
            loadTopPizzaChartFromTimeToTime(fromDatePicker.getValue(), toDatePicker.getValue());


            // Run UI updates on the JavaFX Application Thread
            Platform.runLater(() -> {
                loadLineChartFromTimeToTime(fromDatePicker.getValue(), toDatePicker.getValue(),"Tùy chọn");

            });


        });

        comboBox.getSelectionModel().select(0);
        loadRevenueAllTime();
        loadLineChartAllTime("Tất cả");

        loadTopPizzaChartAllTime();
    }



    //Doanh thu
    void loadRevenueAllTime() {
        revenue.setText(getRevenue() + "");
    }
    void loadRevenueThisYear() {
        long revenueThisYear = getRevenueThisYear();
        revenue.setText(revenueThisYear + "");
    }
    void loadRevenueFromDateToDate(LocalDate from, LocalDate to) {
        revenue.setText(getRevenueFromDateToDate(from, to) + "");
    }

    //Load Chart
    void loadLineChartFromTimeToTime(LocalDate from, LocalDate to, String seriesName) {
        XYChart.Series<String, Number> chartData = getlineChartFromTimeToTime(from, to);
        chartData.setName(seriesName);
        // Introduce a small delay before updating the chart
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> {
                incomeChart.getData().clear();
                incomeChart.getData().add(chartData);
            });
        });
        pause.play();

    }
    void loadLineChartAllTime(String seriesName) {
        XYChart.Series<String, Number> chartData = getlineChartAllTime();
        chartData.setName(seriesName);
        incomeChart.getData().clear(); // Clear existing data
        incomeChart.getData().add(chartData); // Set the new chart data
        Node chartPlotArea = incomeChart.lookup(".chart-plot-background");
        Color backgroundColor = Color.web("#F7F3EF");  // Set the desired color
        String rgbBackground = String.format("%d, %d, %d",
                (int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255));
        chartPlotArea.setStyle("-fx-background-color: rgba(" + rgbBackground + ", 1.0);");

    }
    void loadMonthlyRevenueThisYear() {
        LocalDate currentDate = LocalDate.now();
        LocalDate yearStartDate = LocalDate.of(currentDate.getYear(), 1, 1);

        // Assuming you have a method to calculate monthly revenue similar to your existing methods
        // Modify this method based on your specific data structure
        loadMonthlyRevenueFromDateToDate(yearStartDate, currentDate);
    }

    // Modify this method to handle monthly revenue for "This Year"
    void loadMonthlyRevenueFromDateToDate(LocalDate from, LocalDate to) {
        revenue.setText(getMonthlyRevenueFromDateToDate(from, to) + "");
    }
    void loadLineChartMonthlyThisYear() {
        XYChart.Series<String, Number> chartData = getLineChartMonthlyThisYear();
        chartData.setName("Năm nay");

        // Introduce a small delay before updating the chart
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> {
                incomeChart.getData().clear();
                incomeChart.getData().add(chartData);
            });
        });
        pause.play();
    }
    void loadTopPizzaChartAllTime() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT products.productName, SUM(orderdetails.quantity) AS quantity\n" +
                    "FROM orderdetails\n" +
                    "JOIN products ON orderdetails.productID = products.productID\n" +
                    "WHERE products.categoryID != 4\n" +
                    "GROUP BY products.productName\n" +
                    "ORDER BY quantity DESC\n" +
                    "LIMIT 5;";

            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                String pizzaName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                pieChartData.add(new PieChart.Data(pizzaName, quantity));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        pizzaPieChart.setData(pieChartData);
    }
    void loadTopPizzaChartFromTimeToTime(LocalDate from, LocalDate to) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            MySQLConnector connector = MySQLConnector.getInstance();

            // Get the selected top pizza criteria from the ComboBox

            // Modify your SQL query based on the selected criteria and date range
            String sql = "SELECT products.productName, SUM(orderdetails.quantity) AS quantity\n" +
                    "FROM orderdetails\n" +
                    "JOIN products ON orderdetails.productID = products.productID\n" +
                    "WHERE products.categoryID != 4\n" +
                    "AND orderdetails.orderID IN (SELECT orders.orderID FROM orders WHERE orders.DateTime >= '" +
                    from.atStartOfDay() + "' AND orders.DateTime <=  '" + to.atTime(23, 59, 59) + "')\n" +
                    "GROUP BY products.productName\n" +
                    "ORDER BY quantity DESC\n" +
                    "LIMIT 5;";

            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                String pizzaName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                pieChartData.add(new PieChart.Data(pizzaName, quantity));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        pizzaPieChart.setData(pieChartData);
    }

    private long getRevenue() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "select Sum(orders.TotalPrice)\n" +
                    "from orders where StatusID =3";
            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long revenue = resultSet.getLong(1);
                return revenue;
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    private long getRevenueFromDateToDate(LocalDate from, LocalDate to) {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT Sum(orders.TotalPrice)\n" +
                    "FROM orders " +
                    "WHERE orders.DateTime >= '" + from.atStartOfDay() + "' AND orders.DateTime <=  '" + to.atTime(23, 59, 59) + "'" +
                    "AND orders.StatusID = 3 ";
            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long revenue = resultSet.getLong(1);
                return revenue;
            }
        } catch (Exception ex) {

        }
        return 0;
    }

    private XYChart.Series<String, Number> getlineChartAllTime() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("2023");


        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT DATE_FORMAT(orders.DateTime, '%Y-%m-%d') AS Month, SUM(orders.TotalPrice) AS TotalPriceSum " +
                    "FROM orders " +
                    "where orders.StatusID = 3 " +
                    "GROUP BY Month " +
                    "ORDER BY Month";

            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                String DateTime = resultSet.getString("Month");
                int totalPriceSum = resultSet.getInt("TotalPriceSum");
                series.getData().add(new XYChart.Data(DateTime, totalPriceSum));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return series; // Return the XYChart.Series
    }

    private XYChart.Series<String, Number> getlineChartFromTimeToTime(LocalDate from, LocalDate to) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("2023");

        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT DATE_FORMAT(orders.DateTime, '%Y-%m-%d') AS Date, SUM(orders.TotalPrice) AS TotalPriceSum " +
                    "FROM orders " +
                    "WHERE orders.DateTime >= '" + from.atStartOfDay() + "' AND orders.DateTime <=  '" + to.atTime(23, 59, 59) + "'" +
                    "AND orders.StatusID = 3 " +
                    "GROUP BY Date " +
                    "ORDER BY Date";
            System.out.println("SQL Query: " + sql);



            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                String DateTime = resultSet.getString("Date");
                int totalPriceSum = resultSet.getInt("TotalPriceSum");
                series.getData().add(new XYChart.Data<>(DateTime, totalPriceSum));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return series;
    }
    private long getMonthlyRevenueFromDateToDate(LocalDate from, LocalDate to) {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT DATE_FORMAT(orders.DateTime, '%Y-%m') AS Month, SUM(orders.TotalPrice) AS TotalPriceSum " +
                    "FROM orders " +
                    "WHERE YEAR(orders.DateTime) = YEAR(CURDATE()) AND orders.StatusID = 3 " +
                    "GROUP BY Month " +
                    "ORDER BY Month";

            ResultSet resultSet = connector.queryResults(sql);

            if (resultSet.next()) {
                long revenue = resultSet.getLong(2); // Assuming TotalPriceSum is in the second column
                return revenue;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return 0;
    }
    private long getRevenueThisYear() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT SUM(orders.TotalPrice) " +
                    "FROM orders " +
                    "WHERE YEAR(orders.DateTime) = YEAR(CURDATE()) AND orders.StatusID = 3";

            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long revenue = resultSet.getLong(1);
                return revenue;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return 0;
    }

    private XYChart.Series<String, Number> getLineChartMonthlyThisYear() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT DATE_FORMAT(orders.DateTime, '%Y-%m') AS Month, SUM(orders.TotalPrice) AS TotalPriceSum " +
                    "FROM orders " +
                    "WHERE YEAR(orders.DateTime) = YEAR(CURDATE()) AND orders.StatusID = 3 " +
                    "GROUP BY Month " +
                    "ORDER BY Month";

            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                String month = resultSet.getString("Month");
                int totalPriceSum = resultSet.getInt("TotalPriceSum");
                series.getData().add(new XYChart.Data<>(month, totalPriceSum));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return series;
    }

}

