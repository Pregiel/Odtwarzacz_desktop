package odtwarzacz.Playlist.Queue;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class QueueElement {
    private int playlistIndex;
    private String title;

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

    @Override
    public String toString() {
        return playlistIndex +". " + title;
    }
}
