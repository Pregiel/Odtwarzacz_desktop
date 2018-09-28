/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Odtwarzacz;
import odtwarzacz.Playlist.Queue.QueueFXMLController;
import odtwarzacz.Theme;
import odtwarzacz.Utils.CustomStage;
import odtwarzacz.Utils.ExpandableTimeTask;
import odtwarzacz.Utils.ResizeHelper;
import odtwarzacz.Utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import static odtwarzacz.MainFXMLController.getPlaylist;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class PlaylistFXMLController implements Initializable {

    public TextField searchBox;
    public Button clearSearchBoxButton;
    public ComboBox<String> playlistComboBox;
    public Button undockButton;
    public Label nextLabel;
    public Label nextFileName;
    public Tooltip nextTitleTooltip;
    public Label nextDuration;
    public Button nextReroll;
    public Tooltip nextRerollTooltip;
    public GridPane nextPane;
    public VBox bottomBar;

    @FXML
    private ScrollPane playlistScroll;
    @FXML
    private VBox playlistPane;

    private ExpandableTimeTask searchTask;

    private ChangeListener<Boolean> changeListener;

    private int lastedComboboxIndex;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        getPlaylist().setPlaylistPane(playlistPane);
//        getPlaylist().loadPlaylist();

        playlistScroll.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                getPlaylist().selectAll();
            } else if (event.getCode() == KeyCode.D && event.isControlDown()) {
                getPlaylist().unselectAll();
            }
        });

        playlistScroll.setOnMouseClicked(event -> {
            if (event.getTarget().toString().contains("ScrollPaneSkin"))
                getPlaylist().unselectAll();
        });

        changeListener = (observable, oldValue, newValue) -> {
            if (!newValue && playlistComboBox.getSelectionModel().getSelectedIndex() != lastedComboboxIndex) {
                getPlaylist().loadPlaylistList(getPlaylist().getPlaylistFilesList().get(playlistComboBox.getSelectionModel().getSelectedIndex()));
                getPlaylist().loadPlaylist();
                lastedComboboxIndex = playlistComboBox.getSelectionModel().getSelectedIndex();
            }
        };

        reloadCombobox();


        searchTask = new ExpandableTimeTask(() -> {
            if (searchBox.getText().equals("")) {
                for (PlaylistElement playlistElement : getPlaylist().getPlaylistElementList()) {
                    playlistElement.show();
                }
            } else {
                String searchText = searchBox.getText();
                for (PlaylistElement playlistElement : getPlaylist().getPlaylistElementList()) {
                    if (!playlistElement.getTitleLabel().getText().toLowerCase().contains(searchText.toLowerCase())) {
                        playlistElement.hide();
                    } else {
                        playlistElement.show();
                    }
                }
            }
            getPlaylist().redrawElementsBackground();
        }, 200);

        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                clearSearchBoxButton.setVisible(false);
            } else {
                clearSearchBoxButton.setVisible(true);
            }
        });
    }

    public void reloadCombobox() {
        int index = 0;
        for (int i = 0; i < getPlaylist().getPlaylistFilesList().size(); i++) {
            if (getPlaylist().getPlaylistFilesList().get(i).equals(Odtwarzacz.getConfig().getProperty("playlist.lastused"))) {
                index = i;
                break;
            }
        }

        reloadCombobox(index);
    }

    public void reloadCombobox(int index) {
        playlistComboBox.showingProperty().removeListener(changeListener);
        playlistComboBox.getItems().clear();
        List<String> playlistNames = getPlaylist().getPlaylistNames();
        if (playlistNames.size() == 0) {
            playlistComboBox.getItems().add(Utils.getTranslationsBundle().getString("player.playlist"));
        } else {
            playlistComboBox.getItems().addAll(playlistNames);
        }

        if (index == -1) {
            playlistComboBox.getSelectionModel().selectLast();
        } else {
            playlistComboBox.getSelectionModel().select(index);
        }

        int selectedIndex = playlistComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1 && getPlaylist().getPlaylistFilesList().size() > 0) {
            getPlaylist().loadPlaylistList(getPlaylist().getPlaylistFilesList().get(selectedIndex));
            getPlaylist().loadPlaylist();

//            lastedComboboxIndex = selectedIndex;
        }

        playlistComboBox.showingProperty().addListener(changeListener);
    }

    public ComboBox<String> getPlaylistComboBox() {
        return playlistComboBox;
    }

    @FXML
    private void add(ActionEvent event) {
        getPlaylist().addNew();
    }

    @FXML
    private void remove(ActionEvent event) {
        getPlaylist().removeSelected();
    }

    @FXML
    private void hide(ActionEvent event) {
        getPlaylist().hide();
    }

    @FXML
    private void play(ActionEvent event) {
        getPlaylist().playAll();
    }

    private BorderPane queueRoot;

    private void clearQueueView() {
        queueRoot = null;
    }

    public void showQueue(ActionEvent event) {

        if (queueRoot == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/WindowFXML.fxml"), Utils.getTranslationsBundle());

                queueRoot = loader.load();
                queueRoot.setStyle(Theme.getStyleConst(Theme.WINDOW_FXML));
                ((Label) queueRoot.lookup("#windowTitle")).setText(Utils.getString("player.queue"));

                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/Layouts/QueueFXML.fxml"), Utils.getTranslationsBundle());
                BorderPane queuePane = loader2.load();
                QueueFXMLController queueFXMLController = loader2.getController();
                MainFXMLController.getPlaylist().setQueueFXMLController(queueFXMLController);

                ((AnchorPane) queueRoot.lookup("#center")).getChildren().add(queuePane);

                AnchorPane.setTopAnchor(queuePane, 0.0);
                AnchorPane.setRightAnchor(queuePane, 0.0);
                AnchorPane.setLeftAnchor(queuePane, 0.0);
                AnchorPane.setBottomAnchor(queuePane, 0.0);

                CustomStage stage = new CustomStage();
                stage.setTitle(Utils.getString("player.queue"));
                stage.setScene(new Scene(queueRoot, 400, 600));
                stage.setMinWidth(Odtwarzacz.PLAYLIST_MIN_WIDTH - 30);
                stage.setMinHeight(Odtwarzacz.PLAYER_MIN_HEIGHT);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setOnHidden(e -> {
                    clearQueueView();
                    MainFXMLController.getPlaylist().setQueueFXMLController(null);
                });

                ResizeHelper.addResizeListener(stage, Odtwarzacz.PLAYLIST_MIN_WIDTH, Odtwarzacz.PLAYER_MIN_HEIGHT, 1.7976931348623157E308D, 1.7976931348623157E308D);


                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void searchTyped(KeyEvent keyEvent) {
        if (!searchTask.isStarted()) {
            searchTask.start();
        } else {
            searchTask.resume();
        }
    }

    public void clearSearchBox(ActionEvent event) {
        searchBox.setText("");
        searchTyped(null);
    }

    public void undock(ActionEvent event) {
        if (getPlaylist().getPlaylistWindow() == null) {
            getPlaylist().undock();
            undockButton.setText("R");
        } else {
            getPlaylist().dock();
            undockButton.setText("Q");
        }
    }

    public void renamePlaylist(ActionEvent event) {
        getPlaylist().renamePlaylist();
    }

    public void newPlaylist(ActionEvent event) {
        getPlaylist().newPlaylist();
    }

    public void closePlaylist(ActionEvent event) {
        getPlaylist().closePlaylist();
    }

    public void setNextPane() {
        System.out.println("tak " + getPlaylist().getPlaylistIndex());
        if (getPlaylist().getPlaylistIndex() == -1) {
            System.out.println("taasdasdk");
            bottomBar.getChildren().remove(nextPane);
        } else {
            if (!bottomBar.getChildren().contains(nextPane))
                bottomBar.getChildren().add(0, nextPane);
//            if () {
//
//            }
        }
    }
}
