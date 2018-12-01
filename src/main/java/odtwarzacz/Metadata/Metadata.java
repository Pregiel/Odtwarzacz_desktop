/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Metadata;


import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.util.Duration;
import odtwarzacz.Playlist.PlaylistProperties;

import java.io.File;
import java.util.function.Function;

/**
 * @author Pregiel
 */
public abstract class Metadata {
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String DURATION = "duration";
    public static final String BIT_RATE = "bit_rate";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String FRAME_RATE = "frame_rate";
    public static final String CHANNELS = "channels";
    public static final String SAMPLING_RATE = "sampling_rate";
    public static final String NAME = "name";


    private Duration duration;
    private String title, name;
    private File file;

    public Metadata() {
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void generateMetadata(File file);

    public abstract void setProperties(int index, PlaylistProperties playlistProperties);


    public void generate(File file, Runnable updateRunnable) {
        this.file = file;
        generateMetadata(file);
        Media media = new Media(file.toURI().toString());
        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (change.wasAdded()) {
                handleMetadata(change.getKey(), change.getValueAdded(), 0, null);
//                titleLabel.setText(generateLabel());
                updateRunnable.run();
            }
        });
    }

    public void generate(File file, int index, PlaylistProperties playlistProperties, Runnable updateRunnable) {
        this.file = file;
        generateMetadata(file);
        setProperties(index, playlistProperties);
        Media media = new Media(file.toURI().toString());
        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (change.wasAdded()) {
                handleMetadata(change.getKey(), change.getValueAdded(), index, playlistProperties);
                name = generateLabel();
                playlistProperties.setProperty(index, NAME, name);
                playlistProperties.save();
                updateRunnable.run();
            }
        });
        name = generateLabel();
        playlistProperties.setProperty(index, NAME, name);
        playlistProperties.save();
        updateRunnable.run();
    }

    public void setMetadata(int index, PlaylistProperties playlistProperties) {
        name = playlistProperties.getProperty(index, NAME);
        String durationProperty = playlistProperties.getProperty(index, DURATION);
        if (durationProperty != null) {
            duration = Duration.valueOf(durationProperty);
        }
        title = playlistProperties.getProperty(index, TITLE);
    }

    private void handleMetadata(String key, Object value, int index, PlaylistProperties playlistProperties) {
        switch (key) {
            case "title":
                setTitle(value.toString());
                if (playlistProperties != null) {
                    playlistProperties.setProperty(index, TITLE, value);
                    playlistProperties.save();
                }
                break;

            case "artist":
                ((MetadataAudio) this).setArtist(value.toString());
                if (playlistProperties != null) {
                    playlistProperties.setProperty(index, ARTIST, value);
                    playlistProperties.save();
                }
                break;

            case "album":
                ((MetadataAudio) this).setAlbum(value.toString());
                if (playlistProperties != null) {
                    playlistProperties.setProperty(index, ALBUM, value);
                    playlistProperties.save();
                }
                break;

        }
    }

    public String generateLabel() {
        return getFile().getName();
    }

}
