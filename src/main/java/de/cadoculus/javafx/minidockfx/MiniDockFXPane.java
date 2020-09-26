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


import com.jfoenix.controls.JFXRippler;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * The MiniDockFXPane is a simple docking control.
 * <p>
 * It allows to add and remove individual views to different docking places and move them around.
 * The dock places are called LEFT,CENTER,RIGHT,BOTTOM and are arranged as in the BorderLayout.
 * Unless {@link AbstractTabableView#closeable} is false, it is possible to close views.
 * Unless {@link AbstractTabableView#moveable} is false, it is possible to move views from one dock by another using dragging (left MB pressed).
 * </p>
 * <p>
 * The views lifecycle is as follows:
 * <ul>
 *     <li>Create your view as extension of {@link AbstractTabableView} class. You need to provide the views content in the content variable.</li>
 *     <li>Add the view for display using {@link MiniDockFXPane#add(AbstractTabableView, MiniDockTabPosition...)}.
 *     If you provide no position is given, it will be placed to CENTER. If you give a dock position, it will be placed in that dock.
 *     If you provide PREFERENCES and another value, it will be placed in the same place as stored in preferences
 *     </li>
 * </ul>
 *
 * </p>
 */
public class MiniDockFXPane extends AnchorPane {

    public static final String VIEW_LABEL_STYLE = "minidockfx-view-label";
    public static final String VIEW_BOX_STYLE = "minidockfx-view-box";
    public static final String VIEW_LABEL_TT_STYLE = "minidockfx-view-label-tooltip";
    public static final String CLOSE_BUTTON_STYLE = "minidockfx-tab-close";
    public static final String TAB_HEADER_STYLE = "minidockfx-tab-header";
    public static final String ACTIVE_DRAG_TRGT_STYLE = "minidockfx-drag-sub-target_active";
    private static final String LAST_VERT_SPLIT_KEY = "lastVerticalSplit";
    private static final String LAST_HOR_SPLIT0_KEY = "lastHorizontalSplit0";
    private static final String LAST_HOR_SPLIT1_KEY = "lastHorizontalSplit1";

    private static final Logger LOG = LoggerFactory.getLogger(MiniDockFXPane.class);


    @FXML
    private AnchorPane top;
    @FXML
    private SplitPane verticalSplit;
    @FXML
    private SplitPane horizontalSplit;


    @FXML
    private AnchorPane left;
    @FXML
    private TabbedDockController leftController;
    @FXML
    private AnchorPane center;
    @FXML
    private TabbedDockController centerController;
    @FXML
    private AnchorPane right;
    @FXML
    private TabbedDockController rightController;
    @FXML
    private AnchorPane bottom;
    @FXML
    private TabbedDockController bottomController;

    @FXML
    private BorderPane dragTarget;
    @FXML
    private Label leftDragTarget;
    @FXML
    private Label centerDragTarget;
    @FXML
    private Label rightDragTarget;
    @FXML
    private Label bottomDragTarget;

    private final Preferences prefs = Preferences.userRoot().node(MiniDockFXPane.class.getName() + "." + getId());
    private double lastVerticalSplit = prefs.getDouble(LAST_VERT_SPLIT_KEY, 2 / 3.0);
    private double lastHorizontalSplit0 = prefs.getDouble(LAST_HOR_SPLIT0_KEY, 0.15);
    private double lastHorizontalSplit1 = prefs.getDouble(LAST_VERT_SPLIT_KEY, 0.85);

    private AbstractTabableView draggedView;

    /**
     * The default creator.
     */
    public MiniDockFXPane() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DefaultDock.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            LOG.error("an error occured loading components fxml", exception);
            throw new RuntimeException(exception);
        }

    }

    @FXML
    public void initialize() {

        LOG.error("initialize");

        leftController.setDock(this);
        centerController.setDock(this);
        rightController.setDock(this);
        bottomController.setDock(this);


        verticalSplit.setDividerPositions(lastVerticalSplit);
        horizontalSplit.setDividerPositions(lastHorizontalSplit0, lastHorizontalSplit1);

        for (SplitPane.Divider divider : verticalSplit.getDividers()) {
            divider.positionProperty().addListener((v, o, n) -> dividersChanged());
        }
        for (SplitPane.Divider divider : horizontalSplit.getDividers()) {
            divider.positionProperty().addListener((v, o, n) -> dividersChanged());
        }

        for (Label trgt : List.of(leftDragTarget, centerDragTarget, rightDragTarget, bottomDragTarget)) {
            trgt.setOnMouseDragEntered(mouseDragEvent -> dragEnd(trgt, mouseDragEvent));
            trgt.setOnMouseDragOver(mouseDragEvent -> dragEnd(trgt, mouseDragEvent));
            trgt.setOnMouseDragReleased(mouseDragEvent -> dragEnd(trgt, mouseDragEvent));
            trgt.setOnMouseDragExited(mouseDragEvent -> dragEnd(trgt, mouseDragEvent));
        }

    }


    /**
     * Add a view to the docking panel
     *
     * @param view      the view to add
     * @param positions the desired positions. If no positon is given puts view to CENTER
     * @throws IllegalArgumentException in case of null view or if view was already added
     */
    public void add(AbstractTabableView view, MiniDockTabPosition... positions) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        if (view.getContent() == null) {
            throw new IllegalArgumentException("no content found in view, you need to provide a content in " + view.getClass());
        }
        if (leftController.views.contains(view)) {
            throw new IllegalArgumentException("found view " + view + " already docked on left side");
        } else if (centerController.views.contains(view)) {
            throw new IllegalArgumentException("found view " + view + " already docked on center");
        } else if (rightController.views.contains(view)) {
            throw new IllegalArgumentException("found view " + view + " already docked on right side");
        } else if (bottomController.views.contains(view)) {
            throw new IllegalArgumentException("found view " + view + " already docked on bottom");
        }

        MiniDockTabPosition pos = MiniDockTabPosition.CENTER;
        if (positions != null) {
            for (int i = 0; i < positions.length; i++) {
                MiniDockTabPosition check = positions[i];
                switch (check) {
                    case LEFT:
                    case CENTER:
                    case RIGHT:
                    case BOTTOM:
                        pos = check;
                        break;
                    default:
                        LOG.error("got unsupported position value {}", check);
                }
            }
        }

        TabbedDockController tbc = null;
        switch (pos) {
            case LEFT:
                tbc = leftController;
                break;
            case CENTER:
                tbc = centerController;
                break;
            case RIGHT:
                tbc = rightController;
                break;
            case BOTTOM:
                tbc = bottomController;
                break;
        }

        tbc.add(view);
        tbc.raise(view);
    }

    /**
     * Remove the given view from the docking panel.
     *
     * @param view the view to remove
     * @throws IllegalArgumentException in case of a null view or one which is not managed by the dock
     */
    public void remove(AbstractTabableView view) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        if (leftController.views.contains(view)) {
            leftController.remove(view);
        } else if (centerController.views.contains(view)) {
            centerController.remove(view);
        } else if (rightController.views.contains(view)) {
            rightController.remove(view);
        } else if (bottomController.views.contains(view)) {
            bottomController.remove(view);
        } else {
            throw new IllegalArgumentException("view " + view + " is not managed in docking panel");
        }
    }

    /**
     * Move the given view from the current position to a new one
     *
     * @param view     the view to move
     * @param position the target position
     * @throws IllegalArgumentException in case of a null view or one which is not managed by the dock
     */
    public void move(AbstractTabableView view, MiniDockTabPosition position) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        TabbedDockController tbc = null;
        switch (position) {
            case LEFT:
                tbc = leftController;
                break;
            case CENTER:
                tbc = centerController;
                break;
            case RIGHT:
                tbc = rightController;
                break;
            case BOTTOM:
                tbc = bottomController;
                break;
            default:
                LOG.error("got unsupported position value {}, skip moving", position);
                return;
        }
        if (tbc.views.contains(view)) {
            // nothing to do
        } else {
            remove(view);
            add(view, position);
        }
    }


    /**
     * This is used in a listener and stores the position of the dividers in the preferences.
     */
    private void dividersChanged() {

        double[] pos = verticalSplit.getDividerPositions();
        if (pos.length > 0) {
            lastVerticalSplit = pos[0];
        }
        pos = horizontalSplit.getDividerPositions();
        if (pos.length > 0) {
            lastHorizontalSplit0 = pos[0];
            if (pos.length > 1) {
                lastHorizontalSplit1 = pos[1];
            }
        }
        // store to preferences
        prefs.putDouble(LAST_VERT_SPLIT_KEY, lastVerticalSplit);
        prefs.putDouble(LAST_HOR_SPLIT0_KEY, lastHorizontalSplit0);
        prefs.putDouble(LAST_HOR_SPLIT1_KEY, lastHorizontalSplit1);

    }


    private void debugInfo(String msg) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(msg);
            LOG.debug("left #{}, center #{}, right #{}, bottom #{}",
                    leftController.views.size(),
                    centerController.views.size(),
                    rightController.views.size(),
                    bottomController.views.size());

            LOG.debug("verticalSplit {}", verticalSplit.getItems());
            LOG.debug("horizontalSplit {}", horizontalSplit.getItems());

            LOG.debug("horizontal {}, vertical {}",
                    Arrays.toString(horizontalSplit.getDividerPositions()),
                    Arrays.toString(verticalSplit.getDividerPositions()));

            LOG.debug("lastVerticalSplit {}", lastVerticalSplit);
            LOG.debug("lastHorizontal {} {}", lastHorizontalSplit0, lastHorizontalSplit1);
        }

    }

    void updateLayout() {

        debugInfo("updateLayout");

        // check what sub controls are needed
        boolean needLeft = !leftController.views.isEmpty();
        boolean needCenter = !centerController.views.isEmpty();
        boolean needRight = !rightController.views.isEmpty();
        boolean needFirstRow = needLeft || needCenter || needRight;
        boolean needSecondRow = !bottomController.views.isEmpty();

        // and calculate splitting positions
        double vpos = verticalSplit.getDividerPositions().length > 0 ? verticalSplit.getDividerPositions()[0] : 10;
        double tvpos = Math.min(lastVerticalSplit, vpos);

        // Care about vertical layout
        verticalSplit.getItems().clear();
        if (needFirstRow) {
            verticalSplit.getItems().add(top);
        }
        if (needSecondRow) {
            verticalSplit.getItems().add(bottom);
        }

        if (needFirstRow && needSecondRow) {
            // Cap the value somehow
            tvpos = Math.max(0.15, tvpos);
            tvpos = Math.min(0.85, tvpos);

        } else if (needFirstRow) {
            tvpos = 1.0;
        } else if (needSecondRow) {
            tvpos = 0.0;
        }
        verticalSplit.setDividerPositions(tvpos);

        // Care about horizontal layout
        // TODO: this is ugly and should be clean up
        int numCurSplits = horizontalSplit.getItems().size() - 1;

        double hpos0 = horizontalSplit.getDividerPositions().length > 0 ? horizontalSplit.getDividerPositions()[0] : 10.0;
        double thpos0 = Math.min(hpos0, lastHorizontalSplit0);
        double hpos1 = horizontalSplit.getDividerPositions().length > 1 ? horizontalSplit.getDividerPositions()[1] : 10.0;
        double thpos1 = Math.max(thpos0, Math.min(hpos1, lastHorizontalSplit1));

        // handle the docks them selves
        horizontalSplit.getItems().clear();
        if (needLeft) {
            horizontalSplit.getItems().add(left);
        }
        if (needCenter) {
            horizontalSplit.getItems().add(center);
        }
        if (needRight) {
            horizontalSplit.getItems().add(right);
        }

        // now calculate a usefull splitter position
        if (needLeft && needCenter && needRight) {
            if (numCurSplits == 3) {
                // keep values as is
            } else {
                thpos0 = lastHorizontalSplit0;
                thpos0 = Math.min(thpos0, 0.1);
                thpos0 = Math.max(thpos0, 1.0 / 3);
                thpos1 = Math.min(thpos1, 2.0 / 3);
                thpos1 = Math.max(thpos1, 0.9);
            }
        } else if (needLeft && needCenter) {
            if (numCurSplits == 1) {
                // keep values as is
            } else {
                thpos0 = Math.min(thpos0, 0.1);
                thpos0 = Math.max(thpos0, 1.0 / 3);
                thpos1 = 1.0;
            }
        } else if (needLeft && needRight) {
            if (numCurSplits == 1) {
                // keep values as is
            } else {
                thpos0 = Math.min(thpos0, 1.0 / 3);
                thpos0 = Math.max(thpos0, 2.0 / 3);
                thpos1 = 1.0;
            }
        } else if (needCenter && needRight) {
            if (numCurSplits == 1) {
                // keep values as is
            } else {
                thpos0 = Math.min(thpos0, 1.0 / 3);
                thpos0 = Math.max(thpos0, 2.0 / 3);
            }
        } else {
            thpos0 = thpos1 = 1.0;
        }
        horizontalSplit.setDividerPositions(thpos0, thpos1);

        for (SplitPane.Divider divider : verticalSplit.getDividers()) {
            divider.positionProperty().addListener((v, o, n) -> dividersChanged());
        }
        for (SplitPane.Divider divider : horizontalSplit.getDividers()) {
            divider.positionProperty().addListener((v, o, n) -> dividersChanged());
        }

        debugInfo("finish updateDividers");


    }


    void dragStart(AbstractTabableView view, MouseEvent event) {
        draggedView = view;

        if (MouseEvent.DRAG_DETECTED == event.getEventType()) {

            // make the drag target visible
            dragTarget.setVisible(true);
            dragTarget.toFront();

            setCursor(Cursor.MOVE);

            // and position it in the vicinity of the mouse
            //     1. fallback position in the middle of the dock
            final Bounds dtBounds = dragTarget.getBoundsInLocal();
            final Bounds dkBounds = getBoundsInLocal();
            //LOG.info("    bounds {} {}", dkBounds, dtBounds);

            double lx = (dkBounds.getWidth() - dtBounds.getWidth()) / 2.0;
            double ly = (dkBounds.getHeight() - dtBounds.getHeight()) / 2.0;

            //    2. if possible better place in the vicinity of the mouse
            try {
                final Transform localToSceneTransform = getLocalToSceneTransform();
                final Point2D mouseInLocal = localToSceneTransform.inverseTransform(event.getSceneX(), event.getSceneY());

                // Horizontal
                // place the drag target middle where the mouse it,
                // but keep at lest 10px distance to the edge of the dock
                lx = Math.max(30, mouseInLocal.getX() - dtBounds.getWidth() / 2.0);
                lx = Math.min(lx, dkBounds.getWidth() - 30 - dtBounds.getWidth());

                // Vertical
                // place the drag target below the mouse unless there is not enough space
                ly = mouseInLocal.getY() + 30;
                if ((ly + dtBounds.getHeight() + 30) > dkBounds.getHeight()) {
                    ly = mouseInLocal.getY() - 30 - dtBounds.getHeight();
                }

            } catch (Exception exp) {
                LOG.warn("failed to apply conversion, use fallback postion", exp);
            }

            dragTarget.setLayoutX(lx);
            dragTarget.setLayoutY(ly);


        } else if (MouseEvent.MOUSE_RELEASED == event.getEventType()) {
            finishDragging();
        }
        event.consume();
    }


    private void dragEnd(Label trgt, MouseDragEvent mouseDragEvent) {
        LOG.debug("dragEnd {} {}", trgt.getId(), mouseDragEvent.getEventType());

        if (draggedView == null) {
            LOG.error("something is wrong, got dragEnd, but have no draggedView value ???");
            return;
        }
        if (MouseDragEvent.MOUSE_DRAG_ENTERED == mouseDragEvent.getEventType()) {
            setCursor(Cursor.HAND);
            trgt.getStyleClass().add(ACTIVE_DRAG_TRGT_STYLE);

            if (trgt.getParent() instanceof JFXRippler) {
                ((JFXRippler) trgt.getParent()).createManualRipple().run();
            }

        } else if (MouseDragEvent.MOUSE_DRAG_EXITED == mouseDragEvent.getEventType()) {
            setCursor(Cursor.MOVE);
            trgt.getStyleClass().remove(ACTIVE_DRAG_TRGT_STYLE);
        } else if (MouseDragEvent.MOUSE_DRAG_RELEASED == mouseDragEvent.getEventType()) {
            // we should move the source view
            LOG.info("move view '{}' to {}", draggedView, trgt.getId());

            trgt.getStyleClass().remove(ACTIVE_DRAG_TRGT_STYLE);
            finishDragging();
            move(draggedView, MiniDockTabPosition.parseFromId(trgt.getId()));
            draggedView = null;
        }
        mouseDragEvent.consume();
    }

    private void finishDragging() {
        // have a nice fade out for the drag target panel
        FadeTransition fade = new FadeTransition();
        fade.setNode(dragTarget);
        fade.setDuration(Duration.millis(250));
        fade.setFromValue(10);
        fade.setToValue(0.1);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.setOnFinished(actionEvent -> {
            dragTarget.setVisible(false);
            dragTarget.toBack();
            dragTarget.setOpacity(1);
            setCursor(Cursor.DEFAULT);
        });
        fade.play();

        setCursor(Cursor.DEFAULT);

    }

}

