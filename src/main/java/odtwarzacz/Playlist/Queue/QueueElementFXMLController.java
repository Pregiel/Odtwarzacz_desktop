package odtwarzacz.Playlist.Queue;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import odtwarzacz.MainFXMLController;

import java.net.URL;
import java.util.ResourceBundle;

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
        int removedIndex = MainFXMLController.getPlaylist().getQueue().getQueueElements().get(queueIndex).getPlaylistIndex() - 1;

        MainFXMLController.getPlaylist().getQueue().removeElement(queueIndex);
        for (int i1 = queueIndex; i1 < MainFXMLController.getPlaylist().getQueue().getQueueElements().size(); i1++) {
            MainFXMLController.getPlaylist().getPlaylistElementList().get(
                    MainFXMLController.getPlaylist().getQueue().getQueueElements().get(i1).getPlaylistIndex() - 1
            ).setQueueLabel();
        }

        MainFXMLController.getPlaylist().getPlaylistElementList().get(removedIndex).setQueueLabel();
        MainFXMLController.getPlaylist().getQueueFXMLController().loadQueue();
    }
}
