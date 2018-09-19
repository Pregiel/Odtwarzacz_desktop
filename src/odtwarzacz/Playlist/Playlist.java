/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import odtwarzacz.Connection.Connection;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Theme;
import odtwarzacz.Utils.ExpandableTimeTask;
import odtwarzacz.Utils.MyLocale;
import odtwarzacz.Odtwarzacz;
import odtwarzacz.Playlist.Queue.Queue;
import odtwarzacz.Playlist.Queue.QueueFXMLController;
import odtwarzacz.Utils.Utils;

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
    private PlaylistFXMLController playlistFXMLController;

    private Queue queue;

    private int playlistIndex;

    private boolean random;

    private MainFXMLController mainController;

    private QueueFXMLController queueFXMLController;

    public Playlist(MainFXMLController mainController) {
        this.playlistProperties = new PlaylistProperties(DEFAULT_PLAYLIST);
        this.mainController = mainController;
        playlistElementList = new ArrayList<>();
        queue = new Queue();

        this.random = Boolean.parseBoolean(String.valueOf(Odtwarzacz.getConfig().get("random")));


        loadPlaylistList();

    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueueFXMLController(QueueFXMLController queueFXMLController) {
        this.queueFXMLController = queueFXMLController;
    }

    public void refreshQueueView() {
        if (queueFXMLController != null) {
            queueFXMLController.loadQueue();
        }
    }

    public void makePlaylistPane() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Layouts/PlaylistFXML.fxml"), resourceBundle);

        pane = loader.load();
        pane.setStyle(Theme.getStyleConst(Theme.PLAYLIST_FXML));
        Theme.getInstance().setPlayListNode(pane);

        playlistFXMLController = loader.getController();
    }

    public void setPlaylistPane(VBox playlistPane) {
        this.playlistPane = playlistPane;

    }

    public BorderPane getPane() {
        return pane;
    }

    public void hide() {
        splitPane.getItems().remove(getPane());
        Odtwarzacz.getConfig().setProperty("playlist.visible", "false");
        Odtwarzacz.getConfig().save();
    }

    private double dividerPosition1;

    public double getDividerPosition() {
        return dividerPosition1;
    }

    public void show() {
        if (playlistWindow != null) {
            playlistWindow.close();
            playlistWindow = null;
        }

        splitPane.getItems().add(1, getPane());

        ExpandableTimeTask saveDividerPositionTast = new ExpandableTimeTask(new Runnable() {
            @Override
            public void run() {
                Odtwarzacz.getConfig().setProperty("playlist.divider", String.valueOf(pane.getWidth()));
                Odtwarzacz.getConfig().save();
            }
        }, 400);

        double dividerPosition = Double.parseDouble(Odtwarzacz.getConfig().getProperty("playlist.divider"));
        double windowWidth = Double.parseDouble(Odtwarzacz.getConfig().getProperty("window.width"));

        splitPane.setDividerPosition(0, 1 - (dividerPosition / windowWidth));
        System.out.println(dividerPosition + " " + windowWidth);

        splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            if (saveDividerPositionTast.isStarted() && !saveDividerPositionTast.isFinished()) {
                saveDividerPositionTast.resume();
            } else {
                saveDividerPositionTast.start();
            }
        });



        Odtwarzacz.getConfig().setProperty("playlist.visible", "true");
        Odtwarzacz.getConfig().save();
    }

    private Stage playlistWindow;

    public Stage getPlaylistWindow() {
        return playlistWindow;
    }

    public void showUndock() {
        hide();
        if (playlistWindow != null) {
            playlistWindow.close();
        }

        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(getPane());

        AnchorPane.setTopAnchor(getPane(), 0.0);
        AnchorPane.setRightAnchor(getPane(), 0.0);
        AnchorPane.setLeftAnchor(getPane(), 0.0);
        AnchorPane.setBottomAnchor(getPane(), 0.0);

        playlistWindow = new Stage();
        playlistWindow.setTitle(Utils.getString("player.playlist"));
        playlistWindow.setScene(new Scene(pane, 400, 600));
        playlistWindow.setMinWidth(Odtwarzacz.PLAYLIST_MIN_WIDTH);
        playlistWindow.setMinHeight(Odtwarzacz.PLAYER_MIN_HEIGHT);
        playlistWindow.show();

    }

    public void toogle() {
        if (Odtwarzacz.getConfig().getProperty("playlist.visible").equals("true")) {
            ((Stage) pane.getScene().getWindow()).setMinWidth(Odtwarzacz.MEDIA_MIN_WIDTH);
            hide();
        } else {
            show();
            ((Stage) pane.getScene().getWindow()).setMinWidth(Odtwarzacz.PLAYER_MIN_WIDTH);
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../Layouts/PlaylistElementFXML.fxml"), resourceBundle);

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
        if (playlistList.contains(file.getAbsolutePath())) {
            ButtonType addButton = new ButtonType(Utils.getString("dialog.add"), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(Utils.getString("dialog.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    Utils.getString("playlist.alreadyAdded"),
                    addButton,
                    cancelButton);
            alert.setTitle(Utils.getString("playlist.alreadyAdded.title"));
            alert.setHeaderText(null);
//            alert.setContentText(Utils.getString("playlist.alreadyAdded"));


            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == cancelButton) {
                return;
            }
        }
        playlistList.add(file.getAbsolutePath());
        save();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadPlaylist();
            }
        });

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
        if (mainController.loadFile(new File(playlistList.get(index - 1)), index)) {
            playlistElementList.get(index - 1).setNotFounded(false);
            if (mainController.getConnection() != null) {
                mainController.getConnection().sendMessage(Connection.PLAYLIST_PLAYING_INDEX, playlistIndex);
            }
            playlistElementList.forEach((t) -> {
                if (t.getIndex() == index) {
                    t.setPlaying(true);
                } else {
                    t.setPlaying(false);
                }
            });
        } else {
            playlistElementList.get(index - 1).setNotFounded(true);
        }
    }

    public void playNext() {
        nextPlaylistIndex();
        if (getPlaylistIndex() <= playlistList.size()) {
            play(getPlaylistIndex());
        } else {
            if (mainController.getConnection() != null) {
                mainController.getConnection().sendMessage(Connection.PLAYLIST_PLAYING_INDEX, 0);
            }
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

    public List<PlaylistElement> getPlaylistElementList() {
        return playlistElementList;
    }

    private void nextPlaylistIndex() {
        if (queue.getQueueElements().size() > 0) {
            playlistIndex = queue.getQueueElements().get(0).getPlaylistIndex();
            queue.removeFirstElement();
            playlistElementList.forEach(PlaylistElement::setQueueLabel);
            if (playlistElementList.get(playlistIndex - 1).isNotFounded()) {
                nextPlaylistIndex();
            }
        } else if (random) {
            int newIndex;
            do {
                newIndex = new Random().nextInt(playlistList.size()) + 1;
            } while (newIndex == getPlaylistIndex());
            setPlaylistIndex(newIndex);
        } else {
            boolean next = true;
            do {
                playlistIndex++;
                if (playlistIndex - 1 >= playlistElementList.size()) {
                    next = false;
                    playlistIndex++;
                } else if (playlistElementList.get(playlistIndex - 1).isPlayable() &&
                        !playlistElementList.get(playlistIndex - 1).isNotFounded()) {
                    next = false;
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
