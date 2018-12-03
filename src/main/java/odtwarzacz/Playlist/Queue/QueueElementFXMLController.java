package odtwarzacz.Playlist.Queue;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import odtwarzacz.MainFXMLController;

import java.net.URL;
import java.util.ResourceBundle;

import static odtwarzacz.MainFXMLController.getPlaylist;

public class QueueElementFXMLController implements Initializable {
    public Label fileName;
    public Button queueRemove;
    public GridPane pane;

    private int queueIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initElement(int queueIndex, String title) {
        this.queueIndex = queueIndex;
        fileName.setText(title);

    }

    public void removeElement(ActionEvent event) {
        getPlaylist().getQueue().removeElementByQueueIndex(queueIndex);
    }
}
