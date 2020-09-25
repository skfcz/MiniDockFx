package de.cadoculus.javafx.minidockfx.demo;

import de.cadoculus.javafx.minidockfx.AbstractTabbableView;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


/**
 * THis is just an example to show what it is possible with the AbstractTabbableView.
 */
public class ExampleTabview1 extends AbstractTabbableView {


    public ExampleTabview1(String name, Color color) {
        super(name, true, true);
        content = new BorderPane();

        Color start = color.brighter().desaturate();
        Stop[] stops = new Stop[]{new Stop(0, start), new Stop(1, color)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        content.setBackground(new Background(new BackgroundFill(lg1, CornerRadii.EMPTY, Insets.EMPTY)));

        content.getStyleClass().add("exampleTabview1");
        ((BorderPane) content).setCenter(new Label("content " + name));
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
