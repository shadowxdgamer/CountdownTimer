package mainClock;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.List;

//Create an interface for the TimerListener
interface TimerListener {
 void onTimerComplete();
}
class Time extends Thread{
    private List<TimerListener> listeners = new ArrayList<>();
    
    public void addTimerListener(TimerListener listener) {
        listeners.add(listener);
    }
	int seconds;
	int minutes;
	int hours;
    private JLabel timeLabel; // Reference to the JLabel for updating time
    private volatile boolean paused; // Flag to check if the timer is paused
	public void setTime(int sec,int min,int h, JLabel label) {
        // Set time and handle any overflow
        hours = h + (min / 60) + (sec / 3600); // Handle extra minutes/hours from seconds
        minutes = (min + (sec / 60)) %60; // Normalize minutes
        seconds = sec % 60; // Normalize seconds
        this.timeLabel = label; // Set the label reference
        this.paused = false; // Ensure the timer is not paused when setting time
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        });
	}
	//buttons add time
	public void addTime(int sec,int min) {
		minutes += min;
		seconds += sec;
		if(seconds > 59) {
			seconds = seconds % 60;
			minutes++;
		}
		if(minutes > 59) {
			minutes = minutes % 60;
			hours++;
		}
        // Update timeInput every second
        updateTimeInput();
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        });
	}
	public void run() {
		while(seconds > 0 || minutes > 0 || hours > 0) {
            // Update the label with the current time
            SwingUtilities.invokeLater(() -> {
                timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            });

        // Decrease time by one second
        if (seconds == 0) {
            if (minutes == 0) {
                if (hours > 0) {
                    hours--;   // Borrow an hour
                    minutes = 59;
                    seconds = 59;
                }
            } else {
                minutes--;    // Borrow a minute
                seconds = 59;
            }
        } else {
            seconds--;        // Decrease seconds normally
        }
        // Update timeInput every second
        updateTimeInput();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		synchronized (this) {
            while (paused) {
                try {
                    wait(); // Wait until notified to resume
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
            	}
			}
        // Play sound when the timer reaches zero
        if (seconds == 0 && minutes == 0 && hours == 0) {
        	if(!SoundPlayer.isMuted) {
                SoundPlayer.playSound("/Resources/wav/alarm.wav"); // Specify the correct path to your sound file
                SoundPlayer.isRunning = true;
        	}

        }
		}
        // Notify listeners when the timer completes
        for (TimerListener listener : listeners) {
            listener.onTimerComplete();
        }
	}
    private void updateTimeInput() {
        MainApp.timeInput = String.format("%02d%02d%02d", hours, minutes, seconds);
    }
    public synchronized void pauseTimer() {
        paused = true; // Set the paused flag to true
    }
    public synchronized void resumeTimer() {
        paused = false; // Set the paused flag to false
        notify(); // Notify the thread to continue
    }
}

