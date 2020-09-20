package de.cadoculus.javafx.minidockfx;


import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    }

    public void add(AbstractTabbableView view, MiniDockTabPosition... positions) {
        if (view == null) {
            throw new IllegalArgumentException("expect none null view");
        }
        if (view.getContent() == null) {
            throw new IllegalArgumentException("no content found in view, you need to provide a content in " + view.getClass());
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

