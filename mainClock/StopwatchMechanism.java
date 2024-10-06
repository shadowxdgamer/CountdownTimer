package mainClock;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class StopwatchMechanism extends Thread {
    private int mills = 0;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    private final JLabel timeLabel; // Reference to the JLabel for updating time
    private volatile boolean paused = false; // Flag to check if the timer is paused
    private volatile boolean running = true; // Flag to control thread execution

    public StopwatchMechanism(JLabel timeLabel) {
        this.timeLabel = timeLabel; // Assign JLabel reference
    }

    public void run() {
        while (running) {
            if (!paused) {
                // Update the label with the current time
                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText(String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, mills));
                });

                // Update time counters
                mills++;
                if (mills == 100) {
                    mills = 0;
                    seconds++;
                    if (seconds == 60) {
                        seconds = 0;
                        minutes++;
                        if (minutes == 60) {
                            minutes = 0;
                            hours++;
                        }
                    }
                }

                // Update the timeInput in the main app
                updateTimeInput();
            }

            // Sleep for 10 milliseconds to update roughly every 1/100th of a second
            try {
                Thread.sleep((long) 9);
            } catch (InterruptedException e) {
                System.out.println(e);
                Thread.currentThread().interrupt();
            }

            // Pause handling
            synchronized (this) {
                while (paused) {
                    try {
                        wait(); // Wait until notified to resume
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                }
            }
        }
    }

    private void updateTimeInput() {
    	timeLabel.setText(String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, mills));
    }

    // Pause the stopwatch
    public synchronized void pauseTimer() {
        paused = true; // Set the paused flag to true
    }

    // Resume the stopwatch
    public synchronized void resumeTimer() {
        paused = false; // Set the paused flag to false
        notify(); // Notify the thread to continue
    }

    // Stop the stopwatch thread completely
    public synchronized void stopTimer() {
        running = false; // Stop the thread loop
        paused = false;  // Reset the paused flag
        notify();        // Ensure the thread is not stuck waiting
    }

    // Reset the stopwatch values
    public synchronized void resetTimer() {
        mills = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
        updateTimeInput();
    }
}
