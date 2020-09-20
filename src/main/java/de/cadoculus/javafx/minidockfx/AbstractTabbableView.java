package de.cadoculus.javafx.minidockfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class AbstractTabbableView {

    protected final StringProperty name = new SimpleStringProperty();
    protected final VBox tab;
    protected final BooleanProperty closeable = new SimpleBooleanProperty();
    protected final BooleanProperty moveable = new SimpleBooleanProperty();
    protected Region content;

    protected AbstractTabbableView(String name, boolean canClose, boolean canMove) {

        this.name.setValue(name);
        this.closeable.setValue(canClose);
        this.moveable.setValue(canMove);

        tab = new VBox();
        tab.getStyleClass().add("minidockfx-tab-box");

        Label label = new Label();
        label.getStyleClass().add("minidockfx-tab-label");
        label.textProperty().bind(this.name);
        tab.getChildren().add(label);

        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("minidockfx-tab-close");
        closeButton.visibleProperty().bind(closeable);

        tab.getChildren().add( closeButton);
    }

    public VBox getTab() {
        return tab;
    }

    public Region getContent() {
        return content;
    }


}
