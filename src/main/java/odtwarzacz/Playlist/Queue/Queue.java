package odtwarzacz.Playlist.Queue;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import odtwarzacz.Playlist.PlaylistElement;
import odtwarzacz.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static odtwarzacz.MainFXMLController.getPlaylist;

public class Queue {
    private List<QueueElement> queueElements;

    public Queue() {
        queueElements = new ArrayList<>();
    }

    public List<QueueElement> getQueueElements() {
        return queueElements;
    }

    public void addToQueue(QueueElement element) {
        queueElements.add(element);
    }

    public void removeFirstElement() {
        queueElements.remove(0);
        getPlaylist().refreshQueueView();
    }

    public void removeElement(int index) {
        queueElements.remove(index);
        getPlaylist().refreshQueueView();
    }

    public int removeLastElementByPlaylistIndex(int index) {
        QueueElement foundedElement = null;
        int i = 0, lastIndex = -1;
        for (QueueElement queueElement : queueElements) {
            if (queueElement.getPlaylistIndex() == index) {
                foundedElement = queueElement;
                lastIndex = i;
            }
            i++;
        }


        if (foundedElement != null) {
            queueElements.remove(foundedElement);
        }

        getPlaylist().refreshQueueView();

        return lastIndex;
    }

    public void removeAllElements() {
        for (QueueElement queueElement : getPlaylist().getQueue().getQueueElements()) {
            getPlaylist().getPlaylistElementList().get(queueElement.getPlaylistIndex() - 1).removeQueueLabel();
        }

        if (getPlaylist().getQueueFXMLController() != null) {
            getPlaylist().getQueueFXMLController().queuePane.getChildren().clear();
        }

        getPlaylist().getQueue().getQueueElements().clear();
        getPlaylist().setNextPlaylistIndex();
    }


    public void moveToIndex(int from, int to) {
        if (from != to) {
            if (queueElements.size() == to - 1) {
                to--;
            }

            moveToIndexInLists(from, to);
        }
    }

    public void moveToIndexInLists(int from, int to) {
        for (int i = 0; i < queueElements.size(); i++) {
            QueueElement queueElement = queueElements.get(i);
            int queueIndex = i + 1;
            if (queueIndex == from) {
                queueElement.setQueueIndex(to);
            } else if (queueIndex >= Math.min(from, to) && queueIndex <= Math.max(from, to)) {
                if (from > to) {
                    queueElement.setQueueIndex(queueIndex + 1);
                } else {
                    queueElement.setQueueIndex(queueIndex - 1);
                }
            }
        }

        int listSize = queueElements.size();

        VBox queuePane = getPlaylist().getQueueFXMLController().getQueuePane();

        Node node = queuePane.getChildren().get(from - 1);
        queuePane.getChildren().remove(from - 1);

        QueueElement element = queueElements.get(from - 1);
        queueElements.remove(from - 1);

        if (listSize == to - 1) {
            queuePane.getChildren().add(node);
            queueElements.add(element);
        } else {
            queuePane.getChildren().add(to - 1, node);
            queueElements.add(to - 1, element);
        }

        for (PlaylistElement playlistElement : getPlaylist().getPlaylistElementList()) {
            playlistElement.setQueueLabel();
        }
    }

    private class RememberPosition {
        public int fromPosition, toPosition;
        public List<Integer> fromPositions;

        public RememberPosition(int fromPosition, int toPosition) {
            this.fromPosition = fromPosition;
            this.toPosition = toPosition;
        }

        public RememberPosition(int toPosition) {
            this.toPosition = toPosition;
            fromPositions = new ArrayList<>();
        }
    }

    private List<RememberPosition> rememberPositions;

    public void initRememberPositions() {
        rememberPositions = new ArrayList<>();
    }

    public void addRememberPosition(int fromPosition, int toPosition) {
        rememberPositions.add(new RememberPosition(fromPosition, toPosition));
    }

    public void updateQueueFromRememberPositions() {
        List<RememberPosition> newPositions = new ArrayList<>();

        for (RememberPosition position : rememberPositions) {
            RememberPosition newPosition = new RememberPosition(position.toPosition);

            for (int i = 0; i < queueElements.size(); i++) {
                if (queueElements.get(i).getPlaylistIndex() == position.fromPosition) {
                    newPosition.fromPositions.add(i + 1);
                }
            }
            newPositions.add(newPosition);
        }

        for (RememberPosition newPosition : newPositions) {
            for (Integer fromPosition : newPosition.fromPositions) {
                queueElements.get(fromPosition - 1).setPlaylistIndex(newPosition.toPosition);
            }
        }

        updateQueueIndexes();
    }

    public void updateQueueIndexes() {
        for (int i = 0; i < queueElements.size(); i++) {
            queueElements.get(i).setQueueIndex(i + 1);
        }
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        queueElements.forEach((element) -> stringBuilder.append(element.toString()).append("\n"));
        return stringBuilder.toString();
    }
}
