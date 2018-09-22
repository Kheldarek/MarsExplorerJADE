package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SurvivingMarsApp extends Application {

    public void start(Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("SurvivingMarsGui.fxml"));
        final Scene scene = new Scene(root);
        primaryStage.setTitle("Surviving Mars");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
