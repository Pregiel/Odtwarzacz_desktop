package odtwarzacz.Playlist.Queue;

import java.util.ArrayList;
import java.util.List;

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
    }

    public void removeElement(int index) {
        queueElements.remove(index);
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

        return lastIndex;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        queueElements.forEach((element) -> stringBuilder.append(element.toString()).append("\n"));
        return stringBuilder.toString();
    }
}
