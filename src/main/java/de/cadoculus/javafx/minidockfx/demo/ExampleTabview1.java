package de.cadoculus.javafx.minidockfx.demo;

import de.cadoculus.javafx.minidockfx.AbstractTabbableView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class ExampleTabview1 extends AbstractTabbableView {


    public ExampleTabview1(String name) {
        super(name, true, true);
        content = new BorderPane();

        content.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,
                CornerRadii.EMPTY, Insets.EMPTY)));

        content.getStyleClass().add("exampleTabview1");
        ((BorderPane) content).setCenter(

                new Label("content " + name));
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
