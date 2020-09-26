/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2020, Carsten Zerbst
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.cadoculus.javafx.minidockfx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * This is the controller for a single dock.
 * From the UI side it is based on the layout defined in TabbedDock.fxml
 */
public class TabbedDockController {

    private static final Logger LOG = LoggerFactory.getLogger(TabbedDockController.class);
    private MiniDockFXPane dock;
    final ObservableList<AbstractTabableView> views = FXCollections.observableArrayList();

    @FXML
    private JFXTabPane tabPane;

    @FXML
    public void initialize() {
    }

    /**
     * This method is used to register the parent {@link MiniDockFXPane}
     * @param dock the parent dock
     */
    void setDock(MiniDockFXPane dock) {
        this.dock = dock;
        ListChangeListener lcl = change -> {
            dock.updateLayout();
        };
        views.addListener(lcl);
    }

    /**
     * Add a new view to this dock
     * @param view the view to add
     */
    void add(AbstractTabableView view) {

        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }

        // Create the header visible in the tab
        HBox header = new HBox();
        header.getStyleClass().add(MiniDockFXPane.TAB_HEADER_STYLE);
        header.setAlignment(Pos.CENTER_LEFT);

        // ... add the header content from the view itself
        header.getChildren().add( view.getTab());

        // ... add a button to close the tab
        JFXButton closeButton = new JFXButton();
        closeButton.getStyleClass().add(MiniDockFXPane.CLOSE_BUTTON_STYLE);
        closeButton.visibleProperty().bind(view.closeable);
        closeButton.setOnAction(actionEvent -> this.remove(view));
        FontIcon icon = new FontIcon("fa-close");
        closeButton.setGraphic(icon);
        header.getChildren().add(closeButton);

        // inform the view
        view.beforeAdding();

        // add to tabbed pane
        Tab tab = new Tab();
        tab.setContent(view.getContent());
        tab.setGraphic(header);
        tab.setUserData(view);

        tabPane.getTabs().add(tab);
        views.add(view);

        view.afterAdding();


        LOG.info("mouse listener {}", tab.getGraphic().getOnMousePressed());

        // Add mouse event handlers for the drag source
        tab.getGraphic().setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if ( ! view.moveable.get()) {
                    event.consume();
                    return;
                }

                tab.getGraphic().setMouseTransparent(true);
                event.setDragDetect(true);
                dock.dragStart(view, event);
            }
        });

        LOG.info("mouse listener' {}", tab.getGraphic().getOnMousePressed());

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

    /**
     * Remove a view from the dock
     * @param view the view to remove
     */
    void remove(AbstractTabableView view) {
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

    /**
     * Raise a view in the dock
     * @param view the view to raise
     */
    void raise(AbstractTabableView view) {
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