/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import odtwarzacz.Connection.UsbConnection;
import odtwarzacz.Playlist.Playlist;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import odtwarzacz.Connection.BtConnection;
import odtwarzacz.Connection.Connection;
import odtwarzacz.Connection.WifiConnection;

/**
 * @author Pregiel
 */
public class MainFXMLController implements Initializable {

    public static final String[] SUPPORTED_AUDIO = {"*.MP3"};//{"*.AIFF", "*.MP3", "*.WAV"};
    public static final String[] SUPPORTED_VIDEO = {"*.MP4"};//{"*.FLV", "*.MP4"};

    @FXML
    private BorderPane pane;

    private MediaFXMLController mediaControl;
    @FXML
    private ToggleGroup languageGroup;
    @FXML
    private RadioMenuItem englishLanguageButton;
    @FXML
    private RadioMenuItem polishLanguageButton;

    @FXML
    private StackPane centerPane;

    private Pane lastPickedPane;

    private InfoLabel connectionInfoLabel, fileInfoLabel;
    @FXML
    private SplitPane splitPane;

    private static Playlist playlist;

    public static Playlist getPlaylist() {
        return playlist;
    }

    @FXML
    private Menu openRecentMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
//            refreshScene();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/FirstViewFXML.fxml"), Utils.getResourceBundle());
            lastPickedPane = loader.load();

            playlist = new Playlist(this);
            playlist.makePlaylistPane();

            playlist.setSplitPane(splitPane);

            if (Odtwarzacz.getConfig().getProperty("playlist.visible").equals("true")) {
                playlist.show();
            }

            refreshRecentFiles();
//            pane.setCenter(mediaPane);
            centerPane.getChildren().add(lastPickedPane);
            addInfoLabel();

            englishLanguageButton.setUserData("en");
            polishLanguageButton.setUserData("pl");

            switch (Odtwarzacz.getConfig().getProperty("language")) {
                case "pl":
                    polishLanguageButton.setSelected(true);
                    break;

                default:
                    englishLanguageButton.setSelected(true);
            }

            languageGroup.selectedToggleProperty().addListener((observable) -> {
                if (languageGroup.getSelectedToggle() != null) {
                    changeLanguage(languageGroup.getSelectedToggle().getUserData().toString());
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addInfoLabel() {
        connectionInfoLabel = new InfoLabel();
//        connectionInfoLabel.setText("");
        connectionInfoLabel.getStyleClass().add("info-label");

        centerPane.getChildren().add(connectionInfoLabel);
        StackPane.setAlignment(connectionInfoLabel, Pos.TOP_RIGHT);

        fileInfoLabel = new InfoLabel();
        fileInfoLabel.getStyleClass().add("info-label");

        centerPane.getChildren().add(fileInfoLabel);
        StackPane.setAlignment(fileInfoLabel, Pos.TOP_LEFT);
    }

    private void changeLanguage(String language) {
        Odtwarzacz.getConfig().setProperty("language", language);
        Odtwarzacz.getConfig().save();
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Language changed to: {0}", language);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("You must restart application to change language!");
        alert.showAndWait();
    }

    private void refreshScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/MediaFXML.fxml"), Utils.getResourceBundle());
        Pane mediaPane = loader.load();
        mediaPane.setStyle(Theme.getStyleConst(Theme.MEDIA_FXML));

        mediaControl = loader.getController();
//                mediaControl.setConnectionInfo(connectionInfoLabel);
        mediaControl.setConnection(connection);
        mediaControl.setFileInfoLabel(fileInfoLabel);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                centerPane.getChildren().clear();

                centerPane.getChildren().add(mediaPane);
                centerPane.getChildren().add(connectionInfoLabel);
                centerPane.getChildren().add(fileInfoLabel);

            }
        });

    }

    private void refreshRecentFiles() {
        ((Pane) lastPickedPane.lookup("#recentFiles")).getChildren().clear();
        openRecentMenu.getItems().clear();

        Odtwarzacz.getConfig().getArrayProperty("last").forEach((s) -> {
            Hyperlink recentFile = new Hyperlink(s);
            recentFile.setOnAction((event) -> {
                loadFile(new File(s));
            });

            ((Pane) lastPickedPane.lookup("#recentFiles")).getChildren().add(recentFile);

            MenuItem menuItem = new MenuItem(s);
            menuItem.setOnAction((event) -> {
                loadFile(new File(s));
            });

            openRecentMenu.getItems().add(menuItem);
        });

    }

    //    private WifiConnection wifiConnection;
