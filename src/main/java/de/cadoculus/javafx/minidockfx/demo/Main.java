package de.cadoculus.javafx.minidockfx.demo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.cadoculus.javafx.minidockfx.MiniDockFXPane;
import de.cadoculus.javafx.minidockfx.MiniDockTabPosition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.time.LocalTime;
import java.util.Random;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MiniDockFXPane.class);
    // Simply the 200 line down on https://material.io/resources/color
    public static final    String[] MATERIAL_DESIGN_COLORS = new String[]{
            "#ef9a9a", "#f48fb1", "#ce93d8", "#b39ddb", "#9fa8da",
            "#90caf9", "#81d4fa", "#80deea", "#80cbc4", "#a5d6a7",
            "#c5e1a5", "#e6ee9c", "#fff59d", "#ffe082", "#ffcc80",
            "#ffab91", "#bcaaa4", "#eeeeee", "#b0bec5"
    };
    private final Random random = new Random(42L);


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

        MenuBar menubar = new MenuBar();
        root.setTop(menubar);


        final MiniDockFXPane mdf;
        try {
            FXMLLoader loader = new FXMLLoader(MiniDockFXPane.class.getResource("DefaultDock.fxml"));
            AnchorPane ap = loader.load();
            mdf = (MiniDockFXPane) loader.getController();

            root.setCenter(ap);
        } catch (Exception exp) {
            LOG.error("failed to load fxml", exp);
            throw new IllegalStateException("failed to load MiniDockFXPane");
        }

        Menu menu = new Menu("Tabs");
        menubar.getMenus().add(menu);


        for (MiniDockTabPosition pos : MiniDockTabPosition.values()) {
            if (MiniDockTabPosition.PREFERENCES == pos) {
                continue;
            }
            MenuItem mi = new MenuItem("add to " + pos);
            menu.getItems().add(mi);
            mi.setOnAction(actionEvent -> {

                ExampleTabview1 etv = new ExampleTabview1("Tab " + pos + " " + LocalTime.now(), nextColor());
                mdf.add(etv, pos);
            });
        }


        Scene scene = new Scene(root, 600, 500);

        primaryStage.setTitle("MiniDockFX Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

        final ExampleTabview1 left = new ExampleTabview1("left", nextColor());
        final ExampleTabview1 left2 = new ExampleTabview1("left2", nextColor());
        final ExampleTabview1 center = new ExampleTabview1("center", nextColor());
//        final ExampleTabview1 right = new ExampleTabview1("right");
        final ExampleTabview1 bottom = new ExampleTabview1("bottom", nextColor());
//
        mdf.add(left, MiniDockTabPosition.LEFT);
        mdf.add(left2, MiniDockTabPosition.LEFT);
        mdf.add(center, MiniDockTabPosition.CENTER);
//        mdf.add(right, MiniDockTabPosition.RIGHT);
        mdf.add(bottom, MiniDockTabPosition.BOTTOM);

    }
    private Color nextColor() {
        String color = MATERIAL_DESIGN_COLORS[random.nextInt( MATERIAL_DESIGN_COLORS.length -1)];
        return Color.web(color);
    }

    public static void main(String[] args) {

        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            URL url = Main.class.getClassLoader().getResource("logback.xml");
            System.out.println("url " + url);

            configurator.doConfigure(url);

            LOG.error("test error");
            LOG.warn("test warn");
            LOG.info("test info");
        } catch (JoranException je) {

            // StatusPrinter will handle this
        }

        launch(args);
    }
}

