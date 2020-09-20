package de.cadoculus.javafx.minidockfx;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import java.util.Optional;


public class TabbedDockController {


    @FXML
    private JFXTabPane tabbedDockTabpane;

    @FXML
    public void initialize() {

    }

    public void add(AbstractTabbableView view) {

        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        Tab tab = new Tab();
        tab.setContent(view.getContent());
        tab.setGraphic(view.getTab());
        tab.setUserData(view);

        tabbedDockTabpane.getTabs().add(tab);
    }

    public void remove(AbstractTabbableView view) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        final Optional<Tab> first = tabbedDockTabpane.getTabs().stream().filter(tab -> view.equals(tab.getUserData())).findFirst();

        if (first.isEmpty()) {
            throw new IllegalArgumentException("could not find tab for view " + view);
        }
        tabbedDockTabpane.getTabs().remove( first.get());
    }
}