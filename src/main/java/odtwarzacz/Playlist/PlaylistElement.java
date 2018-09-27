/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import odtwarzacz.Odtwarzacz;
import odtwarzacz.Playlist.Queue.QueueFXMLController;
import odtwarzacz.Theme;
import odtwarzacz.Utils.*;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Metadata.Metadata;
import odtwarzacz.Metadata.MetadataAudio;
import odtwarzacz.Metadata.MetadataVideo;
import odtwarzacz.Playlist.Queue.QueueElement;

import static odtwarzacz.MainFXMLController.getPlaylist;

/**
 * @author Pregiel
 */
public class PlaylistElement {

    private static final String CSS_BACKGROUND_SELECTED = "background-selected";
    private static final String CSS_TITLE_PLAYING = "title-playing";
    private static final String CSS_TITLE_NOTFOUND = "title-notfound";

    private int index;

    private GridPane pane;
    private CheckBox songCheckbox;
    private Label titleLabel, queueIndexLabel, durationLabel;
    private Button queueAddBtn, queueRemoveBtn;
    private Tooltip titleTooltip;
    private ContextMenu contextMenu;

    private boolean selected, playing, notFounded;

    private Metadata metadata;

    private File file;

    private FileType fileType;


    public PlaylistElement(int index, String path, GridPane pane) {
        this.pane = pane;
        this.pane.setStyle(Theme.getStyleConst(Theme.PLAYLISTELEMENT_FXML));
        setIndex(index);

        Theme.getInstance().addPlayListElementNode(pane);

        titleLabel = (Label) pane.lookup("#songName");
        songCheckbox = (CheckBox) pane.lookup("#songCheckbox");

        queueIndexLabel = (Label) pane.lookup("#queueIndex");
        queueAddBtn = (Button) pane.lookup("#queueAdd");
        queueRemoveBtn = (Button) pane.lookup("#queueRemove");

        durationLabel = (Label) pane.lookup("#durationLabel");

        titleTooltip = titleLabel.getTooltip();

        initContextMenu();

        songCheckbox.selectedProperty().set(true);
        file = new File(path);

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

        if (file.exists()) {
            generateTitleLabel();
        } else {
            setNotFounded(true);
        }


        queueRemoveBtn.setDisable(true);

        queueAddBtn.setOnAction((event) -> addToQueue());

        queueRemoveBtn.setOnAction((event) -> removeFromQueue());

        if (index % 2 == 0) {
            pane.getStyleClass().clear();
            pane.getStyleClass().add("background");
        } else {
            pane.getStyleClass().clear();
            pane.getStyleClass().add("background-alter");
        }


    }

    private MenuItem playableMenuItem;

