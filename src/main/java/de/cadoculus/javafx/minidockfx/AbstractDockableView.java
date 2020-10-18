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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * This is the base class to be extended for views
 */
public abstract class AbstractDockableView implements DockableView {

    protected final StringProperty name = new SimpleStringProperty();
    protected final HBox tab;
    protected final BooleanProperty closeable = new SimpleBooleanProperty();
    protected final BooleanProperty moveable = new SimpleBooleanProperty();
    protected final String id;
    protected Region content;

    /**
     * The default constructor
     *
     * @param name     the initial view name. Use {@link #name} to change it at runtime
     * @param id       the id used to store position in preferences. No storage used if id is empty or null.
     * @param canClose true if the view is closeable. If false no button is added to the tab.
     * @param canMove  true if the view is moveable. If false it is not possible to move the tab
     */
    protected AbstractDockableView(String name, String id, boolean canClose, boolean canMove) {

        this.id = id;
        this.name.setValue(name);
        this.closeable.setValue(canClose);
        this.moveable.setValue(canMove);

        tab = new HBox();
        tab.getStyleClass().add(MiniDockFXPane.VIEW_BOX_STYLE);
        tab.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label();
        label.getStyleClass().add(MiniDockFXPane.VIEW_LABEL_STYLE);
        label.textProperty().bind(this.name);
        tab.getChildren().add(label);

        // in case the title becomes too long we add a tooltip
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(this.name);
        tooltip.getStyleClass().add(MiniDockFXPane.VIEW_LABEL_TT_STYLE);
        label.setTooltip(tooltip);
    }

    public BooleanProperty closeable() {
        return closeable;
    }

    public BooleanProperty moveable() {
        return moveable;
    }

    /**
     * Get the part to be displayed in the tab,
     * e.g. the name, some tool icons ...
     *
     */
    @Override
    public Region getTab() {
        return tab;
    }

    @Override
    public Region getContent() {
        return content;
    }

    /**
     * This method is called before a view is added.
     * You could override it if you need that information.
     */
    @Override
    public void beforeAdding() {

    }

    /**
     * This method is called after a view was added.
     * You could override it if you need that information.
     */
    @Override
    public void afterAdding() {

    }

    /**
     * This method is called before a view is added.
     * You could override it if you need that information.
     */
    @Override
    public void beforeClose() {

    }

    /**
     * This method is called after a view was added.
     * You could override it if you need that information.
     */
    @Override
    public void afterClose() {

    }


}
