package BTC_PriceTracker;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BTCController implements Initializable {

    @FXML private LineChart<Number, Number> lineChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Label currentValueLabel;


    // Initialize Method
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lineChart.setTitle("BTC Price Tracker");
        lineChart.getYAxis().setLabel("Price ($USD)");
        lineChart.setLegendVisible(false);
        lineChart.setPrefSize(800, 500);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

    }

    // Button Event Handlers
    public void loadDay(){
        CompletableFuture<ObservableList<BTCData>> future = new CompletableFuture<>();
        future.supplyAsync(() -> this.asyncLoadData("day")).thenApply(this::prepareSeries).thenAccept(this::finalDraw);
    }

    public void loadHour(){
        CompletableFuture<ObservableList<BTCData>> future = new CompletableFuture<>();
        future.supplyAsync(() -> this.asyncLoadData("hour")).thenApply(this::prepareSeries).thenAccept(this::finalDraw);
    }

    public void loadMinute(){
        CompletableFuture<ObservableList<BTCData>> future = new CompletableFuture<>();
        future.supplyAsync(() -> this.asyncLoadData("minute")).thenApply(this::prepareSeries).thenAccept(this::finalDraw);
    }


    public ObservableList<BTCData> asyncLoadData(String t){

        ObservableList<BTCData> values = BTCData.getData(t);

        ArrayList<Float> potentialMinMax = new ArrayList<>();
        Float tolerance = 0f;

        for (BTCData b: values){
            potentialMinMax.add(b.getOpen());
        }

        // set yAxis tolerance
        switch (t) {
            case "day":
                tolerance = 500f;
                break;
            case "hour":
                tolerance = 100f;
                break;
            case "minute":
                tolerance = 25f;
                break;
        }

        // Dynamically change y-axis values
        float yMax = Collections.max(potentialMinMax) + tolerance;
        float yMin = Collections.min(potentialMinMax) - tolerance;
        String currentValue = "$" + potentialMinMax.get(potentialMinMax.size()-1).toString();

        // Set Axis Titles After asynchronously loading the data
        Platform.runLater(() -> {

            if (t.equals("day")){
                xAxis.setLabel("Days");
                xAxis.setUpperBound(30);
            }
            if (t.equals("hour")){
                xAxis.setLabel("Hours");
                xAxis.setUpperBound(24);
            }
            if (t.equals("minute")){
                xAxis.setLabel("Minutes");
                xAxis.setUpperBound(60);
            }

            xAxis.setLowerBound(0);
            xAxis.setTickUnit(1);
            yAxis.setUpperBound(yMax);
            yAxis.setLowerBound(yMin);
            yAxis.setTickUnit((yMax - yMin) / 6);
            currentValueLabel.setText(currentValue);
        });

        return values;
    }

    // Setup LineChart Values
    public XYChart.Series<Number, Number> prepareSeries(ObservableList<BTCData> values){

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (BTCData d: values){
            Integer time = d.getTimeStamp(); // grab time from BTCData object
            Float open = d.getOpen();        // grab open price from BTCData object

            System.out.println("(" + time + ", $" + open + ")");

            series.getData().add(new XYChart.Data<>(time, open)); // add a new datapoint to series
        }

        return series;
    }


    //                                   time    open
    public void finalDraw(XYChart.Series<Number, Number> chartData){
        // Add the Series to line chart on the main thread at a later time
        Platform.runLater(() -> {
            lineChart.getData().setAll(chartData);
        });
    }

}
