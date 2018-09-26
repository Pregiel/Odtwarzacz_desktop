package odtwarzacz.Playlist.Queue;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Playlist.Playlist;
import odtwarzacz.Utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueFXMLController implements Initializable {
    public ScrollPane queueScroll;
    public VBox queuePane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadQueue();
    }

    public void clear(ActionEvent event) {
        for (QueueElement queueElement : MainFXMLController.getPlaylist().getQueue().getQueueElements()) {
            MainFXMLController.getPlaylist().getPlaylistElementList().get(queueElement.getPlaylistIndex() - 1).removeQueueLabel();
        }
        queuePane.getChildren().clear();
        MainFXMLController.getPlaylist().getQueue().getQueueElements().clear();
    }

    public void loadQueue() {
        queuePane.getChildren().clear();

        int i = 0;
        for (QueueElement queueElement : MainFXMLController.getPlaylist().getQueue().getQueueElements()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/QueueElementFXML.fxml"), Utils.getTranslationsBundle());

                GridPane pane = loader.load();

                ((Label) pane.lookup("#fileName")).setText(MainFXMLController.getPlaylist().getPlaylistElementList().get(queueElement.getPlaylistIndex() - 1).getTitleLabel().getText());

                int finalI = i;
                ((Button) pane.lookup("#queueRemove")).setOnAction(event -> {
                    int removedIndex = MainFXMLController.getPlaylist().getQueue().getQueueElements().get(finalI).getPlaylistIndex() - 1;
                    MainFXMLController.getPlaylist().getQueue().removeElement(finalI);
                    for (int i1 = finalI; i1 < MainFXMLController.getPlaylist().getQueue().getQueueElements().size(); i1++) {
                        MainFXMLController.getPlaylist().getPlaylistElementList().get(
                                MainFXMLController.getPlaylist().getQueue().getQueueElements().get(i1).getPlaylistIndex() - 1
                        ).setQueueLabel();
                    }
                    MainFXMLController.getPlaylist().getPlaylistElementList().get(removedIndex).setQueueLabel();
                    loadQueue();
                });

                queuePane.getChildren().add(pane);
            } catch (IOException ex) {
                Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }

    }
}
