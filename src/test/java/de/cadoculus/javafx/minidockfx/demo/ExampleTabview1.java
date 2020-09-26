package de.cadoculus.javafx.minidockfx.demo;

import com.jfoenix.controls.JFXCheckBox;
import de.cadoculus.javafx.minidockfx.AbstractTabableView;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


/**
 * THis is just an example to show what it is possible with the AbstractTabbableView.
 */
public class ExampleTabview1 extends AbstractTabableView {


    public ExampleTabview1(String name, String id, Color color) {
        super(name, id, true, true );
        content = new BorderPane();

        content.getStyleClass().add("ExampleView1");

        Color start = color.brighter().desaturate();
        Stop[] stops = new Stop[]{new Stop(0, start), new Stop(1, color)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        content.setBackground(new Background(new BackgroundFill(lg1, CornerRadii.EMPTY, Insets.EMPTY)));

        content.getStyleClass().add("exampleTabview1");
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        ((BorderPane) content).setCenter(vbox);

        vbox.getChildren().add(new Label("content " + name));

        JFXCheckBox closeCB = new JFXCheckBox("closeable");
        closeCB.selectedProperty().bindBidirectional(closeable);
        vbox.getChildren().add(closeCB);

        JFXCheckBox moveCB = new JFXCheckBox("moveable");
        moveCB.selectedProperty().bindBidirectional(moveable);
        vbox.getChildren().add(moveCB);

    }

    @Override
    public void beforeAdding() {

    }

    @Override
    public void afterAdding() {

    }

    @Override
    public void beforeClose() {

    }

    @Override
    public void afterClose() {

    }
}
