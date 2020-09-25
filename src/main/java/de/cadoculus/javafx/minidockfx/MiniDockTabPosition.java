package de.cadoculus.javafx.minidockfx;

import java.util.Locale;

/**
 * This enumeration is used to identify the positions of the docking panel.
 * THe 'PREFERENCES' is for a later version to preserve the last choice
 * by a user.
 */
public enum MiniDockTabPosition {
    PREFERENCES, LEFT, CENTER, RIGHT, BOTTOM;

    public static MiniDockTabPosition parseFromId(String id) {
        if (id == null || id.trim().length() == 0) {
            return CENTER;
        }
        id = id.trim().toLowerCase(Locale.ENGLISH);
        if (id.startsWith("left")) {
            return LEFT;
        } else if (id.startsWith("center")) {
            return CENTER;
        } else if (id.startsWith("right")) {
            return RIGHT;
        } else if (id.startsWith("bottom")) {
            return BOTTOM;
        }
        return CENTER;

    }
}
