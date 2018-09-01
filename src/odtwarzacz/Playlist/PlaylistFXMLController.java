/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import odtwarzacz.Connection.Connection;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Playlist.Queue.QueueFXMLController;
import odtwarzacz.Utils;

import static odtwarzacz.MainFXMLController.getPlaylist;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class PlaylistFXMLController implements Initializable {

    @FXML
    private ScrollPane playlistScroll;
    @FXML
    private VBox playlistPane;
    @FXML
    private ToggleButton randomTogglebutton;

    public ToggleButton getRandomTogglebutton() {
        return randomTogglebutton;
    }

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

        if (getPlaylist().isRandom()) {
            randomTogglebutton.setSelected(true);
        }
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

    @FXML
    private void randomToggle(ActionEvent event) {
        getPlaylist().setRandom(randomTogglebutton.isSelected());
        Connection.getInstance().sendMessage(randomTogglebutton.isSelected() ? Connection.RANDOM_ON : Connection.RANDOM_OFF);
    }

    private Parent queueRoot;

    private void clearQueueView() {
        queueRoot = null;
    }

    public void showQueue(ActionEvent event) {

        if (queueRoot == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Queue/QueueFXML.fxml"), Utils.getResourceBundle());
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
}
