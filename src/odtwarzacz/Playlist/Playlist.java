/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import odtwarzacz.Connection.Connection;
import odtwarzacz.MainFXMLController;
import odtwarzacz.MyLocale;
import odtwarzacz.Odtwarzacz;

import static odtwarzacz.Connection.Connection.PLAYLIST_SEND;

/**
 * @author Pregiel
 */
public class Playlist {

    private static final String DEFAULT_PLAYLIST = "playlist.properties";

    private BorderPane pane;
    private SplitPane splitPane;
    private PlaylistProperties playlistProperties;
    private List<String> playlistList;
    private List<PlaylistElement> playlistElementList;
    private VBox playlistPane;

    private int playlistIndex;

    private boolean random;

    private MainFXMLController mainController;

    public Playlist(MainFXMLController mainController) {
        this.playlistProperties = new PlaylistProperties(DEFAULT_PLAYLIST);
        this.mainController = mainController;
        playlistElementList = new ArrayList<>();

        this.random = Boolean.parseBoolean(String.valueOf(Odtwarzacz.getConfig().get("random")));

        loadPlaylistList();

    }

    public void makePlaylistPane() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PlaylistFXML.fxml"), resourceBundle);
        this.pane = loader.load();

    }

    public void setPlaylistPane(VBox playlistPane) {
        this.playlistPane = playlistPane;

    }

    public BorderPane getScrollPane() {
        return pane;
    }

    public void hide() {
        splitPane.getItems().remove(getScrollPane());
        Odtwarzacz.getConfig().setProperty("playlist.visible", "false");
        Odtwarzacz.getConfig().save();
    }

    public void show() {
        splitPane.getItems().add(1, getScrollPane());

        splitPane.setDividerPositions(Double.valueOf(Odtwarzacz.getConfig().getProperty("playlist.divider")));

        Odtwarzacz.getConfig().setProperty("playlist.visible", "true");
        Odtwarzacz.getConfig().save();

//        splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
//            Odtwarzacz.getConfig().setProperty("playlist.divider", String.valueOf(newValue));
//            System.out.println(newValue);
//            Odtwarzacz.getConfig().save();
//        });
    }

    public void toogle() {
        if (Odtwarzacz.getConfig().getProperty("playlist.visible").equals("true")) {
            hide();
        } else {
            show();
        }
    }

    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public void loadPlaylist() {
        playlistPane.getChildren().clear();
        playlistElementList.clear();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

        int i = 1;
        for (String s : playlistList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PlaylistElementFXML.fxml"), resourceBundle);

                PlaylistElement element = new PlaylistElement(i++, s, loader.load());
                playlistElementList.add(element);

                playlistPane.getChildren().add(element.getPane());
            } catch (IOException ex) {
                Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (mainController.getConnection() != null) {
            mainController.getConnection().sendMessage(PLAYLIST_SEND, MainFXMLController.getPlaylist().toMessage());
        }
    }

    public void loadPlaylistList() {
        this.playlistList = playlistProperties.getArray();
    }

    public PlaylistProperties getPlaylistProperties() {
        return playlistProperties;
    }

    public void add(File file) {
        playlistList.add(file.getAbsolutePath());
        save();
        loadPlaylist();
    }

    public void addNew() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", MainFXMLController.SUPPORTED_VIDEO),
                new FileChooser.ExtensionFilter("Audio Files", MainFXMLController.SUPPORTED_AUDIO),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        List<File> list = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {
            for (File file : list) {
                add(file);
            }
        }
    }

    public void removeSelected() {
        int removedAmount = 0;
        for (PlaylistElement element : playlistElementList) {
            if (element.isSelected()) {
                playlistList.remove((element.getIndex() - 1) - removedAmount++);

            }
        }
        save();
        loadPlaylist();
    }

    public void save() {
        getPlaylistProperties().setArray(playlistList);
        getPlaylistProperties().save();
    }

    public void unselectAll() {
        playlistElementList.forEach((t) -> {
            t.setSelected(false);
        });
    }

    public void selectAll() {
        playlistElementList.forEach((t) -> {
            t.setSelected(true);
        });
    }

    public void playAll() {
        if (playlistList.size() > 0) {
            play(1);
        }
    }

    public void play(int index) {
        mainController.loadFile(new File(playlistList.get(index - 1)), index);
        playlistElementList.forEach((t) -> {
            if (t.getIndex() == index) {
                t.setPlaying(true);
            } else {
                t.setPlaying(false);
            }
        });
    }

    public void playNext() {
        nextPlaylistIndex();
        if (getPlaylistIndex() <= playlistList.size()) {
            play(getPlaylistIndex());
        } else {
            setNoPlayAll();
        }
    }

    public void setNoPlayAll() {
        playlistElementList.forEach((t) -> {
            t.setPlaying(false);

        });
    }

    public List<String> getPlaylistList() {
        return playlistList;
    }

    public int getPlaylistIndex() {
        return playlistIndex;
    }

    private void nextPlaylistIndex() {
        if (random) {
            int newIndex;
            do {
                newIndex = new Random().nextInt(playlistList.size()) + 1;
            } while (newIndex == getPlaylistIndex());
            setPlaylistIndex(newIndex);
        } else {
            boolean next = true;
            do {
                playlistIndex++;
                if (playlistElementList.get(playlistIndex - 1).isPlayable()) {
                    next = false;
                } else if (playlistIndex == playlistElementList.size()) {
                    next = false;
                    playlistIndex++;
                }

            } while (next);
        }
    }

    public void setPlaylistIndex(int playlistIndex) {
        this.playlistIndex = playlistIndex;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
        Odtwarzacz.getConfig().setProperty("random", String.valueOf(random));
        Odtwarzacz.getConfig().save();

    }

    public String toMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : playlistList) {
            stringBuilder.append(s).append(Connection.SEPARATOR);
        }

        return stringBuilder.toString();
    }
}
