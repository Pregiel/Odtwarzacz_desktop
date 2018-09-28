package odtwarzacz.Playlist.Queue;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import odtwarzacz.Utils.Utils;

import static odtwarzacz.MainFXMLController.getPlaylist;

public class QueueElement {
    private static final String CSS_BACKGROUND_SELECTED = "background-selected";

    private int playlistIndex, queueIndex;
    private String title;
    private boolean selected;

    private GridPane pane;

    public QueueElement(int queueIndex, int playlistIndex, String title) {
        this.queueIndex = queueIndex;
        this.playlistIndex = playlistIndex;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlaylistIndex() {
        return playlistIndex;
    }

    public void setPlaylistIndex(int playlistIndex) {
        this.playlistIndex = playlistIndex;
    }

    public void setPane(GridPane newPane) {
        pane = newPane;

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.isControlDown()) {
                    setSelected(!isSelected());
                } else {
                    getPlaylist().getQueueFXMLController().unselectAll();
                    setSelected(true);
                }
            }
        });

        pane.setOnDragDetected(event -> {
            Dragboard dragboard = pane.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(queueIndex));
            dragboard.setContent(content);

            getPlaylist().getQueueFXMLController().unselectAll();
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
                    getPlaylist().getQueue().moveToIndex(Integer.parseInt(dragboard.getString()), queueIndex + 1);
                } else {
                    getPlaylist().getQueue().moveToIndex(Integer.parseInt(dragboard.getString()), queueIndex);
                }

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public boolean isSelected() {
        return selected;
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

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    @Override
    public String toString() {
        return playlistIndex + ". " + title;
    }
}
