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

    @FXML
    public void initialize() {

        LOG.info("initialize");

        ListChangeListener lcl = change -> {
            updateDividers();
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
            divider.positionProperty().addListener((v, o,n) -> dividersChanged());
        }
        for (SplitPane.Divider divider : horizontalSplit.getDividers()) {
            divider.positionProperty().addListener((v, o,n) -> dividersChanged());
        }


    }

    private void dividersChanged() {
        LOG.info("dividers changed {}");
    }

    private void sizeChanged() {
        LOG.info("size changed {} {}", dockPane.getWidth(), dockPane.getHeight());
    }

    private void updateDividers() {
        LOG.info("updateDividers");

        boolean needTopRow = false;
        boolean needBottomRow = false;

        LOG.info("left #{}, center #{}, right #{}, bottom #{}",
                leftController.views.size(),
                centerController.views.size(),
                rightController.views.size(),
                bottomController.views.size());
        LOG.info("horizontal {}, vertical {}",
                Arrays.toString(horizontalSplit.getDividerPositions()),
                Arrays.toString(verticalSplit.getDividerPositions()));


        // check vertically
        boolean needFirstRow = !(leftController.views.isEmpty() && centerController.views.isEmpty() && rightController.views.isEmpty());
        boolean needSecondRow = !bottomController.views.isEmpty();

        if (needFirstRow && needSecondRow) {
            // devider must be somewhere between 5 and 95%
            double vpos = verticalSplit.getDividerPositions()[0];
            if (vpos < 0.05 || vpos > 0.95) {
                verticalSplit.setDividerPosition(0, 0.8);
            }
        } else {
            verticalSplit.setDividerPosition(0, needFirstRow ? 1.0 : 0.0);
        }


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

