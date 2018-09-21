/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Metadata;


import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;
import java.util.function.Function;

/**
 *
 * @author Pregiel
 */
public abstract class Metadata {
    private Duration duration;
    private String title;
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

    public abstract void generateMetadata(File file);


    public void generate(File file, Runnable updateRunnable) {
        this.file = file;
        generateMetadata(file);
        Media media = new Media(file.toURI().toString());
        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (change.wasAdded()) {
                handleMetadata(change.getKey(), change.getValueAdded());
//                titleLabel.setText(generateLabel());
                updateRunnable.run();
            }
        });
    }

    private void handleMetadata(String key, Object value) {
        switch (key) {
            case "title":
                setTitle(value.toString());
                break;

            case "artist":
                ((MetadataAudio) this).setArtist(value.toString());
                break;

            case "album":
                ((MetadataAudio) this).setAlbum(value.toString());
                break;

        }
    }

    public String generateLabel() {
        StringBuilder label = new StringBuilder();

        label.append(getFile().getName());

        return label.toString();
    }

}
