package de.cadoculus.javafx.minidockfx.demo;

import de.cadoculus.javafx.minidockfx.MiniDockFXPane;
import de.cadoculus.javafx.minidockfx.MiniDockTabPosition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MiniDockFXPane.class);

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        BorderPane root = new BorderPane();
        MiniDockFXPane mdf = null;
        try {
            FXMLLoader loader = new FXMLLoader(MiniDockFXPane.class.getResource("DefaultDock.fxml"));
            AnchorPane ap = loader.load();
            mdf = (MiniDockFXPane) loader.getController();

            root.setCenter(ap);
        } catch (Exception exp) {
            LOG.error("failed to load fxml", exp);
        }

        Scene scene = new Scene(root, 600, 500);

        primaryStage.setTitle("MiniDockFX Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

        final ExampleTabview1 left = new ExampleTabview1("left");
//        final ExampleTabview1 left2 = new ExampleTabview1("left2");
//        final ExampleTabview1 center = new ExampleTabview1("center");
//        final ExampleTabview1 right = new ExampleTabview1("right");
//        final ExampleTabview1 bottom = new ExampleTabview1("bottom");
//
        mdf.add(left, MiniDockTabPosition.LEFT);
//        mdf.add(left2, MiniDockTabPosition.LEFT);
//        mdf.add(center, MiniDockTabPosition.CENTER);
//        mdf.add(right, MiniDockTabPosition.RIGHT);
//        mdf.add(bottom, MiniDockTabPosition.BOTTOM);

    }

    public static void main(String[] args) {
        launch(args);
    }
}

