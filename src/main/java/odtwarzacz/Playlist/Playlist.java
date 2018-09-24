/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
import javafx.stage.StageStyle;
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
import static odtwarzacz.MainFXMLController.getPlaylist;

/**
 * @author Pregiel
 */
public class Playlist {

    private static final String DEFAULT_PLAYLIST = "Playlists/default_playlist.playlist";
    public static final String PLAYLIST_FOLDER = "Playlists";

    private BorderPane pane;
    private SplitPane splitPane;
    private PlaylistProperties playlistProperties;
    private List<String> playlistList;
    private List<PlaylistElement> playlistElementList;
    private VBox playlistPane;

    private Queue queue;

    private int playlistIndex;

    private boolean random;

    private MainFXMLController mainController;

    private QueueFXMLController queueFXMLController;

    private PlaylistFXMLController playlistFXMLController;

    public Playlist(MainFXMLController mainController) {
        this.mainController = mainController;
        playlistElementList = new ArrayList<>();
        playlistFilesList = new ArrayList<>();
        queue = new Queue();

        new File(PLAYLIST_FOLDER).mkdir();

        this.random = Boolean.parseBoolean(String.valueOf(Odtwarzacz.getConfig().get("random")));

        loadPlaylistFiles();

        String lastUsedPlaylist = Odtwarzacz.getConfig().getProperty("playlist.lastused");
        if (lastUsedPlaylist != null) {
            File lastUsedFile = new File(lastUsedPlaylist);

            if (lastUsedFile.exists() && lastUsedFile.isFile()) {
                loadPlaylistList(lastUsedFile.getAbsolutePath());
            } else {
                loadPlaylistList((playlistFilesList.size() > 0) ? playlistFilesList.get(0) : DEFAULT_PLAYLIST);
            }
        } else {
            loadPlaylistList((playlistFilesList.size() > 0) ? playlistFilesList.get(0) : DEFAULT_PLAYLIST);
        }
    }

    private List<String> playlistFilesList;

    public List<String> getPlaylistFilesList() {
        return playlistFilesList;
    }

