package de.cadoculus.javafx.minidockfx;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;

import java.util.Optional;


public class TabbedDockController {


    @FXML
    private JFXTabPane tabPane;

    @FXML
    public void initialize() {

    }

    public void add(AbstractTabbableView view) {

        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }

        view.beforeAdding();

        Tab tab = new Tab();
        tab.setContent(view.getContent());
        tab.setGraphic(view.getTab());
        tab.setUserData(view);

        // try to find close button and add event handler
        final Optional<Button> closeO = view.getTab().getChildren().stream().filter(c -> c instanceof Button).
                map( Button.class::cast).
                filter(c -> c.getStyleClass().contains(AbstractTabbableView.CLOSE_BUTTON_STYLE)).
                findFirst();
        if ( closeO.isPresent()) {
            closeO.get().setOnAction(actionEvent -> TabbedDockController.this.remove(view));
        }

        tabPane.getTabs().add(tab);

        view.afterAdding();
    }

    public void remove(AbstractTabbableView view) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        final Optional<Tab> first = tabPane.getTabs().stream().filter(tab -> view.equals(tab.getUserData())).findFirst();

        if (first.isEmpty()) {
            throw new IllegalArgumentException("could not find tab for view " + view);
        }

        view.beforeClose();
        tabPane.getTabs().remove( first.get());
        view.afterClose();

    }
}