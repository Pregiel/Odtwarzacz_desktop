/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import odtwarzacz.Connection.Connection;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Playlist.Queue.QueueFXMLController;
import odtwarzacz.Theme;
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
    @FXML
    private ScrollPane playlistScroll;
    @FXML
    private VBox playlistPane;

    private ExpandableTimeTask searchTask;

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../Layouts/QueueFXML.fxml"), Utils.getResourceBundle());
//                queueRoot = FXMLLoader.load(getClass().getResource("Queue/QueueFXML.fxml"), Utils.getResourceBundle());

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
        getPlaylist().showUndock();
    }
}
