package odtwarzacz.Utils;

public class ExpandableTimeTask {
    private Runnable task;
    private int time, totalTime;
    private boolean stop, finished, started;

    public ExpandableTimeTask(Runnable task, int timeInMillis) {
        this.task = task;
        this.totalTime = timeInMillis;
        started = false;
        finished = false;
        stop = false;
    }

    public void start() {
        started = true;
        finished = false;
        stop = false;
        time = totalTime;
        Thread timeoutThread = new Thread(() -> {
            while (time >= 0) {
                try {
                    Thread.sleep(100);
                    if (!stop) {
                        time -= 100;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            task.run();
            finished = true;
            started = false;
        });
        timeoutThread.start();
    }

    public boolean isFinished() {
        return finished;
    }

    public void stop() {
        stop = true;
    }

    public void resume() {
        stop = false;
        time = totalTime;
    }

    public void expand() {
        time = totalTime;
    }

    public boolean isStarted() {
        return started;
    }
}
