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
import odtwarzacz.Utils.MyLocale;
import odtwarzacz.Playlist.Playlist;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueFXMLController implements Initializable {
    public ScrollPane queueScroll;
    public VBox queuePane;

    private List<QueueElement> queueElementList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        for (QueueElement queueElement : MainFXMLController.getPlaylist().getQueue().getQueueElements()) {
//
//
//            queuePane.getChildren().add(element.getPane());
//        }

        loadQueue();
    }

    public void clear(ActionEvent event) {
        for (QueueElement queueElement : MainFXMLController.getPlaylist().getQueue().getQueueElements()) {
            MainFXMLController.getPlaylist().getPlaylistElementList().get(queueElement.getPlaylistIndex() - 1).removeFromQueue();
        }
        queuePane.getChildren().clear();
        MainFXMLController.getPlaylist().getQueue().getQueueElements().clear();
    }

    public void loadQueue() {
        queuePane.getChildren().clear();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations.MessagesBundle", MyLocale.getLocale(),
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

        int i = 0;
        for (QueueElement queueElement : MainFXMLController.getPlaylist().getQueue().getQueueElements()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../../Layouts/QueueElementFXML.fxml"), resourceBundle);

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

//                PlaylistElement element = new PlaylistElement(i++, s, loader.load());
//                playlistElementList.add(element);

                queuePane.getChildren().add(pane);
            } catch (IOException ex) {
                Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }

    }
}
