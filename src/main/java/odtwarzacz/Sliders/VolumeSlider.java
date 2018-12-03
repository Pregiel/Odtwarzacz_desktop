/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Sliders;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import odtwarzacz.Connection.Connection;
import odtwarzacz.IconFont;
import odtwarzacz.Odtwarzacz;

/**
 * @author Pregiel
 */
public class VolumeSlider extends CustomSlider {

    private final MediaPlayer mediaPlayer;
    private boolean collapseOnRelease;
    private double mutedVolume = 0;
    private Connection connection;
    private Label volLabel;
    private Button volButton;
    private double volume;

    public VolumeSlider(AnchorPane slider, Pane track, MediaPlayer mediaPlayer, Label volLabel, Button volButton) {
        super(slider, track);
        this.collapseOnRelease = false;
        this.mediaPlayer = mediaPlayer;
        this.connection = null;
        this.volLabel = volLabel;
        this.volButton = volButton;
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

    private boolean muted = false;

    public void mute() {
        if (!muted) {
            setMutedVolume(mediaPlayer.getVolume());
            setVolume(0.0);

            if (connection != null) {
                connection.sendMessage(Connection.MUTE_ON);
            }
            muted = true;
        } else {
            setVolume(mutedVolume);
            mutedVolume = 0;

            if (connection != null) {
                connection.sendMessage(Connection.MUTE_OFF);
            }
            muted = false;
        }

        setButtonIcon(mediaPlayer.getVolume());
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

    public void setVolume(double newVolume) {
        if (newVolume < 0.0) {
            newVolume = 0.0;
        } else if (newVolume > 1.0) {
            newVolume = 1.0;
        }

        if (newVolume > 0.0) {
            mutedVolume = 0;

            if (connection != null) {
                connection.sendMessage(Connection.MUTE_OFF);
            }
            muted = false;
        }

        setButtonIcon(newVolume);


        setSliderPosition(newVolume);
        mediaPlayer.setVolume(newVolume);

        volume = newVolume;
        Platform.runLater(() ->
                volLabel.setText(String.valueOf(Math.round(volume * 100)) + "%"));


    }

    public void addVolume(double value) {
        if (muted) {
            setVolume(mutedVolume + value);
        } else {
            setVolume(getSliderPosition() + value);
        }
    }

    private void setButtonIcon(double volume) {
        Platform.runLater(() -> {
            if (muted) {
                volButton.setText(IconFont.ICON_MUTE);
            } else if (volume == 0.0) {
                volButton.setText(IconFont.ICON_SOUND_OFF);
            } else if (volume < 0.5) {
                volButton.setText(IconFont.ICON_SOUND_HALF);
            } else {
                volButton.setText(IconFont.ICON_SOUND_MAX);
            }
        });
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

    private Timer volumeChangeTimer;

    public void volumePressed(double value) {
        if (volumeChangeTimer != null) {
            volumeChangeTimer.cancel();
            volumeChangeTimer.purge();
        }
        volumeChangeTimer = new Timer();
        volumeChangeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                addVolume(value);
                if (connection != null) {
                    connection.sendMessage(Connection.VOLUME, String.valueOf(Math.round(volume * 100)) + "%");
                } else {
                    volumeChangeTimer.cancel();
                    volumeChangeTimer.purge();
                }
            }
        }, 200, 500);
    }

    public void volumeReleased() {
        if (volumeChangeTimer != null) {
            volumeChangeTimer.cancel();
            volumeChangeTimer.purge();
        }

        if (connection != null) {
            connection.sendMessage(Connection.VOLUME, String.valueOf(Math.round(volume * 100)) + "%");
        }
    }

}
