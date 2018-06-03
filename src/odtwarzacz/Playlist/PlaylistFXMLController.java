/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import odtwarzacz.MainFXMLController;

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
    }

}
