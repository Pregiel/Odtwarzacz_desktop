/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Theme;
import odtwarzacz.Utils.CustomStage;
import odtwarzacz.Utils.Delta;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class PlaylistUndockedFXMLController implements Initializable {


    public BorderPane pane;
    public GridPane titleBar;

    private Stage maximazeScreen;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final Delta dragDelta = new Delta();

        titleBar.setOnMousePressed(mouseEvent -> {
            CustomStage stage = ((CustomStage) pane.getScene().getWindow());
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    stage.setWindowMaximazed(!stage.isWindowMaximazed());
                } else {
                    dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                    dragDelta.y = stage.getY() - mouseEvent.getScreenY();

                    stage.setMoving(true);
                }
            }
        });

        titleBar.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
                CustomStage stage = ((CustomStage) pane.getScene().getWindow());
                if (!stage.isResizing()) {
                    if (stage.isWindowMaximazed()) {
                        stage.setWindowMaximazed(false);
                        dragDelta.x = stage.getWidth() / -2;
                    } else {
                        stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                        stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                    }
                    if (mouseEvent.getScreenY() == 0) {
                        maximazeScreen.show();

                    } else {
                        maximazeScreen.hide();

                    }
                }
            }
        });

        titleBar.setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                CustomStage stage = ((CustomStage) pane.getScene().getWindow());
                stage.setMoving(false);
                stage.getScene().setCursor(Cursor.DEFAULT);

                if (mouseEvent.getScreenY() == 0) {
                    stage.setWindowMaximazed(true);
                }
                maximazeScreen.hide();

            }
        });

        initMaximazeScreen();
    }



    private void initMaximazeScreen() {
        Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Pane screen = new Pane();

        screen.setPrefHeight(winSize.height - 10);
        screen.setPrefWidth(winSize.width - 10);
        screen.setStyle("-fx-background-color: " + Theme.getStyle("window.maximaze.color") + ";");

        maximazeScreen = new Stage();
        maximazeScreen.setScene(new Scene(screen));
        maximazeScreen.initStyle(StageStyle.TRANSPARENT);
        maximazeScreen.getScene().setFill(Color.TRANSPARENT);
        maximazeScreen.setAlwaysOnTop(false);
    }



    public void minButton(ActionEvent event) {
        CustomStage stage = ((CustomStage) pane.getScene().getWindow());
        if (!stage.isMoving() && !stage.isResizing()) {
            stage.setIconified(true);
        }
    }

    public void maxButton(ActionEvent event) {
        CustomStage stage = ((CustomStage) pane.getScene().getWindow());
        if (!stage.isMoving() && !stage.isResizing()) {
            if (stage.isWindowMaximazed()) {
                stage.setWindowMaximazed(false);
            } else {
                stage.setWindowMaximazed(true);
            }
        }
    }

    public void exitButton(ActionEvent event) {
        CustomStage stage = ((CustomStage) pane.getScene().getWindow());
        if (!stage.isMoving() && !stage.isResizing()) {
            stage.close();
        }
    }
}
