package BTC_PriceTracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BTCTrackerFXML.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600); // Load Controller Class
        primaryStage.setTitle("BTC Price Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
