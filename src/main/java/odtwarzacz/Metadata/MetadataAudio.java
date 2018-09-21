/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Metadata;

import it.sauronsoftware.jave.AudioInfo;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import javafx.util.Duration;

import java.io.File;

/**
 * @author Pregiel
 */
public class MetadataAudio extends Metadata {
    private String artist;
    private String album;

    private int bitRate, channels, samplingRate;

    public MetadataAudio() {
    }

    @Override
    public void generateMetadata(File file) {
        Encoder encoder = new Encoder();

        try {
            MultimediaInfo info = encoder.getInfo(file);
            setDuration(new Duration(info.getDuration()));

            AudioInfo audioInfo = info.getAudio();
            setBitRate(audioInfo.getBitRate());
            setChannels(audioInfo.getChannels());
            setSamplingRate(audioInfo.getSamplingRate());
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    @Override
    public String generateLabel() {

        StringBuilder label = new StringBuilder();

        if (getTitle() != null) {
            if (getArtist() != null) {
                {
                    label.append(getArtist()).append(" - ");
                }
                label.append(getTitle());
            }
        } else {
            label.append(getFile().getName());
        }

        return label.toString();
    }

    @Override
    public String toString() {
        return "MetadataAudio{" +
                "title=" + getTitle() +
                ", duration=" + getDuration() +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", bitRate=" + bitRate +
                ", channels=" + channels +
                ", samplingRate=" + samplingRate +
                '}';
    }
}
