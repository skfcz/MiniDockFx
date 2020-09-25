package de.cadoculus.javafx.minidockfx;

import com.jfoenix.controls.JFXTabPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class TabbedDockController {

    private static final Logger LOG = LoggerFactory.getLogger(TabbedDockController.class);
    private MiniDockFXPane dock;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    public void initialize() {
    }

    final ObservableList<AbstractTabbableView> views = FXCollections.observableArrayList();


    void setDock(MiniDockFXPane dock) {
        this.dock = dock;
        ListChangeListener lcl = change -> {
            dock.updateLayout();
        };
        views.addListener(lcl);
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
                map(Button.class::cast).
                filter(c -> c.getStyleClass().contains(AbstractTabbableView.CLOSE_BUTTON_STYLE)).
                findFirst();
        if (closeO.isPresent()) {
            closeO.get().setOnAction(actionEvent -> TabbedDockController.this.remove(view));
        }


        tabPane.getTabs().add(tab);
        view.afterAdding();
        views.add(view);


        // Add mouse event handlers for the source
        tab.getGraphic().setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tab.getGraphic().setMouseTransparent(true);
                event.setDragDetect(true);
                dock.dragStart(view, event);
            }
        });

        tab.getGraphic().setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tab.getGraphic().setMouseTransparent(false);
                dock.dragStart(view, event);
            }
        });

        tab.getGraphic().setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                event.setDragDetect(false);
                dock.dragStart(view, event);
            }
        });

        tab.getGraphic().setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                tab.getGraphic().startFullDrag();
                dock.dragStart(view, event);
            }
        });
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
        tabPane.getTabs().remove(first.get());
        view.afterClose();
        views.remove(view);

    }

    public void raise(AbstractTabbableView view) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        final Optional<Tab> first = tabPane.getTabs().stream().filter(tab -> view.equals(tab.getUserData())).findFirst();

        if (first.isEmpty()) {
            throw new IllegalArgumentException("could not find tab for view " + view);
        }
        tabPane.getSelectionModel().select(first.get());
    }
}