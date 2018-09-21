package odtwarzacz;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import lk.vivoxalabs.customstage.tools.ActionAdapter;
import odtwarzacz.Connection.UsbConnection;
import odtwarzacz.Playlist.Playlist;

import java.awt.*;
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
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser.ExtensionFilter;
import odtwarzacz.Connection.BtConnection;
import odtwarzacz.Connection.Connection;
import odtwarzacz.Connection.WifiConnection;
import odtwarzacz.Utils.CustomStage;
import odtwarzacz.Utils.Delta;
import odtwarzacz.Utils.Utils;

/**
 * @author Pregiel
 */
public class MainFXMLController implements Initializable {

    public static final String[] SUPPORTED_AUDIO = {"*.MP3"};//{"*.AIFF", "*.MP3", "*.WAV"};
    public static final String[] SUPPORTED_VIDEO = {"*.MP4"};//{"*.FLV", "*.MP4"};
    public GridPane titleBar;

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

    private ActionAdapter actionAdapter;

    private Stage maximazeScreen;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
//            refreshScene();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/FirstViewFXML.fxml"), Utils.getTranslationsBundle());
            lastPickedPane = loader.load();

            playlist = new Playlist(this);
            playlist.makePlaylistPane();

            playlist.setSplitPane(splitPane);


            double dividerPosition = Double.parseDouble(Odtwarzacz.getConfig().getProperty("playlist.divider"));
            double windowWidth = Double.parseDouble(Odtwarzacz.getConfig().getProperty("window.width"));

            if (Odtwarzacz.getConfig().getProperty("playlist.visible").equals("true")) {
                playlist.show();
            }

            Platform.runLater(() -> {
                splitPane.setDividerPosition(0, 1 - (dividerPosition / windowWidth));
            });


            splitPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                double dividerPosition2 = Double.parseDouble(Odtwarzacz.getConfig().getProperty("playlist.divider"));
                splitPane.setDividerPosition(0, 1 - (dividerPosition2 / (double) newValue));
            });

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
            final Delta dragDelta = new Delta();

            titleBar.setOnMousePressed(mouseEvent -> {
                CustomStage stage = ((CustomStage) pane.getScene().getWindow());
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        stage.setWindowMaximazed(!stage.isWindowMaximazed());
                    } else {
                        dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                        dragDelta.y = stage.getY() - mouseEvent.getScreenY();

                        stage.setMoving(true);
                    }
                }
            });

            titleBar.setOnMouseDragged(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
                    CustomStage stage = ((CustomStage) pane.getScene().getWindow());
                    if (!stage.isResizing()) {
                        if (stage.isWindowMaximazed()) {
                            stage.setWindowMaximazed(false);
                            dragDelta.x = stage.getWidth() / -2;
                        } else {
                            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                        }
                        if (mouseEvent.getScreenY() == 0) {
                            maximazeScreen.show();

                        } else {
                            maximazeScreen.hide();

                        }
                    }
                }
            });

            titleBar.setOnMouseReleased(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    CustomStage stage = ((CustomStage) pane.getScene().getWindow());
                    stage.setMoving(false);
                    stage.getScene().setCursor(Cursor.DEFAULT);

                    if (mouseEvent.getScreenY() == 0) {
                        stage.setWindowMaximazed(true);
                    }
                    maximazeScreen.hide();

                }
            });

            initMaximazeScreen();

            Platform.runLater(() ->
                    actionAdapter = new ActionAdapter((Stage) pane.getScene().getWindow()));

        } catch (IOException ex) {
            Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initMaximazeScreen() {
        Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        Pane screen = new Pane();

        screen.setPrefHeight(winSize.height - 10);
        screen.setPrefWidth(winSize.width - 10);
        screen.setStyle("-fx-background-color: rgba(99,170,255,0.2);");

        maximazeScreen = new Stage();
        maximazeScreen.setScene(new Scene(screen));
        maximazeScreen.initStyle(StageStyle.TRANSPARENT);
        maximazeScreen.getScene().setFill(Color.TRANSPARENT);
        maximazeScreen.setAlwaysOnTop(false);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/MediaFXML.fxml"), Utils.getTranslationsBundle());
        Pane mediaPane = loader.load();
        mediaPane.setStyle(Theme.getStyleConst(Theme.MEDIA_FXML));
        Theme.getInstance().setMediaNode(mediaPane);

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

                mediaControl.setScaling(centerPane.getScene().getWindow(), centerPane);

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

    public void setDarkTheme(ActionEvent event) {
        Theme.getInstance().changeTheme(Theme.DARK_THEME);
    }

    public void setLightTheme(ActionEvent event) {
        Theme.getInstance().changeTheme(Theme.LIGHT_THEME);

    }

    public void minButton(ActionEvent event) {
        CustomStage stage = ((CustomStage) pane.getScene().getWindow());
        if (!stage.isMoving() && !stage.isResizing()) {
            stage.setIconified(true);
        }
    }

    public void maxButton(ActionEvent event) {
        CustomStage stage = ((CustomStage) pane.getScene().getWindow());
        if (!stage.isMoving() && !stage.isResizing()) {
            if (stage.isWindowMaximazed()) {
                stage.setWindowMaximazed(false);
            } else {
                stage.setWindowMaximazed(true);
            }
        }
    }

    public void exitButton(ActionEvent event) {
        CustomStage stage = ((CustomStage) pane.getScene().getWindow());
        if (!stage.isMoving() && !stage.isResizing()) {
            Platform.exit();
            System.exit(0);
        }
    }
}
