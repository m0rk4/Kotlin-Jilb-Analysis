package by.mark;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
/*
   Created by Mark Putsiata.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/gui.fxml"));
        loader.setControllerFactory(c -> new AppController(new Model(), stage));
        BorderPane root = loader.load();
        Rectangle2D screen = Screen.getPrimary().getBounds();
        BackgroundImage myBI = new BackgroundImage(
                new Image(getClass().getResource("images/bg_kotlin.png").toExternalForm(),
                        screen.getWidth(), screen.getHeight(), false, false),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));
        Scene scene = new Scene(root);
        stage.setTitle("KTJilbAnalyzer");
        stage.getIcons().add(new Image(getClass().getResource("images/ktLogo.png").toExternalForm()));
        stage.setScene(scene);
        stage.setMinWidth(815);
        stage.setMinHeight(640.0);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}