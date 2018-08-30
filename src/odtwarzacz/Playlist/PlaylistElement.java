/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.File;
import java.util.Arrays;

import it.sauronsoftware.jave.*;
import javafx.collections.MapChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.util.Duration;
import odtwarzacz.FileType;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Metadata.Metadata;
import odtwarzacz.Metadata.MetadataAudio;
import odtwarzacz.Metadata.MetadataVideo;
import odtwarzacz.Utils;

/**
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

    private Metadata metadata;

    private File file;

    private FileType fileType;


    public PlaylistElement(int index, String path, GridPane pane) {
        this.index = index;
        this.pane = pane;
        this.titleLabel = (Label) pane.lookup("#songName");
        this.songCheckbox = (CheckBox) pane.lookup("#songCheckbox");
        this.songCheckbox.selectedProperty().set(true);
        this.file = new File(path);

        setSelected(false);
        setPlaying(false);

        if (Arrays.asList(MainFXMLController.SUPPORTED_AUDIO).contains("*." + Utils.getExtension(path).toUpperCase())) {
            metadata = new MetadataAudio();
            fileType = FileType.AUDIO;
        } else if (Arrays.asList(MainFXMLController.SUPPORTED_VIDEO).contains("*." + Utils.getExtension(path).toUpperCase())) {
            metadata = new MetadataVideo();
            fileType = FileType.VIDEO;
        } else {
            fileType = FileType.NONE;
        }
        metadata.generate(file, () -> titleLabel.setText(index + ". " + metadata.generateLabel()));
//        generateMetadata();

        this.titleLabel.setText(index + ". " + metadata.generateLabel());

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

//    private String generateLabel() {
//
//        StringBuilder label = new StringBuilder();
//
//        label.append(index).append(". ");
//
//
//        switch (fileType) {
//            case AUDIO:
//                if (metadata.getTitle() != null) {
//                    if (((MetadataAudio) metadata).getArtist() != null) {
//                        {
//                            label.append(((MetadataAudio) metadata).getArtist()).append(" - ");
//                        }
//                        label.append(metadata.getTitle());
//                    }
//                } else {
//                    label.append(file.getName());
//                }
//                break;
//
//            case VIDEO:
//                if (metadata.getTitle() != null) {
//                    label.append(metadata.getTitle());
//                } else {
//                    label.append(file.getName());
//                }
//                break;
//
//            default:
//                label.append(file.getName());
////                    label.append(metadata.getDuration());
//
//        }
//        return label.toString();
//    }

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
        if (playing) {
            if (!this.playing) {
                titleLabel.getStyleClass().add(CSS_TITLE_PLAYING);
            }
        } else {
            titleLabel.getStyleClass().remove(CSS_TITLE_PLAYING);
        }

        this.playing = playing;
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

    public Metadata getMetadata() {
        return metadata;
    }

//    private void generateMetadata() {
//        Media media = new Media(file.toURI().toString());
//        media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
//            if (change.wasAdded()) {
//                handleMetadata(change.getKey(), change.getValueAdded());
//                titleLabel.setText(generateLabel());
//            }
//        });
//
//    }
//
//    private void handleMetadata(String key, Object value) {
//        switch (key) {
//            case "title":
//                metadata.setTitle(value.toString());
//                break;
//
//            case "artist":
//                ((MetadataAudio) metadata).setArtist(value.toString());
//                break;
//
//            case "album":
//                ((MetadataAudio) metadata).setAlbum(value.toString());
//                break;
//
//        }
//    }

}
