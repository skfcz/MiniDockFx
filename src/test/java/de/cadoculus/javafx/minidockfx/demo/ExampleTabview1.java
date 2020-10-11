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
package de.cadoculus.javafx.minidockfx.demo;

import com.jfoenix.controls.JFXCheckBox;
import de.cadoculus.javafx.minidockfx.AbstractDockableView;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


/**
 * This is just an example to show what it is possible with the AbstractTabbableView.
 */
public class ExampleTabview1 extends AbstractDockableView {


    public ExampleTabview1(String name, String id, Color color) {
        super(name, id, true, true );
        content = new BorderPane();

        content.getStyleClass().add("ExampleView1");

        Color start = color.brighter().desaturate();
        Stop[] stops = new Stop[]{new Stop(0, start), new Stop(1, color)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        content.setBackground(new Background(new BackgroundFill(lg1, CornerRadii.EMPTY, Insets.EMPTY)));

        content.getStyleClass().add("exampleTabview1");
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        ((BorderPane) content).setCenter(vbox);

        vbox.getChildren().add(new Label("content " + name));

        JFXCheckBox closeCB = new JFXCheckBox("closeable");
        closeCB.selectedProperty().bindBidirectional(closeable);
        vbox.getChildren().add(closeCB);

        JFXCheckBox moveCB = new JFXCheckBox("moveable");
        moveCB.selectedProperty().bindBidirectional(moveable);
        vbox.getChildren().add(moveCB);

    }

    @Override
    public void beforeAdding() {

    }

    @Override
    public void afterAdding() {

    }

    @Override
    public void beforeClose() {

    }

    @Override
    public void afterClose() {

    }
}