    public List<String> loadPlaylistFiles() {
        playlistFilesList.clear();

        File[] files = new File(PLAYLIST_FOLDER).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (Utils.getExtension(file.getName()).equals("playlist")) {
                        playlistFilesList.add(file.getAbsolutePath());
                    }
                }
            }
        }
        sortPlaylistFiles();
        return playlistFilesList;
    }

    private class SortClass {
        private String file;
        private int id, sort_id;

        public SortClass(String file, int id, int sort_id) {
            this.file = file;
            this.id = id;
            this.sort_id = sort_id;
        }

        public String getFile() {
            return file;
        }

        public int getId() {
            return id;
        }

        public int getSort_id() {
            return sort_id;
        }

        @Override
        public String toString() {
            return "SortClass{" +
                    "file='" + file + '\'' +
                    ", id=" + id +
                    ", sort_id=" + sort_id +
                    "}";
        }
    }

    public void sortPlaylistFiles() {
        if (playlistFilesList.size() > 1) {
            List<SortClass> list = new ArrayList<>();

            int index = 1;
            for (String s : playlistFilesList) {
                list.add(new SortClass(s, index, new PlaylistProperties(s).getPlaylistId()));
                index++;
            }

            for (int i = 0; i < list.size(); i++) {
                for (int ii = i + 1; ii < list.size(); ii++) {
                    if (list.get(i).getSort_id() > list.get(ii).getSort_id()) {
                        Collections.swap(list, i, ii);
                    }
                }
            }

            playlistFilesList.clear();
            for (SortClass sortClass : list) {
                playlistFilesList.add(sortClass.getFile());
            }
        }
    }

    public static int getPlaylistLastId() {
        List<String> list = new ArrayList<>();
        File[] files = new File(PLAYLIST_FOLDER).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (Utils.getExtension(file.getName()).equals("playlist")) {
                        list.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return list.size();
    }


    public List<String> getPlaylistNames() {
        List<String> results = new ArrayList<>();

        for (String s : playlistFilesList) {
            results.add(new PlaylistProperties(s).getPlaylistName());
        }

        return results;
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
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/PlaylistFXML.fxml"), resourceBundle);
        FXMLLoader loader = new FXMLLoader(Paths.get("Layouts/PlaylistFXML.fxml").toUri().toURL(), Utils.getTranslationsBundle());

        pane = loader.load();
        playlistFXMLController = loader.getController();
        pane.setStyle(Theme.getStyleConst(Theme.PLAYLIST_FXML));
        Theme.getInstance().setPlayListNode(pane);
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

    public void undock() {
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

    public void dock() {
        show();
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

    public void setPlaylistName(String name) {
        playlistProperties.setPlaylistName(name);
        playlistFXMLController.reloadCombobox();
    }

    public String getPlaylistName() {
        return playlistProperties.getPlaylistName();
    }

    public void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    private Thread loadPlaylistThread;

    public void loadPlaylist() {
        playlistPane.getChildren().clear();
        playlistElementList.clear();
        Theme.getInstance().clearPlayListElementNode();


        loadPlaylistThread = new Thread(() -> {
            int i = 1;
            List<PlaylistElement> playlistElements = new ArrayList<>();
            for (String s : playlistList) {
                try {
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/PlaylistElementFXML.fxml"), resourceBundle);
                    FXMLLoader loader = new FXMLLoader(Paths.get("Layouts/PlaylistElementFXML.fxml").toUri().toURL(), Utils.getTranslationsBundle());

                    PlaylistElement element = new PlaylistElement(i++, s, loader.load());
                    playlistElementList.add(element);
                    playlistElements.add(element);

                } catch (IOException ex) {
                    Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            Platform.runLater(() -> {
                playlistPane.getChildren().clear();
                for (PlaylistElement playlistElement : playlistElements) {
                    playlistPane.getChildren().add(playlistElement.getPane());

                }
            });

            if (mainController.getConnection() != null) {
                mainController.getConnection().sendMessage(PLAYLIST_SEND, MainFXMLController.getPlaylist().toMessage());
            }

        });
        loadPlaylistThread.start();

    }

    public void loadPlaylistList(String playlistPath) {
        this.playlistProperties = new PlaylistProperties(playlistPath);
        this.playlistList = playlistProperties.getArray();

        Odtwarzacz.getConfig().setProperty("playlist.lastused", playlistPath);
        Odtwarzacz.getConfig().save();
    }

    public PlaylistProperties getPlaylistProperties() {
        return playlistProperties;
    }

    public void add(File file) {
        if (playlistList.contains(file.getAbsolutePath())) {
            ButtonType addButton = new ButtonType(Utils.getString("dialog.add"), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(Utils.getString("dialog.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    String.format(Utils.getString("playlist.alreadyAdded"), file.getName()),
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
    }

    public void renamePlaylist() {
        TextInputDialog dialog = new TextInputDialog(Utils.getString("player.playlist"));
        dialog.setTitle(Utils.getString("dialog.renameplaylist"));
        dialog.setHeaderText(null);
        dialog.setContentText(Utils.getString("dialog.entername"));
        dialog.setGraphic(null);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(Utils.getString("dialog.ok"));
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText(Utils.getString("dialog.cancel"));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            setPlaylistName(s);
        });
    }

    public void newPlaylist() {
        TextInputDialog dialog = new TextInputDialog(Utils.getString("player.playlist"));
        dialog.setTitle(Utils.getString("dialog.addplaylist"));
        dialog.setHeaderText(null);
        dialog.setContentText(Utils.getString("dialog.entername"));
        dialog.setGraphic(null);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(Utils.getString("dialog.ok"));
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText(Utils.getString("dialog.cancel"));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            this.playlistProperties = new PlaylistProperties(PLAYLIST_FOLDER + "/" + s + ".playlist", s);
            loadPlaylistFiles();
            playlistFXMLController.reloadCombobox(-1);
        });
    }

    public void closePlaylist() {
        ButtonType addButton = new ButtonType(Utils.getString("dialog.yes"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(Utils.getString("dialog.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                Utils.getString("dialog.areyousure"),
                addButton,
                cancelButton);
        alert.setTitle(Utils.getString("dialog.closeplaylist"));
        alert.setHeaderText(null);


        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                removePlaylist(playlistProperties.getPlaylistId());
            }
        });
    }

    private class PlaylistClass {
        private String file;
        private PlaylistProperties properties;

        public PlaylistClass(String file, PlaylistProperties properties) {
            this.file = file;
            this.properties = properties;
        }

        public String getFile() {
            return file;
        }

        public PlaylistProperties getProperties() {
            return properties;
        }
    }

    private void removePlaylist(int indexToDelete) {
        if (playlistFilesList.size() > 1) {
            List<PlaylistClass> list = new ArrayList<>();

            for (String s : playlistFilesList) {
                list.add(new PlaylistClass(s, new PlaylistProperties(s)));
            }

            int noIndex = 1;

            for (PlaylistClass playlistClass : list) {
                int index = playlistClass.getProperties().getPlaylistId();
                if (index == indexToDelete) {
                    File file = new File(playlistClass.getFile());
                    file.delete();
                    noIndex--;
                } else if (index > indexToDelete) {
                    playlistClass.getProperties().setProperty("playlist.id", String.valueOf(noIndex));
                    playlistClass.getProperties().save();
                }
                noIndex++;
            }
        } else {
            File file = new File(playlistFilesList.get(0));
            file.delete();

            this.playlistProperties = new PlaylistProperties(DEFAULT_PLAYLIST);
        }

        loadPlaylistFiles();
        playlistFXMLController.reloadCombobox(0);
    }


    //TODO: przetlumaczyc
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

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadPlaylist();
                }
            });
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
        if (getPlaylistIndex() > -1) {
            if (getPlaylistIndex() <= playlistList.size()) {
                play(getPlaylistIndex());
            } else {
                if (mainController.getConnection() != null) {
                    mainController.getConnection().sendMessage(Connection.PLAYLIST_PLAYING_INDEX, 0);
                }
                setNoPlayAll();
            }
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
        if (playlistIndex > -1) {
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
