/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.File;

import javafx.collections.MapChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Metadata.MetadataMusic;

/**
 *
 * @author Pregiel
 */
public class PlaylistElement {

    private static final String CSS_BACKGROUND_SELECTED = "background-selected";
    private static final String CSS_TITLE_PLAYING = "title-playing";

    private int index;

    private GridPane pane;
    private CheckBox songCheckbox;
    private Label titleLabel;

    private boolean selected;
    private boolean playing;
    private boolean playable;

    private MetadataMusic metadata;

    private File file;

    public PlaylistElement(int index, String path, GridPane pane) {
        this.index = index;
        this.pane = pane;
        this.titleLabel = (Label) pane.lookup("#songName");
        this.songCheckbox = (CheckBox) pane.lookup("#songCheckbox");
        this.songCheckbox.selectedProperty().set(true);
        this.file = new File(path);

        setSelected(false);
        setPlaying(false);

        metadata = new MetadataMusic();
        generateMetadata();

        this.titleLabel.setText(generateLabel());

        this.pane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    MainFXMLController.getPlaylist().play(index);
                } else if (event.getClickCount() == 1) {
                    if (!event.isControlDown()) {
                        MainFXMLController.getPlaylist().unselectAll();
                        setSelected(true);
                    } else {
                        setSelected(!isSelected());
                    }
                }
            }
        });
    }

    private String generateLabel() {

        String label = index + ". ";
        if (metadata.getTitle() != null) {
            if (metadata.getArtist() != null) {
                {
                    label = label + metadata.getArtist() + " - ";
                }
                label = label + metadata.getTitle();
            }
        } else {
            label = label + file.getName();
        }

        return label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;

        if (playing) {
            titleLabel.getStyleClass().add(CSS_TITLE_PLAYING);
        } else {
            titleLabel.getStyleClass().remove(CSS_TITLE_PLAYING);
        }
    }

    public boolean isPlayable() {
        return getSongCheckbox().isSelected();
    }
    
    

    public void setSelected(boolean selected) {
        this.selected = selected;

//        pane.getStyleClass().removeAll("playlist-element", "playlist-element-selected");
        if (selected) {
            pane.getStyleClass().add(CSS_BACKGROUND_SELECTED);
        } else {
//            pane.getStyleClass().add("playlist-element");
            pane.getStyleClass().remove(CSS_BACKGROUND_SELECTED);
        }
    }

    public void setSongCheckbox(CheckBox songCheckbox) {
        this.songCheckbox = songCheckbox;
    }

    public GridPane getPane() {
        return pane;
    }

    public CheckBox getSongCheckbox() {
        return songCheckbox;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public MetadataMusic getMetadata() {
        return metadata;
    }

    private void generateMetadata() {

        Media media = new Media(file.toURI().toString());
        media.getMetadata().addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
                if (change.wasAdded()) {
                    handleMetadata(change.getKey(), change.getValueAdded());
                    titleLabel.setText(generateLabel());
                }
            }
        });

    }

    private void handleMetadata(String key, Object value) {
        switch (key) {
            case "title":
                metadata.setTitle(value.toString());
                break;

            case "artist":
                metadata.setArtist(value.toString());
                break;

            case "album":
                metadata.setAlbum(value.toString());
                break;

        }
    }

}
