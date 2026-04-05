package hotel.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;

// Week 6: Multithreading - simulates concurrent room service tasks
public class ServiceTask implements Runnable {
    private final String taskName;
    private final int roomNumber;
    private final int durationMs;
    private final ObservableList<String> logList;

    public ServiceTask(String taskName, int roomNumber, int durationMs, ObservableList<String> logList) {
        this.taskName = taskName;
        this.roomNumber = roomNumber;
        this.durationMs = durationMs;
        this.logList = logList;
    }

    @Override
    public void run() {
        log("🔔 [STARTED] " + taskName + " for Room " + roomNumber);
        try {
            Thread.sleep(durationMs);  // simulate work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log("✅ [DONE]    " + taskName + " for Room " + roomNumber + " completed.");
    }

    private void log(String msg) {
        Platform.runLater(() -> logList.add(0, msg));
    }
}
