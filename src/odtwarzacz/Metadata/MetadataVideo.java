/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Metadata;

import it.sauronsoftware.jave.*;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;

/**
 * @author Pregiel
 */
public class MetadataVideo extends Metadata {
    private int bitRate, width, height;
    private float frameRate;

    public MetadataVideo() {

    }

    @Override
    public void generate(File file) {
        Encoder encoder = new Encoder();

        try {
            MultimediaInfo info = encoder.getInfo(file);
            setDuration(new Duration(info.getDuration()));

            VideoInfo videoInfo = info.getVideo();
            setBitRate(videoInfo.getBitRate());
            setFrameRate(videoInfo.getFrameRate());
            setSize(videoInfo.getSize().getWidth(), videoInfo.getSize().getHeight());

        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }


    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(float frameRate) {
        this.frameRate = frameRate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "MetadataVideo{" +
                "title=" + getTitle() +
                ", duration=" + getDuration() +
                ", bitRate=" + bitRate +
                ", width=" + width +
                ", height=" + height +
                ", frameRate=" + frameRate +
                '}';
    }
}
