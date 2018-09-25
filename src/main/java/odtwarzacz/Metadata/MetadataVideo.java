/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Metadata;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import javafx.util.Duration;
import odtwarzacz.Playlist.PlaylistProperties;

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
    public void generateMetadata(File file) {
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

    @Override
    public void setProperties(int index, PlaylistProperties playlistProperties) {
        if (playlistProperties != null) {
            playlistProperties.setProperty(index, DURATION, String.valueOf(getDuration().toMillis())+"ms");
            playlistProperties.setProperty(index, BIT_RATE, bitRate);
            playlistProperties.setProperty(index, WIDTH, width);
            playlistProperties.setProperty(index, HEIGHT, height);
            playlistProperties.setProperty(index, FRAME_RATE, frameRate);
            playlistProperties.save();
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
    public void setMetadata(int index, PlaylistProperties playlistProperties) {
        super.setMetadata(index, playlistProperties);
        bitRate = Integer.parseInt(playlistProperties.getProperty(index, BIT_RATE));
        width = Integer.parseInt(playlistProperties.getProperty(index, WIDTH));
        height = Integer.parseInt(playlistProperties.getProperty(index, HEIGHT));
        frameRate = Float.parseFloat(playlistProperties.getProperty(index, FRAME_RATE));
    }

    @Override
    public String generateLabel() {
        StringBuilder label = new StringBuilder();

        if (getTitle() != null) {
            label.append(getTitle());
        } else {
            label.append(getFile().getName());
        }

        return label.toString();
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
