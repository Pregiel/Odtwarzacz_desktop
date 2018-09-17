/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Sliders;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import odtwarzacz.Connection.Connection;
import odtwarzacz.Odtwarzacz;

/**
 * @author Pregiel
 */
public class VolumeSlider extends CustomSlider {

    private final MediaPlayer mediaPlayer;
    private boolean collapseOnRelease;
    private boolean isExpanded;
    private double mutedVolume = 0;
    private Connection connection;
    private Label volLabel;

    public VolumeSlider(AnchorPane slider, Pane track, MediaPlayer mediaPlayer, Label volLabel) {
        super(slider, track);
        this.collapseOnRelease = false;
        this.isExpanded = false;
        this.mediaPlayer = mediaPlayer;
        this.connection = null;
        this.volLabel = volLabel;
        setDirection(Direction.VERTICAL);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean getCollapseOnRelease() {
        return collapseOnRelease;
    }

    public void setCollapseOnRelease(boolean collapseOnRelease) {
        this.collapseOnRelease = collapseOnRelease;
    }

    public double getMutedVolume() {
        return mutedVolume;
    }

    public void setMutedVolume(double mutedVolume) {
        this.mutedVolume = mutedVolume;
    }

    public void mute() {
        if (getMutedVolume() == 0) {
            setMutedVolume(mediaPlayer.getVolume());
            setVolume(0.0);
            if (connection != null) {
                connection.sendMessage(Connection.MUTE_ON);
            }
        } else {
            setVolume(mutedVolume);
            mutedVolume = 0;
            if (connection != null) {
                connection.sendMessage(Connection.MUTE_OFF);
            }
        }
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        setIsChanging(true);
        setVolume(event);
        if (mutedVolume > 0) {
            mutedVolume = 0;
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        setIsChanging(false);
        setVolume(event);
        if (collapseOnRelease) {
//            animateVolumeSliderCollapse(event);
            collapseOnRelease = false;
        }

        Odtwarzacz.getConfig().setProperty("volume", String.valueOf(mediaPlayer.getVolume()));
        Odtwarzacz.getConfig().save();
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Volume changed to: {0}", String.valueOf(mediaPlayer.getVolume()));

    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        setVolume(event);
    }

    public void setVolume(double volume) {
        setSliderPosition(volume);
        mediaPlayer.setVolume(volume);
        volLabel.setText(String.valueOf(Math.round(volume * 100)) + "%");

        if (connection != null) {
            connection.sendMessage(Connection.VOLUME, volume);
        }
    }

    public void setVolume(MouseEvent event) {
        if (event.getY() <= getBackTrack().getHeight() && event.getY() >= 0) {
            setVolume(1 - (event.getY() / getBackTrack().getHeight()));
        } else if (event.getY() > getBackTrack().getHeight()) {
            setVolume(0);
        } else if (event.getY() < 0) {
            setVolume(1);
        }
    }

    public void animateVolumeSliderExpand(MouseEvent event) {
        if (!isExpanded) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(getBackTrack().scaleXProperty(), 0)),
                    new KeyFrame(new Duration(250),
                            new KeyValue(getBackTrack().scaleXProperty(), 1))
            );
            timeline.play();
            isExpanded = true;
        }
    }

    public void animateVolumeSliderCollapse(MouseEvent event) {
        if (isExpanded) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(getBackTrack().scaleXProperty(), 1)),
                    new KeyFrame(new Duration(250),
                            new KeyValue(getBackTrack().scaleXProperty(), 0))
            );
            timeline.play();
            isExpanded = false;
        }
    }

}
