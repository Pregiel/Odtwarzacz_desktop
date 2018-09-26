package odtwarzacz.Playlist.Queue;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import odtwarzacz.Utils.Utils;

import static odtwarzacz.MainFXMLController.getPlaylist;

public class QueueElement {
    private static final String CSS_BACKGROUND_SELECTED = "background-selected";

    private int playlistIndex;
    private String title;
    private boolean selected;

    private GridPane pane;

    public QueueElement(int playlistIndex, String title) {
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

    public void setPane(GridPane pane) {
        this.pane = pane;

        this.pane.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.isControlDown()) {
                    setSelected(!isSelected());
                } else {
                    getPlaylist().getQueueFXMLController().unselectAll();
                    setSelected(true);
                }
            }
        });
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

    @Override
    public String toString() {
        return playlistIndex + ". " + title;
    }
}
