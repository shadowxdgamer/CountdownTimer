package mainClock;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

public class StopwatchPanel extends JPanel {
	StopwatchMechanism sw;
    private JLabel lblTime;
    private static JToggleButton btnSound;
    private static JButton ThemeToggle;
    private MainApp mainApp; // Reference to the parent container (MainApp or JFrame)
    private String state = "paused";
    public StopwatchPanel(MainApp mainApp) {
    	this.mainApp = mainApp; // Store the reference for future use
        init();
    }

    private void init() {
        this.setLayout(new MigLayout("fill,insets 10", "[grow]", "[]10[]10[]10[]"));
        
        // Top section
        JPanel topPanel = new JPanel(new MigLayout("insets 0", "[grow][]20[]"));
        
        // Top section: Timer and Stopwatch buttons, with sound toggle and fullscreen
        JButton btnTimer = new JButton("Timer");
        btnTimer.setIcon(new FlatSVGIcon("Resources/Svg/timer.svg", 25, 25));
        JButton btnStopwatch = new JButton("Stopwatch");
        btnStopwatch.setIcon(new FlatSVGIcon("Resources/Svg/stopwatch.svg", 25, 25));
        btnSound = new JToggleButton(""); // Sound icon
        btnSound.setIcon(new FlatSVGIcon("Resources/Svg/volume.svg", 25, 25));
        ThemeToggle = new JButton(""); // Fullscreen icon
        ThemeToggle.setIcon(new FlatSVGIcon("Resources/Svg/lightMode.svg", 25, 25));
        
        // Use span and right alignment for sound and fullscreen buttons
        topPanel.add(btnTimer, "split 2, align left"); // Timer and Stopwatch on the left
        topPanel.add(btnStopwatch, "align left");
        topPanel.add(btnSound, "align right"); // Align sound and fullscreen to the right
        topPanel.add(ThemeToggle, "split 2,align right, wrap");
        
        this.add(topPanel, "growx, wrap");
        
        // Timer Display (Centering it properly)
        lblTime = new JLabel("00:00:0.00", JLabel.CENTER);
        lblTime.setFont(new Font("Arial", Font.BOLD, 48)); // Timer display font size
        this.add(lblTime, "span, align center, wrap");
     // Create a new panel to hold both buttons together
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[]20[]", "[]"));

        // Play button
        JButton btnPlay = new JButton("");
        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
        btnPlay.setPreferredSize(new Dimension(120, 40));

        // Replay button
        JButton btnReplay = new JButton("");
        btnReplay.setIcon(new FlatSVGIcon("Resources/Svg/replay.svg", 25, 25));
        btnReplay.setPreferredSize(new Dimension(120, 40));

        // Add both buttons to the button panel
        buttonPanel.add(btnPlay);

        
        add(buttonPanel,"span,align center");
        
        btnTimer.addActionListener(e -> {
        	MainApp.cardLayout.show(MainApp.cardPanel, "Timer");
        	MainApp.updateIcons(); // Update icons after switching
        });
        btnStopwatch.addActionListener(e ->{
        	MainApp.cardLayout.show(MainApp.cardPanel, "Stopwatch");
        	MainApp.updateIcons(); // Update icons after switching
        });
        
        // Toggle Mute
        btnSound.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundPlayer.toggleMute(); // Call the toggle method
                if(SoundPlayer.isMuted()) {
                	btnSound.setIcon(new FlatSVGIcon("Resources/Svg/mute.svg", 25, 25));
                }
                else
                	btnSound.setIcon(new FlatSVGIcon("Resources/Svg/volume.svg", 25, 25));
                //for debugging
                //System.out.println(SoundPlayer.isMuted());
            }
        });
        
        // Toggle night and light mode
        ThemeToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToggleTheme.toggleTheme(); // Call the toggle method
                if(ToggleTheme.isDarkTheme()) {
                	ThemeToggle.setIcon(new FlatSVGIcon("Resources/Svg/lightMode.svg", 25, 25));
                }
                else
                	ThemeToggle.setIcon(new FlatSVGIcon("Resources/Svg/darkMode.svg", 25, 25));
                //for debugging
//                System.out.println(ToggleTheme.isDarkTheme());
                SwingUtilities.updateComponentTreeUI(mainApp); // Refresh the UI
            }
        });
        
        btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sw == null || !sw.isAlive()) {
                    // Start timer if it's not running
                    sw = new StopwatchMechanism(lblTime);
                    sw.start(); // Start the timer thread
                    //btnPlay.setText("⏸️"); // Change button to pause icon
                    btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/pause.svg", 25, 25));
                    state = "playing";
                    buttonPanel.add(btnReplay);
                }
                else {
                    // Toggle pause and resume
                    if (state.equals("playing")) {
                        sw.pauseTimer(); // Pause the timer
                        //btnPlay.setText("▶"); // Change button to play icon
                        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
	                    state = "paused";
	                    System.out.println(state);
                    } else {
                        sw.resumeTimer(); // Resume the timer
                        //btnPlay.setText("⏸️"); // Change button to pause icon
                        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/pause.svg", 25, 25));
	                    state = "playing";
	                    System.out.println(state);
                    }
                }
			}
			
		});
        btnReplay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sw.resetTimer();
				sw.stopTimer();
				btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
				state = "finished";
				buttonPanel.remove(btnReplay);
				
			}
		});
        
    }
    public static void updateIcons() {
        // Update the mute button icon based on the current mute state
    	btnSound.setSelected(SoundPlayer.isMuted());
        if (SoundPlayer.isMuted()) {
            btnSound.setIcon(new FlatSVGIcon("Resources/Svg/mute.svg", 25, 25)); // Mute icon
        } else {
            btnSound.setIcon(new FlatSVGIcon("Resources/Svg/volume.svg", 25, 25)); // Volume icon
        }

        // Update the theme button icon based on the current theme (dark/light mode)
        if (ToggleTheme.isDarkTheme()) {
            ThemeToggle.setIcon(new FlatSVGIcon("Resources/Svg/lightMode.svg", 25, 25)); // Dark mode icon
        } else {
            ThemeToggle.setIcon(new FlatSVGIcon("Resources/Svg/darkMode.svg", 25, 25)); // Light mode icon
        }
    }
}
