package de.cadoculus.javafx.minidockfx;


import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class MiniDockFXPane {

    private static final Logger LOG = LoggerFactory.getLogger(MiniDockFXPane.class);

    public static final String ACTIVE_DRAG_TRGT = "minidockfx-drag-sub-target_active";

    @FXML
    private AnchorPane dockPane;
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


    private double lastVerticalSplit = 0.0;
    private double lastHorizontalSplit0 = 0.15;
    private double lastHorizontalSplit1 = 0.85;

    private AbstractTabbableView draggedView;

    @FXML
    public void initialize() {

        leftController.setDock(this);
        centerController.setDock(this);
        rightController.setDock(this);
        bottomController.setDock(this);


        dockPane.widthProperty().addListener((v, o, n) -> {
            sizeChanged();
        });
        dockPane.heightProperty().addListener((v, o, n) -> {
            sizeChanged();
        });

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
    }

    private void sizeChanged() {

        debugInfo("sizeChange");

        // vertical split
        if (verticalSplit.getItems().size() == 1) {
            if (verticalSplit.getItems().contains(bottom)) {
                verticalSplit.setDividerPositions(0.0);
            } else {
                verticalSplit.setDividerPositions(1.0);
            }
        }
        debugInfo("finished sizeChange");

    }

    private void debugInfo(String msg) {

        LOG.info(msg);
        LOG.info("left #{}, center #{}, right #{}, bottom #{}",
                leftController.views.size(),
                centerController.views.size(),
                rightController.views.size(),
                bottomController.views.size());

        LOG.info("verticalSplit {}", verticalSplit.getItems());
        LOG.info("horizontalSplit {}", horizontalSplit.getItems());

        LOG.info("horizontal {}, vertical {}",
                Arrays.toString(horizontalSplit.getDividerPositions()),
                Arrays.toString(verticalSplit.getDividerPositions()));

        LOG.info("lastVerticalSplit {}", lastVerticalSplit);
        LOG.info("lastHorizontal {} {}", lastHorizontalSplit0, lastHorizontalSplit1);


    }

    void updateLayout() {

        debugInfo("updateLayout");

        // check what subcontrols are needed
        boolean needLeft = !leftController.views.isEmpty();
        boolean needCenter = !centerController.views.isEmpty();
        boolean needRight = !rightController.views.isEmpty();
        boolean needFirstRow = needLeft || needCenter || needRight;
        boolean needSecondRow = !bottomController.views.isEmpty();

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
        double hpos0 = horizontalSplit.getDividerPositions().length > 0 ? horizontalSplit.getDividerPositions()[0] : 10.0;
        double thpos0 = Math.min(hpos0, lastHorizontalSplit0);
        double hpos1 = horizontalSplit.getDividerPositions().length > 1 ? horizontalSplit.getDividerPositions()[1] : 10.0;
        double thpos1 = Math.max(thpos0, Math.min(hpos1, lastHorizontalSplit1));

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
        if (needLeft && needCenter && needRight) {
            thpos0 = Math.min(thpos0, 0.1);
            thpos0 = Math.max(thpos0, 1.0 / 3);
            thpos1 = Math.min(thpos1, 2.0 / 3);
            thpos1 = Math.max(thpos1, 0.9);
        } else if (needLeft && needCenter) {
            thpos0 = Math.min(thpos0, 0.1);
            thpos0 = Math.max(thpos0, 1.0 / 3);
            thpos1 = 1.0;
        } else if (needLeft && needRight) {
            thpos0 = Math.min(thpos0, 1.0 / 3);
            thpos0 = Math.max(thpos0, 2.0 / 3);
            thpos1 = 1.0;
        } else if (needCenter && needRight) {
            thpos0 = Math.min(thpos0, 1.0 / 3);
            thpos0 = Math.max(thpos0, 2.0 / 3);
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


    public void add(AbstractTabbableView view, MiniDockTabPosition... positions) {
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
                    case PREFERENCES:
                        LOG.info("position preferences not implemented yet");
                        continue;
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


    void dragStart(AbstractTabbableView view, MouseEvent event) {

//        LOG.info("dragPressed {}", view.name.get());
//        LOG.info("    {}/{} {}", event.getSceneX(), event.getSceneY(), event.getEventType());
//        LOG.info("    {}/{} {}", dockPane.getLayoutX(), dockPane.getLayoutY(), dockPane.getLayoutBounds());

        draggedView = view;

//        LOG.info("dockPane children {}", dockPane.getChildren());

        if (MouseEvent.DRAG_DETECTED == event.getEventType()) {

            // make the drag target visible
            dragTarget.setVisible(true);
            dragTarget.toFront();

            dockPane.setCursor(Cursor.MOVE);

            // and position it in the vicinity of the mouse
            //     1. fallback position in the middle of the dock
            final Bounds dtBounds = dragTarget.getBoundsInLocal();
            final Bounds dkBounds = dockPane.getBoundsInLocal();
            //LOG.info("    bounds {} {}", dkBounds, dtBounds);

            double lx = (dkBounds.getWidth() - dtBounds.getWidth()) / 2.0;
            double ly = (dkBounds.getHeight() - dtBounds.getHeight()) / 2.0;

            //    2. if possible better place in the vicinity of the mouse
            try {
                final Transform localToSceneTransform = dockPane.getLocalToSceneTransform();
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
            dragTarget.setVisible(false);
            dragTarget.toBack();
            dockPane.setCursor(Cursor.DEFAULT);


        }
        event.consume();
    }

    private void dragEnd(Label trgt, MouseDragEvent mouseDragEvent) {
        LOG.info("dragEnd {} {}", trgt.getId(), mouseDragEvent.getEventType());

        if (draggedView == null) {
            LOG.error("something is wrong, got dragEnd, but have no draggedView value ???");
            return;
        }
        if (MouseDragEvent.MOUSE_DRAG_ENTERED == mouseDragEvent.getEventType()) {
            dockPane.setCursor(Cursor.HAND);
            trgt.getStyleClass().add( ACTIVE_DRAG_TRGT);
        } else if (MouseDragEvent.MOUSE_DRAG_EXITED == mouseDragEvent.getEventType()) {
            dockPane.setCursor(Cursor.MOVE);
            trgt.getStyleClass().remove( ACTIVE_DRAG_TRGT);
        } else if (MouseDragEvent.MOUSE_DRAG_RELEASED == mouseDragEvent.getEventType()) {
            // we should move the source view
            trgt.getStyleClass().remove( ACTIVE_DRAG_TRGT);
            dockPane.setCursor(Cursor.DEFAULT);
            LOG.info("move view '{}' to {}", draggedView, trgt.getId());
        }


        LOG.info("style on  {}: {}", trgt.getId(), trgt.getStyleClass());
        mouseDragEvent.consume();
    }

}