//    
//    public WifiConnection getWifiConnection() {
//        return wifiConnection;
//    }
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    @FXML
    private void connectWifi(ActionEvent event) {
        connection = new WifiConnection();
        connection.setMediaController(mediaControl);
        connection.setMainFXMLController(this);
        connection.setConnectionInfo(connectionInfoLabel);
        connection.connect();
    }

    @FXML
    private void wifiSendTAK(ActionEvent event) {
//        wifiConnection.sendMessage("TAK");
        connection.sendSnapshot();
    }

    //    private BtConnection btConnection;
    @FXML
    private void connectBT(ActionEvent event) {
        connection = new BtConnection();
        connection.setMediaController(mediaControl);
        connection.setMainFXMLController(this);
        connection.setConnectionInfo(connectionInfoLabel);
        connection.connect();
    }

    @FXML
    private void BTSendTAK(ActionEvent event) {
//        btConnection.sendMessage("TAK");

    }


    public void connectUSB(ActionEvent event) {
        connection = new UsbConnection();
        connection.setMediaController(mediaControl);
        connection.setMainFXMLController(this);
        connection.setConnectionInfo(connectionInfoLabel);
        connection.connect();
    }


    private File file;

    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Video Files", SUPPORTED_VIDEO),
                new ExtensionFilter("Audio Files", SUPPORTED_AUDIO),
                new ExtensionFilter("All Files", "*.*"));
        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            loadFile(file);
        }

    }

    public boolean loadFile(File file) {
        getPlaylist().setNoPlayAll();
        loadFile(file, -1);
        return true;
    }

    public boolean loadFile(File file, int playlistIndex) {
        if (ifFileExists(file)) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Load file: {0}", file);

            if (mediaControl != null) {
                mediaControl.changeFile(file);
            } else {
                try {
                    refreshScene();
                    mediaControl.changeMediaPlayer(file);
                } catch (IOException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            getPlaylist().setPlaylistIndex(playlistIndex);
            saveFileToRecent(file.getAbsolutePath());
            refreshRecentFiles();
        } else {
            removeFromRecentFiles(file.getAbsolutePath());
            fileInfoLabel.setInfoText(InfoLabel.FILE_NOFILE, file.getName());
            refreshRecentFiles();
            return false;
        }

        return true;
    }

    private boolean ifFileExists(File file) {
        return file.exists() && !file.isDirectory();
    }

    private void saveFileToRecent(String path) {

        int recentListSize = Odtwarzacz.getConfig().getArrayProperty("last").size();
        if (recentListSize > 5) {
            recentListSize = 5;
        }

        int duplicatePosition = recentListSize;
        for (int i = 0; i < recentListSize; i++) {
            if (path.equals(Odtwarzacz.getConfig().getProperty("last." + i))) {
                duplicatePosition = i;
            }

        }
        for (int i = duplicatePosition; i > 0; i--) {
            Odtwarzacz.getConfig().setProperty("last." + i, Odtwarzacz.getConfig().getProperty("last." + (i - 1)));
        }
        Odtwarzacz.getConfig().setProperty("last.0", path);
        Odtwarzacz.getConfig().save();

    }

    private void removeFromRecentFiles(String path) {
        int recentListSize = Odtwarzacz.getConfig().getArrayProperty("last").size();

        boolean finded = false;
        for (int i = 0; i < recentListSize; i++) {
            if (finded) {
                Odtwarzacz.getConfig().setProperty("last." + (i - 1), Odtwarzacz.getConfig().getProperty("last." + i));
            } else if (path.equals(Odtwarzacz.getConfig().getProperty("last." + i))) {
                finded = true;
            }

        }

        Odtwarzacz.getConfig().remove("last." + (recentListSize - 1));
        Odtwarzacz.getConfig().save();
    }

    @FXML
    private void showHidePlaylist(ActionEvent event) {
        playlist.toogle();
    }

    public void sendDirectoryTree(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            System.out.println("No Directory selected");
        } else {
            System.out.println(Utils.getDirectoryTree(selectedDirectory));
        }
    }

    public void disconnect(ActionEvent actionEvent) {
        connection.disconnect();

    }

}
