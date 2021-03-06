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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * This is the controller for a single dock.
 * From the UI side it is based on the layout defined in TabbedDock.fxml
 */
public class TabbedDockController {

    private static final Logger LOG = LoggerFactory.getLogger(TabbedDockController.class);
    private MiniDockFXPane dock;
    final ObservableList<DockableView> views = FXCollections.observableArrayList();
    private final ScheduledThreadPoolExecutor executor;
    private ScheduledFuture<?> scheduledFuture;
    private boolean dragFlag;


    @FXML
    private JFXTabPane tabPane;

    public TabbedDockController() {
        executor = new ScheduledThreadPoolExecutor(1);
        executor.setRemoveOnCancelPolicy(true);
    }


    @FXML
    public void initialize() {
    }

    /**
     * This method is used to register the parent {@link MiniDockFXPane}
     *
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
     *
     * @param view the view to add
     */
    void add(DockableView view) {

        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }

        // Create the header visible in the tab
        HBox header = new HBox();
        header.getStyleClass().add(MiniDockFXPane.TAB_HEADER_STYLE);
        header.setAlignment(Pos.CENTER_LEFT);

        // ... add the header content from the view itself
        header.getChildren().add(view.getTab());

        // ... add a button to close the tab
        JFXButton closeButton = new JFXButton();
        closeButton.getStyleClass().add(MiniDockFXPane.CLOSE_BUTTON_STYLE);
        closeButton.visibleProperty().bind(view.closeable());
        closeButton.managedProperty().bind(view.closeable());
        closeButton.setOnAction(actionEvent -> dock.remove(view));
        FontIcon icon = new FontIcon("fa-close");
        closeButton.setGraphic(icon);
        header.getChildren().add(closeButton);

        // ... add a context menu
        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("minidockfx-context-menu");
        menu.getItems().add(new MenuItem("dummy"));
        menu.setOnShowing(windowEvent -> {
            updateMenu(menu, view);
        });
        header.setOnContextMenuRequested(contextMenuEvent -> menu.show(header, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));

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

        Node mouseArea = tab.getGraphic();

        // Add mouse event handlers for dragging and maximising
        mouseArea.setOnMousePressed(event -> {
            if (!event.isPrimaryButtonDown()) {
                return;
            }
            if (!view.moveable().get()) {
                event.consume();
                return;
            }
            // Due to whatever reasons this does not work in
            // delayed #singlePressAction
            mouseArea.setMouseTransparent(true);
            event.setDragDetect(true);

            if (event.getClickCount() == 1) {
                // we start a  future, if there is no mouse relase with 500 milliseconds
                // the dock target is shown
                scheduledFuture = executor.schedule(() -> singlePressAction(view, tab, event), 500, TimeUnit.MILLISECONDS);
            } else if (event.getClickCount() > 1) {
                // if we have a scheduled future we remove it
                if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                    scheduledFuture.cancel(false);
                }
                // and start
                dock.maximize(this);
            }
        });

        mouseArea.setOnMouseReleased(event -> {
            if (MouseButton.PRIMARY != event.getButton()) {
                return;
            }
            // if we have a running future we stop this
            if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                scheduledFuture.cancel(false);
            }

            mouseArea.setMouseTransparent(false);
            if (dragFlag) {
                dock.dragStart(view, event);
            }
            dragFlag = false;
        });

        mouseArea.setOnMouseDragged(event -> {
            if (MouseButton.PRIMARY != event.getButton()) {
                return;
            }
            if (dragFlag) {
                event.setDragDetect(false);
                dock.dragStart(view, event);
            }
        });

        mouseArea.setOnDragDetected(event -> {
            if (MouseButton.PRIMARY != event.getButton()) {
                return;
            }

            mouseArea.startFullDrag();
            if (dragFlag) {
                dock.dragStart(view, event);
            }
        });
    }

    // this is called with a delay to allow double click counting
    private void singlePressAction(DockableView view, Tab tab, MouseEvent event) {
        Platform.runLater(() -> {
            if (!view.moveable().get()) {
                event.consume();
                return;
            }
            dragFlag = true;
            tab.getGraphic().setMouseTransparent(true);
            dock.dragStart(view, event);
        });
    }

    /**
     * This is called when opening the context menu and updates the visible menu items
     *
     * @param menu the menu to update
     * @param view the view for which to work
     */
    private void updateMenu(ContextMenu menu, DockableView view) {
        menu.getItems().clear();

        if (view.closeable().get()) {
            MenuItem mi = new MenuItem(dock.getResourceBundle().getString("label_close"));
            mi.setOnAction(actionEvent -> dock.remove(view));
            menu.getItems().add(mi);
        }

        if (views.size() > 1) {
            MenuItem mi = new MenuItem(dock.getResourceBundle().getString("label_close_all"));
            mi.setOnAction(actionEvent -> {
                final List<DockableView> allViews = new ArrayList<>(views);
                for (DockableView cview : allViews) {
                    dock.remove(cview);
                }
            });
            menu.getItems().add(mi);
        }

        int pos = views.indexOf(view);

        if (pos > 0) {
            MenuItem mi = new MenuItem(dock.getResourceBundle().getString("label_close_all_to_left"));
            mi.setOnAction(actionEvent -> {
                final List<DockableView> toTheLeft = new ArrayList<>(views.subList(0, pos));
                for (DockableView cview : toTheLeft) {
                    dock.remove(cview);
                }
            });
            menu.getItems().add(mi);
        }
        if (pos != (views.size() - 1)) {
            MenuItem mi = new MenuItem(dock.getResourceBundle().getString("label_close_all_to_right"));
            mi.setOnAction(actionEvent -> {
                final List<DockableView> toTheRight = new ArrayList<>(views.subList(pos + 1, views.size()));
                LOG.info("right {}", toTheRight);
                for (DockableView cview : toTheRight) {
                    dock.remove(cview);
                }
            });
            menu.getItems().add(mi);
        }
        menu.getItems().add(new SeparatorMenuItem());

        MenuItem mi = new MenuItem(dock.getResourceBundle().getString(
                this.equals(dock.getMaximisedController()) ? "label_unmaximize" : "label_maximize"));
        mi.setOnAction(actionEvent -> dock.maximize(this));
        menu.getItems().add(mi);


    }

    /**
     * Remove a view from the dock
     *
     * @param view the view to remove
     */
    void remove(DockableView view) {
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
     * Checks if this controller contains the given view
     */
    boolean contains(DockableView view) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        return tabPane.getTabs().stream().anyMatch(tab -> view.equals(tab.getUserData()));
    }

    /**
     * Raise a view in the dock
     *
     * @param view the view to raise
     */
    void raise(DockableView view) {
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