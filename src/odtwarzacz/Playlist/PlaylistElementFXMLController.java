/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Pregiel
 */
public class PlaylistElementFXMLController implements Initializable {

    @FXML
    private GridPane pane;
    @FXML
    private CheckBox songCheckbox;
    @FXML
    private Label songName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public GridPane getPane() {
        return pane;
    }

    public CheckBox getSongCheckbox() {
        return songCheckbox;
    }

    public Label getSongName() {
        return songName;
    }
    
    
}
