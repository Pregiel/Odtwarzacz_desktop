/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import odtwarzacz.Connection.Connection;
import odtwarzacz.IconFont;
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
    public Label searchLabel;
    public GridPane searchPane;

    @FXML
    private ScrollPane playlistScroll;
    @FXML
    private VBox playlistPane;

    private ExpandableTimeTask searchTask;

    private ChangeListener<Boolean> changeListener;

    private int selectedPlaylistTitle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getPlaylist().setPlaylistPane(playlistPane);

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
            if (!newValue && playlistComboBox.getSelectionModel().getSelectedIndex() != selectedPlaylistTitle) {
                getPlaylist().loadPlaylistList(getPlaylist().getPlaylistFilesList().get(playlistComboBox.getSelectionModel().getSelectedIndex()));
                getPlaylist().loadPlaylist(getPlaylist().getPlaylistIndex() > 0);
                getPlaylist().clearPrevPlaylistList();
                selectedPlaylistTitle = playlistComboBox.getSelectionModel().getSelectedIndex();
                if (Connection.getInstance() != null) {
                    Connection.getInstance().sendMessage(Connection.PLAYLIST_TITLES, getSelectedPlaylistTitle(), getPlaylist().titlesToMessage());
                }
            }
        };

        reloadCombobox();

        setNextPane();
        bottomBar.getChildren().remove(searchPane);


        searchTask = new ExpandableTimeTask(() -> {
            if (searchBox.getText().equals("")) {
                for (PlaylistElement playlistElement : getPlaylist().getPlaylistElementList()) {
                    playlistElement.show();
                    Platform.runLater(() -> {
                        bottomBar.getChildren().remove(searchPane);
                        searchLabel.setText("");
                    });
                }
            } else {
                String searchText = searchBox.getText();
                int results = 0;
                for (PlaylistElement playlistElement : getPlaylist().getPlaylistElementList()) {
                    if (!playlistElement.getTitleLabel().getText().toLowerCase().contains(searchText.toLowerCase())) {
                        playlistElement.hide();
                    } else {
                        results++;
                        playlistElement.show();
                    }
                }

                String resultText = String.format(Utils.getString("playlist.search.found"), results);
                Platform.runLater(() -> {
                    if (!bottomBar.getChildren().contains(searchPane)) {
                        if (bottomBar.getChildren().contains(nextPane)) {
                            bottomBar.getChildren().add(1, searchPane);
                        } else {
                            bottomBar.getChildren().add(0, searchPane);
                        }
                    }
                    searchLabel.setText(resultText);
                });
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
        List<String> playlistNames = getPlaylist().getPlaylistTitles();
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

            selectedPlaylistTitle = selectedIndex;
        }

        playlistComboBox.showingProperty().addListener(changeListener);
    }

    public int getSelectedPlaylistTitle() {
        return selectedPlaylistTitle;
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
            undockButton.setText(IconFont.ICON_UNDOCK);
        } else {
            getPlaylist().dock();
            undockButton.setText(IconFont.ICON_DOCK);
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
        if (getPlaylist().getPlaylistElementList().size() > 0) {
            if (getPlaylist().getPlaylistIndex() == -1) {
                bottomBar.getChildren().remove(nextPane);
            } else {
                if (!bottomBar.getChildren().contains(nextPane))
                    bottomBar.getChildren().add(0, nextPane);

                PlaylistElement element = getPlaylist().getPlaylistElementList().get(getPlaylist().getNextPlaylistIndex() - 1);

                nextFileName.setText(element.getTitleLabel().getText());
                nextDuration.setText(element.getDurationLabel().getText());

                switch (getPlaylist().getNextPlayingMode()) {
                    case QUEUE:
                        nextLabel.setText(Utils.getString("playlist.nextfile.queue"));
                        nextReroll.setOnAction(event -> {
                            getPlaylist().getQueue().removeFirstElement();
                            getPlaylist().getPlaylistElementList().forEach(PlaylistElement::setQueueLabel);
                            getPlaylist().setNextPlaylistIndex();
                        });
                        nextRerollTooltip.setText(Utils.getString("playlist.nextfile.reroll.queue"));
                        nextReroll.setText(IconFont.ICON_CLEAR);
                        break;

                    case RANDOM:
                        nextLabel.setText(Utils.getString("playlist.nextfile.random"));
                        nextReroll.setOnAction(event -> {
                            getPlaylist().setNextPlaylistIndex();
                        });
                        nextRerollTooltip.setText(Utils.getString("playlist.nextfile.reroll.random"));
                        nextReroll.setText(IconFont.ICON_RANDOMIZE);
                        break;

                    default:
                        nextLabel.setText(Utils.getString("playlist.nextfile"));
                        nextReroll.setOnAction(event -> {
                            getPlaylist().incNextPlaylistIndex();
                            setNextPane();
                        });
                        nextRerollTooltip.setText(Utils.getString("playlist.nextfile.reroll.normal"));
                        nextReroll.setText(IconFont.ICON_SKIP);
                }

            }
        } else {
            bottomBar.getChildren().remove(nextPane);
        }

        getPlaylist().sendNextFile();
    }
}
