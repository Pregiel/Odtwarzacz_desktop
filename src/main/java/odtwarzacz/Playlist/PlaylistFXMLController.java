/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Odtwarzacz;
import odtwarzacz.Playlist.Queue.QueueFXMLController;
import odtwarzacz.Utils.ExpandableTimeTask;
import odtwarzacz.Utils.Utils;

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
        getPlaylist().loadPlaylist();

        playlistScroll.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                getPlaylist().selectAll();
            } else if (event.getCode() == KeyCode.D && event.isControlDown()) {
                getPlaylist().unselectAll();
            }
        });


        changeListener = (observable, oldValue, newValue) -> {
            if (!newValue && playlistComboBox.getSelectionModel().getSelectedIndex() != lastedComboboxIndex) {
                getPlaylist().loadPlaylistList(getPlaylist().getPlaylistFilesList().get(playlistComboBox.getSelectionModel().getSelectedIndex()));
                getPlaylist().loadPlaylist();
            }
            lastedComboboxIndex = playlistComboBox.getSelectionModel().getSelectedIndex();
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
            int i = 0;
            for (PlaylistElement playlistElement : getPlaylist().getPlaylistElementList()) {
                if (!playlistElement.isHidden()) {
                    if (i % 2 == 0) {
                        playlistElement.setStyle(2);
                    } else {
                        playlistElement.setStyle(1);
                    }
                    i++;
                }

            }
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
        playlistComboBox.getItems().addAll(
                getPlaylist().getPlaylistNames()
        );

        if (index == -1) {
            playlistComboBox.getSelectionModel().selectLast();
        } else {
            playlistComboBox.getSelectionModel().select(index);
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

    private Parent queueRoot;

    private void clearQueueView() {
        queueRoot = null;
    }

    public void showQueue(ActionEvent event) {

        if (queueRoot == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/QueueFXML.fxml"), Utils.getTranslationsBundle());
//                queueRoot = FXMLLoader.load(getClass().getResource("Queue/QueueFXML.fxml"), Utils.getTranslationsBundle());

                queueRoot = loader.load();

                QueueFXMLController queueFXMLController = loader.getController();

                MainFXMLController.getPlaylist().setQueueFXMLController(queueFXMLController);

                Stage stage = new Stage();
                stage.setTitle(Utils.getString("player.queue"));
                stage.setScene(new Scene(queueRoot, 250, 350));
                stage.setOnHidden(e -> {
                    clearQueueView();
                    MainFXMLController.getPlaylist().setQueueFXMLController(null);
                });
                stage.show();
                // Hide this current window (if this is what you want)
//            ((Node)(event.getSource())).getScene().getWindow().hide();
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
        } else {
            getPlaylist().dock();
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
}
