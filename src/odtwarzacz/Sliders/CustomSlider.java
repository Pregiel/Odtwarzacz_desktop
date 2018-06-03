/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Sliders;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Pregiel
 */
public abstract class CustomSlider {

    private final Pane track;
    private final AnchorPane backTrack;
    private boolean isChanging = false;

    public CustomSlider(AnchorPane slider, Pane sliderTrack) {
        backTrack = slider;
        track = sliderTrack;
        backTrack.setOnMousePressed((event) -> {
            onMousePressed(event);
        });
        backTrack.setOnMouseReleased((event) -> {
            onMouseReleased(event);
        });
        backTrack.setOnMouseDragged((event) -> {
            onMouseDragged(event);
        });

    }

    public Pane getTrack() {
        return track;
    }

    public AnchorPane getBackTrack() {
        return backTrack;
    }

    public void setIsChanging(boolean isChanging) {
        this.isChanging = isChanging;
    }
    
    public boolean isChanging() {
        return isChanging;
    }

    public abstract void onMousePressed(MouseEvent event);

    public abstract void onMouseReleased(MouseEvent event);

    public abstract void onMouseDragged(MouseEvent event);

    public void setSliderPosition(double value) {
        AnchorPane.setRightAnchor(getTrack(), getBackTrack().getWidth() * (1 - value));
    }
    

    public void setSliderPosition(MouseEvent event) {
        setSliderPosition(event.getX() / getBackTrack().getWidth());
    }

    public double getSliderPosition() {
        return track.getWidth() / backTrack.getWidth();
    }

    public double getWidth() {
        return backTrack.getWidth();
    }
}
