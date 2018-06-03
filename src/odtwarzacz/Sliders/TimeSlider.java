/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Sliders;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Pregiel
 */
public class TimeSlider extends CustomSlider {

    private final MediaPlayer mediaPlayer;

    public TimeSlider(AnchorPane slider, Pane track, MediaPlayer mediaPlayer) {
        super(slider, track);
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        setIsChanging(true);
        mediaPlayer.pause();
        moveTo(event);
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        setIsChanging(false);
        moveTo(event);
        mediaPlayer.play();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        moveTo(event);
    }
    
    public void moveTo(double value) {
        setSliderPosition(value);
        mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(value));
    }

    public void moveTo(MouseEvent event) {
        setSliderPosition(event);
        mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(
                getTrack().getWidth() / getBackTrack().getWidth()));
    }
    
    public void moveTo(Duration time) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(time));
    }
    
    

}
