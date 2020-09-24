package de.cadoculus.javafx.minidockfx;

import com.jfoenix.controls.JFXButton;
import com.sun.javafx.css.FontFaceImpl;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public abstract class AbstractTabbableView {


    public static final String CLOSE_BUTTON_STYLE = "minidockfx-tab-close";
    public static final String TAB_LABEL_STYLE = "minidockfx-tab-label";
    public static final String TAB_BOX_STYLE = "minidockfx-tab-box";

    protected final StringProperty name = new SimpleStringProperty();
    protected final HBox tab;
    protected final BooleanProperty closeable = new SimpleBooleanProperty();
    protected final BooleanProperty moveable = new SimpleBooleanProperty();
    protected Region content;
    protected boolean added=false;

    protected AbstractTabbableView(String name, boolean canClose, boolean canMove) {

        this.name.setValue(name);
        this.closeable.setValue(canClose);
        this.moveable.setValue(canMove);

        tab = new HBox();
        tab.getStyleClass().add(TAB_BOX_STYLE);
        tab.setAlignment( Pos.CENTER_LEFT);

        Label label = new Label();
        label.getStyleClass().add(TAB_LABEL_STYLE);
        label.textProperty().bind(this.name);
        tab.getChildren().add(label);

        JFXButton closeButton = new JFXButton();
        closeButton.getStyleClass().add(CLOSE_BUTTON_STYLE);
        closeButton.visibleProperty().bind(closeable);
        FontIcon icon = new FontIcon("fa-close");

        closeButton.setGraphic(icon);

        tab.getChildren().add(closeButton);
    }

    public HBox getTab() {
        return tab;
    }

    public Region getContent() {
        return content;
    }

    public abstract void beforeAdding();

    public abstract void afterAdding();


    public abstract void beforeClose();

    public abstract void afterClose();

    public void setAdded(boolean b) {
        this.added = b;
    }



}
