/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz;

import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.util.Duration;
import odtwarzacz.Connection.Connection;

/**
 *
 * @author Pregiel
 */
public class InfoLabel extends Label {

    private static final double HIDEIN_DURATION = 5000;

    public static final String CONNECTION_WAITFORCLIENT = "CONNECTION_WAITFORCLIENT";
    public static final String CONNECTION_WAITFORCLIENT_WIFI = "CONNECTION_WAITFORCLIENT_WIFI";
    public static final String CONNECTION_WAITFORCLIENT_BT = "CONNECTION_WAITFORCLIENT_BT";
    public static final String CONNECTION_CONNECTED = "CONNECTION_CONNECTED";
    public static final String CONNECTION_DISCONNECTED = "CONNECTION_DISCONNECTED";
    public static final String FILE_NOFILE = "FILE_NOFILE";

    private Timeline animation;

    public static enum Animations {
        threeDots, hideIn;
    }

//    private final Timeline threeDots;
    public InfoLabel() {
        super();
        hide();
    }

    private Timeline threeDotsAnimation() {
        Timeline threeDots = new Timeline(
                new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
                    String statusText = this.getText();//.substring(0, connectionLabel.getText().indexOf('.'));
                    int dots_count = statusText.split("\\.").length;
                    switch (dots_count) {
                        case 1:
                            this.setText(statusText.split("\\.")[0] + ". .");
                            break;

                        case 2:
                            this.setText(statusText.split("\\.")[0] + ". . .");
                            break;

                        case 3:
                            this.setText(statusText.split("\\.")[0] + ".");
                            break;
                    }
                }),
                new KeyFrame(Duration.millis(1000))
        );
        threeDots.setCycleCount(Timeline.INDEFINITE);

        return threeDots;
    }

    private Timeline hideInAnimation(double ms) {
        Timeline hideIn = new Timeline(new KeyFrame(Duration.millis(ms), (event) -> {
            this.setText("");
            hide();
        })
        );

        return hideIn;
    }

    public final void hide() {
        this.setVisible(false);
        if (animation != null) {
            animation.stop();
        }
    }

    public void setInfoText(String msg, String... extra) {
        this.setVisible(true);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        switch (msg) {
            case CONNECTION_WAITFORCLIENT:
                this.setText(resourceBundle.getString("connection.waitforclient"));
                break;

            case CONNECTION_WAITFORCLIENT_WIFI:
                this.setText(resourceBundle.getString("connection.connectingwifi") + ".");
                setAnimation(Animations.threeDots);
                break;

            case CONNECTION_WAITFORCLIENT_BT:
                this.setText(resourceBundle.getString("connection.connectingbt") + ".");
                setAnimation(Animations.threeDots);
                break;

            case CONNECTION_CONNECTED:
                this.setText(resourceBundle.getString("connection.connectedwith") + extra[0]);
                setAnimation(Animations.hideIn);
                break;

            case CONNECTION_DISCONNECTED:
                this.setText(resourceBundle.getString("connection.disconnectedwith") + extra[0]);
                setAnimation(Animations.hideIn);
                break;

            case FILE_NOFILE:
                this.setText(resourceBundle.getString("file.nofile") + extra[0]);
                setAnimation(Animations.hideIn);
                break;

            default:
                this.setText(msg + Arrays.toString(extra));
        }
    }

    public void setAnimation(Animations animation) {
        if (this.animation != null) {
            this.animation.stop();
        }
        switch (animation) {
            case hideIn:
                (this.animation = hideInAnimation(HIDEIN_DURATION)).play();
                break;

            case threeDots:
                (this.animation = threeDotsAnimation()).play();
                break;
        }
    }

}
