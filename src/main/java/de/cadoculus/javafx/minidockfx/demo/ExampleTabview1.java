package de.cadoculus.javafx.minidockfx.demo;

import de.cadoculus.javafx.minidockfx.AbstractTabbableView;
import javafx.scene.control.Label;

public class ExampleTabview1 extends AbstractTabbableView {


    public ExampleTabview1(String name) {
        super(name, true, true);
        content = new Label("content " + name);

    }
}
