package odtwarzacz.Utils;

import javafx.stage.Stage;

import java.awt.*;

public class CustomStage extends Stage {
    private boolean windowsMaximazed,  resizing, moving;

    private double lastWidth=300, lastHeight=300, lastX, lastY;

    public boolean isWindowMaximazed() {
        return windowsMaximazed;
    }

    public void setWindowMaximazed(boolean windowsMaximazed) {
        this.windowsMaximazed = windowsMaximazed;
        if (windowsMaximazed) {
            Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            lastHeight = getHeight();
            lastWidth = getWidth();
            lastX = getX();
            lastY = getY();

            setHeight((double) winSize.height);
            setWidth((double) winSize.width);
            setX(0);
            setY(0);
        } else {
            setHeight(lastHeight);
            setWidth(lastWidth);
            setX(lastX);
            setY(lastY);
        }
    }

    public boolean isResizing() {
        return resizing;
    }

    public void setResizing(boolean resizing) {
        this.resizing = resizing;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}