    public void initContextMenu() {
        contextMenu = new ContextMenu();

        MenuItem play = new MenuItem(Utils.getString("playlist.element.play"));
        MenuItem remove = new MenuItem(Utils.getString("playlist.element.remove"));
        MenuItem removeSelected = new MenuItem(Utils.getString("playlist.element.removeselected"));
        MenuItem properties = new MenuItem(Utils.getString("playlist.element.properties"));
        MenuItem fileLocation = new MenuItem(Utils.getString("playlist.element.fileLocation"));
        playableMenuItem = new MenuItem(Utils.getString("playlist.element.disable"));

        Menu queue = new Menu(Utils.getString("player.queue"));
        MenuItem queueAdd = new MenuItem(Utils.getString("playlist.element.addqueue"));
        MenuItem queueRemove = new MenuItem(Utils.getString("queue.removeFromQueue"));
        MenuItem queueClear = new MenuItem(Utils.getString("playlist.element.clearqueue"));
        MenuItem queueShow = new MenuItem(Utils.getString("playlist.showQueue"));


        play.setOnAction(event -> getPlaylist().play(index));

        remove.setOnAction(event -> getPlaylist().remove(index));

        removeSelected.setOnAction(event -> getPlaylist().removeSelected());

        properties.setOnAction(event -> {
            makePropertiesWindow();
        });

        fileLocation.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(file.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        playableMenuItem.setOnAction(event -> songCheckbox.fire());

        queueAdd.setOnAction(event -> addToQueue());

        queueRemove.setOnAction(event -> removeFromQueue());

        queueClear.setOnAction(event -> {
            for (QueueElement queueElement : getPlaylist().getQueue().getQueueElements()) {
                getPlaylist().getPlaylistElementList().get(queueElement.getPlaylistIndex() - 1).removeQueueLabel();
            }
            getPlaylist().getQueue().getQueueElements().clear();
        });

        queueShow.setOnAction(event -> getPlaylist().getPlaylistFXMLController().showQueue(event));


        queue.getItems().addAll(queueAdd, new SeparatorMenuItem(), queueRemove, queueClear,
                new SeparatorMenuItem(), queueShow);

        contextMenu.getItems().addAll(play, new SeparatorMenuItem(), properties, fileLocation, queue,
                new SeparatorMenuItem(), remove, removeSelected, new SeparatorMenuItem(), playableMenuItem);

    }

    private void makePropertiesWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/WindowFXML.fxml"), Utils.getTranslationsBundle());

            BorderPane root = loader.load();
            root.setStyle(Theme.getStyleConst(Theme.WINDOW_FXML));
            ((Label) root.lookup("#windowTitle")).setText(Utils.getString("player.queue"));

            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/Layouts/PropertiesFXML.fxml"), Utils.getTranslationsBundle());
            AnchorPane propertiesPane = loader2.load();

            ((AnchorPane) root.lookup("#center")).getChildren().add(propertiesPane);

            AnchorPane.setTopAnchor(propertiesPane, 0.0);
            AnchorPane.setRightAnchor(propertiesPane, 0.0);
            AnchorPane.setLeftAnchor(propertiesPane, 0.0);
            AnchorPane.setBottomAnchor(propertiesPane, 0.0);

            CustomStage stage = new CustomStage();
            stage.setTitle(Utils.getString("player.properties"));
            stage.setScene(new Scene(root, 500, 300));
            stage.initStyle(StageStyle.UNDECORATED);

            ResizeHelper.addResizeListener(stage, 300, 200, 1.7976931348623157E308D, 1.7976931348623157E308D);

            Platform.runLater(() -> {
                GridPane gridPane = ((GridPane) propertiesPane.lookup("#scrollPane").lookup("#gridPane"));
                insertProperties(gridPane);
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertProperties(GridPane gridPane) {
        propertyAmount = 0;

        insertProperty(gridPane, Utils.getString("properties.name"), metadata.getName());
        insertProperty(gridPane, Utils.getString("properties.path"), file.getAbsolutePath());
        insertProperty(gridPane, Utils.getString("properties.duration"), durationLabel.getText());

        if (metadata instanceof MetadataAudio) {
            insertProperty(gridPane, Utils.getString("properties.artist"), String.valueOf(((MetadataAudio) metadata).getArtist()));
            insertProperty(gridPane, Utils.getString("properties.album"), String.valueOf(((MetadataAudio) metadata).getAlbum()));
            if (((MetadataAudio) metadata).getBitRate() != -1)
                insertProperty(gridPane, Utils.getString("properties.bitRate"), String.valueOf(((MetadataAudio) metadata).getBitRate()) + " kbps");
            insertProperty(gridPane, Utils.getString("properties.channels"), String.valueOf(((MetadataAudio) metadata).getChannels()) +
                    (((MetadataAudio) metadata).getChannels() == 2 ? " (stereo)" : (((MetadataAudio) metadata).getChannels() == 1 ? " (mono)" : "")));
            insertProperty(gridPane, Utils.getString("properties.samplingRate"), String.valueOf(((MetadataAudio) metadata).getSamplingRate()) + " Hz");
        } else if (metadata instanceof MetadataVideo) {
            if (((MetadataVideo) metadata).getBitRate() != -1)
                insertProperty(gridPane, Utils.getString("properties.bitRate"), String.valueOf(((MetadataVideo) metadata).getBitRate()) + " kbps");
            insertProperty(gridPane, Utils.getString("properties.width"), String.valueOf(((MetadataVideo) metadata).getWidth()));
            insertProperty(gridPane, Utils.getString("properties.height"), String.valueOf(((MetadataVideo) metadata).getHeight()));
            insertProperty(gridPane, Utils.getString("properties.frameRate"), String.valueOf(((MetadataVideo) metadata).getFrameRate()));

        }

    }

    private int propertyAmount = 0;

    private void insertProperty(GridPane gridPane, String propertyName, String propertyValue) {
        Label name = new Label(propertyName);
        name.getStyleClass().add("property-name");
        name.setWrapText(true);

        Label value = new Label(propertyValue);
        value.getStyleClass().add("property-value");
        value.setWrapText(true);

        gridPane.add(name, 0, propertyAmount);
        gridPane.add(value, 1, propertyAmount);

        propertyAmount++;

    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void generateTitleLabel() {
        titleLabel.setText(index + ". " + metadata.getName());
        if (notFounded) {
            titleTooltip.setText(Utils.getString("file.nofile") + " " + file.getAbsolutePath());
        } else {
            titleTooltip.setText(metadata.getName());
        }
    }

    public void setDurationLabel() {
        long s = (long) metadata.getDuration().toSeconds();
        durationLabel.setText(String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60)));
    }

    public void setStyle(int value) {
        if (value == 1) {
            pane.getStyleClass().clear();
            pane.getStyleClass().add("background");
        } else {
            pane.getStyleClass().clear();
            pane.getStyleClass().add("background-alter");
        }
    }

    public void removeFromQueue() {
        int elementIndex = getPlaylist().getQueue().removeLastElementByPlaylistIndex(index);

        if (elementIndex > -1) {
            for (int i = elementIndex; i < getPlaylist().getQueue().getQueueElements().size(); i++) {
                getPlaylist().getPlaylistElementList().get(
                        getPlaylist().getQueue().getQueueElements().get(i).getPlaylistIndex() - 1
                ).setQueueLabel();
            }
        }

        setQueueLabel();

        getPlaylist().refreshQueueView();

        for (QueueElement queueElement : getPlaylist().getQueue().getQueueElements()) {
            if (queueElement.getPlaylistIndex() == index) {
                return;
            }
        }

        queueRemoveBtn.setDisable(true);
    }

    public void removeQueueLabel() {
        queueIndexLabel.setTooltip(null);
        queueIndexLabel.setText("");

        queueRemoveBtn.setDisable(true);
    }

    public void addToQueue() {
        getPlaylist().getQueue().addToQueue(new QueueElement(index, titleLabel.getText()));

        setQueueLabel();

        queueRemoveBtn.setDisable(false);
        getPlaylist().refreshQueueView();
    }

    private boolean hidden = false;

    public boolean isHidden() {
        return hidden;
    }

    public void hide() {
        hidden = true;
        pane.setVisible(false);
        pane.setManaged(false);
    }

    public void show() {
        hidden = false;
        pane.setVisible(true);
        pane.setManaged(true);
    }

    public void setQueueLabel() {
        queueIndexLabel.setTooltip(null);

        List<Integer> queueIndexes = new ArrayList<>();
        int i = 0;
        for (QueueElement queueElement : getPlaylist().getQueue().getQueueElements()) {
            if (queueElement.getPlaylistIndex() == index) {
                queueIndexes.add(i + 1);
            }
            i++;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (queueIndexes.size() == 0) {
        } else if (queueIndexes.size() == 1) {
            stringBuilder.append("[").append(queueIndexes.get(0).toString()).append("]");
        } else if (queueIndexes.size() < 4) {
            String prefix = "[";
            for (int ii : queueIndexes) {
                stringBuilder.append(prefix).append(ii);
                prefix = ", ";
            }
            stringBuilder.append("]");

        } else {
            stringBuilder.append("[")
                    .append(queueIndexes.get(0))
                    .append(" ... ")
                    .append(queueIndexes.get(queueIndexes.size() - 1))
                    .append("]");

            StringBuilder stringBuilder1 = new StringBuilder();
            String prefix = "[";
            for (int ii : queueIndexes) {
                stringBuilder1.append(prefix).append(ii);
                prefix = ", ";
            }
            stringBuilder1.append("]");
            Tooltip tooltip = new Tooltip();
            tooltip.setText(stringBuilder1.toString());
            queueIndexLabel.setTooltip(tooltip);
        }

        queueIndexLabel.setText(stringBuilder.toString());
    }

    public int getIndex() {
        return index;
    }

    private EventHandler<MouseEvent> clickEventHandler;

    public void setIndex(int newIndex) {
        index = newIndex;


        pane.setOnDragDetected(event -> {
            Dragboard dragboard = pane.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(index));
            dragboard.setContent(content);

            getPlaylist().unselectAll();
            setSelected(true);

            event.consume();
        });

        pane.setOnDragEntered(event -> {
            if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
                if (event.getY() > pane.getHeight() / 2) {
                    pane.getStyleClass().add("background-drag-bottom");
                } else {
                    pane.getStyleClass().add("background-drag-top");
                }
            }
            event.consume();
        });

        pane.setOnDragExited(event -> {
            pane.getStyleClass().remove("background-drag-top");
            pane.getStyleClass().remove("background-drag-bottom");
            event.consume();
        });

        pane.setOnDragOver(event -> {
            if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            if (event.getY() > pane.getHeight() / 2) {
                if (!pane.getStyleClass().contains("background-drag-bottom")) {
                    pane.getStyleClass().remove("background-drag-top");
                    pane.getStyleClass().add("background-drag-bottom");
                }
            } else {
                if (!pane.getStyleClass().contains("background-drag-top")) {
                    pane.getStyleClass().remove("background-drag-bottom");
                    pane.getStyleClass().add("background-drag-top");
                }
            }

            event.consume();
        });

        pane.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                if (event.getY() > pane.getHeight() / 2) {
                    Utils.print(dragboard.getString(), "to", index + 1);
                    getPlaylist().moveToPosition(Integer.parseInt(dragboard.getString()), index + 1);
                } else {
                    Utils.print(dragboard.getString(), "to", index);
                    getPlaylist().moveToPosition(Integer.parseInt(dragboard.getString()), index);
                }
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        if (clickEventHandler != null)
            pane.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEventHandler);

        clickEventHandler = (MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (!event.isControlDown()) {
                    if (event.getClickCount() == 1) {
                        getPlaylist().unselectAll();
                        setSelected(true);
                    } else {
                        getPlaylist().play(index);
                    }
                } else {
                    setSelected(!isSelected());
                }
            } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                if (!event.isControlDown()) {
                    getPlaylist().unselectAll();
                }
                setSelected(true);
                contextMenu.show(pane, event.getScreenX(), event.getScreenY());
                playableMenuItem.setText((getPlayable() ? Utils.getString("playlist.element.disable")
                        : Utils.getString("playlist.element.enable")));
            } else if (event.getButton().equals(MouseButton.MIDDLE)) {
                getPlaylist().unselectAll();
                setSelected(true);
                if (event.isShiftDown()) {
                    removeFromQueue();
                } else {
                    addToQueue();
                }
            }
        };

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEventHandler);
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

    public boolean getPlayable() {
        return getSongCheckbox().isSelected();
    }


    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected) {
            if (!pane.getStyleClass().contains(CSS_BACKGROUND_SELECTED))
                pane.getStyleClass().add(CSS_BACKGROUND_SELECTED);
        } else {
            pane.getStyleClass().remove(CSS_BACKGROUND_SELECTED);
        }
    }

    public boolean isNotFounded() {
        return notFounded;
    }

    public void setNotFounded(boolean notFounded) {
        this.notFounded = notFounded;
        if (notFounded) {
            titleLabel.getStyleClass().add(CSS_TITLE_NOTFOUND);
        } else {
            titleLabel.getStyleClass().remove(CSS_TITLE_NOTFOUND);
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

}
