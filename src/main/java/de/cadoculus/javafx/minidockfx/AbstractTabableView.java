package de.cadoculus.javafx.minidockfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * This is the base class to be extended for views
 */
public abstract class AbstractTabableView {

    protected final StringProperty name = new SimpleStringProperty();
    protected final HBox tab;
    protected final BooleanProperty closeable = new SimpleBooleanProperty();
    protected final BooleanProperty moveable = new SimpleBooleanProperty();
    protected final BooleanProperty docked = new SimpleBooleanProperty();
    protected final String id;
    protected Region content;

    /**
     * The default constructor
     * @param name     the initial view name. Use {@link #name} to change it at runtime
     * @param id the id used to store position in preferences. No storage used if id is empty or null.
     * @param canClose true if the view is closeable. If false no button is added to the tab.
     * @param canMove  true if the view is moveable. If false it is not possible to move the tab
     */
    protected AbstractTabableView(String name, String id, boolean canClose, boolean canMove) {

        this.id = id;
        this.name.setValue(name);
        this.closeable.setValue(canClose);
        this.moveable.setValue(canMove);

        tab = new HBox();
        tab.getStyleClass().add(MiniDockFXPane.VIEW_BOX_STYLE);
        tab.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label();
        label.getStyleClass().add(MiniDockFXPane.VIEW_LABEL_STYLE);
        label.textProperty().bind(this.name);
        tab.getChildren().add(label);

        // in case the title becomes too long we add a tooltip
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(this.name);
        tooltip.getStyleClass().add(MiniDockFXPane.VIEW_LABEL_TT_STYLE);
        label.setTooltip(tooltip);
    }

    /**
     * Get the part to be displayed in the tab,
     * e.g. the name, some toolicons ...
     */
    public Node getTab() {
        return tab;
    }

    public Region getContent() {
        return content;
    }

    /**
     * This method is called before a view is added.
     * You could override it if you need that information.
     */
    public void beforeAdding() {

    }

    /**
     * This method is called after a view was added.
     * You could override it if you need that information.
     */
    public void afterAdding() {

    }

    /**
     * This method is called before a view is added.
     * You could override it if you need that information.
     */
    public void beforeClose() {

    }

    /**
     * This method is called after a view was added.
     * You could override it if you need that information.
     */
    public void afterClose() {

    }


}
