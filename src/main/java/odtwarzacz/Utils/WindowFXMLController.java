/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import odtwarzacz.IconFont;
import odtwarzacz.Theme;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class WindowFXMLController implements Initializable {


    public BorderPane pane;
    public GridPane titleBar;
    public Button maximizeButton;

    private Stage maximizeScreen;

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
                    stage.setWindowMaximized(!stage.isWindowMaximized());
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
                    if (stage.isWindowMaximized()) {
                        stage.setWindowMaximized(false);
                        dragDelta.x = stage.getWidth() / -2;
                    } else {
                        stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                        stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                    }
                    if (mouseEvent.getScreenY() == 0) {
                        maximizeScreen.show();

                    } else {
                        maximizeScreen.hide();

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
                    stage.setWindowMaximized(true);
                }
                maximizeScreen.hide();

            }
        });

        initMaximizeScreen();
    }

    private void initMaximizeScreen() {
        Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Pane screen = new Pane();

        screen.setPrefHeight(winSize.height - 10);
        screen.setPrefWidth(winSize.width - 10);
        screen.setStyle("-fx-background-color: " + Theme.getStyle("window.maximize.color") + ";");

        maximizeScreen = new Stage();
        maximizeScreen.setScene(new Scene(screen));
        maximizeScreen.initStyle(StageStyle.TRANSPARENT);
        maximizeScreen.getScene().setFill(Color.TRANSPARENT);
        maximizeScreen.setAlwaysOnTop(false);
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
            if (stage.isWindowMaximized()) {
                stage.setWindowMaximized(false);
                maximizeButton.setText(IconFont.ICON_MAXIMIZE);
            } else {
                stage.setWindowMaximized(true);
                maximizeButton.setText(IconFont.ICON_MINIMIZE);
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
