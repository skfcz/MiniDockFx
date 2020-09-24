package de.cadoculus.javafx.minidockfx;


import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MiniDockFXPane {

    private static final Logger LOG = LoggerFactory.getLogger(MiniDockFXPane.class);

    @FXML
    private AnchorPane dockPane;

    @FXML
    private SplitPane verticalSplit;

    @FXML
    private SplitPane horizontalSplit;

    @FXML
    private AnchorPane top;

    @FXML
    private AnchorPane left;

    @FXML
    private AnchorPane center;

    @FXML
    private AnchorPane right;

    @FXML
    private AnchorPane bottom;

    @FXML
    private TabbedDockController leftController;
    @FXML
    private TabbedDockController centerController;
    @FXML
    private TabbedDockController rightController;
    @FXML
    private TabbedDockController bottomController;

    private double lastVerticalSplit = 0.0;
    private double lastHorizontalSplit0 = 0.15;
    private double lastHorizontalSplit1 = 0.85;

    @FXML
    public void initialize() {
        ListChangeListener lcl = change -> {
            updateLayout();
        };

        leftController.views.addListener(lcl);
        centerController.views.addListener(lcl);
        rightController.views.addListener(lcl);
        bottomController.views.addListener(lcl);

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
        LOG.info("lastHorizontal {} {}",lastHorizontalSplit0, lastHorizontalSplit1);


    }

    private void updateLayout() {

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
        double thpos0 = Math.min( hpos0, lastHorizontalSplit0);
        double hpos1 = horizontalSplit.getDividerPositions().length > 1 ? horizontalSplit.getDividerPositions()[1] : 10.0;
        double thpos1 = Math.max( thpos0, Math.min( hpos1, lastHorizontalSplit1));

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
    }


}

