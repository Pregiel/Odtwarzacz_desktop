package odtwarzacz.Playlist.Queue;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import odtwarzacz.MainFXMLController;
import odtwarzacz.Playlist.Playlist;
import odtwarzacz.Utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static odtwarzacz.MainFXMLController.getPlaylist;

public class QueueFXMLController implements Initializable {
    public ScrollPane queueScroll;
    public VBox queuePane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadQueue();

        queueScroll.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.A && event.isControlDown()) {
                selectAll();
            } else if (event.getCode() == KeyCode.D && event.isControlDown()) {
                unselectAll();
            }
        });

        queueScroll.setOnMouseClicked(event -> {
            if (event.getTarget().toString().contains("queuePane"))
                unselectAll();
        });
    }

    public void clear(ActionEvent event) {
        getPlaylist().getQueue().removeAllElements();
        getPlaylist().setNextPlaylistIndex();
    }

    public void loadQueue() {
        queuePane.getChildren().clear();

        int i = 0;
        for (QueueElement queueElement : getPlaylist().getQueue().getQueueElements()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/QueueElementFXML.fxml"), Utils.getTranslationsBundle());
                GridPane pane = loader.load();
                QueueElementFXMLController controller = loader.getController();

                queueElement.setPane(pane);

                controller.initElement(i,
                        getPlaylist().getPlaylistElementList().get(queueElement.getPlaylistIndex() - 1).getTitleLabel().getText());

                queuePane.getChildren().add(pane);
            } catch (IOException ex) {
                Logger.getLogger(Playlist.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }

    }

    public void unselectAll() {
        getPlaylist().getQueue().getQueueElements().forEach((t) -> {
            t.setSelected(false);
        });
    }

    public void selectAll() {
        getPlaylist().getQueue().getQueueElements().forEach((t) -> {
            t.setSelected(true);
        });
    }

    public void removeSelected(ActionEvent event) {
        int size = getPlaylist().getQueue().getQueueElements().size();
        List<Integer> wasOnList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            QueueElement element = getPlaylist().getQueue().getQueueElements().get(i);

            if (!wasOnList.contains(element.getPlaylistIndex())){
                wasOnList.add(element.getPlaylistIndex());
            }

            if (element.isSelected()) {
                getPlaylist().getQueue().getQueueElements().remove(element);


                i--;
                size--;
            } else {
                element.setSelected(false);
            }
        }

        wasOnList.forEach(integer ->
            getPlaylist().getPlaylistElementList().get(integer-1).setQueueLabel()
        );


        loadQueue();
        getPlaylist().setNextPlaylistIndex();
    }

    public VBox getQueuePane() {
        return queuePane;
    }
}
