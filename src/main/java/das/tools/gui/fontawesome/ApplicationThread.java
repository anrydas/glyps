package das.tools.gui.fontawesome;

import javafx.application.Platform;

public class ApplicationThread implements Runnable {
    private final Thread thread;

    public ApplicationThread(Runnable runnable) {
        this.thread = new Thread(() -> Platform.runLater(runnable), "BackgroundThread");
        this.thread.setDaemon(true);
    }

    @Override
    public void run() {
        this.thread.start();
    }

}
