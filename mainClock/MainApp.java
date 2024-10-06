package mainClock;



import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import net.miginfocom.swing.*;

public class MainApp extends JFrame {
	int hh,mm = 5,ss;
	String state = "paused";
	private Time t;
	private static JToggleButton btnSound;
	private static JButton ThemeToggle;
	public static CardLayout cardLayout; 
	public static JPanel cardPanel;
    private JButton btnPlay;
    private JLabel lblTime;
    private boolean isEditingTime = false;  // Track if we are in edit mode
    public static String timeInput = "000500";  // Placeholder for time input in HHMMSS format
    private Timer cursorBlinkTimer;
    private boolean showCursor = false;  // Blinking cursor flag

	public MainApp(){
		init();
	}
	private void init() {
		 	setTitle("Timer");
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        ImageIcon icon = new ImageIcon(MainApp.class.getResource("/Resources/Svg/logo.png"));
	        setIconImage(icon.getImage());

	        setSize(new Dimension(600, 400)); // Adjust size to match design
	        setLocationRelativeTo(null);
	        //CardLayout
	        cardLayout = new CardLayout();
	        cardPanel = new JPanel(cardLayout);
	        
	        JPanel mainPanel = new JPanel(new MigLayout("fill,insets 10","[grow]","[]10[]10[]10[]"));
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
	        mainPanel.add(topPanel, "growx, wrap");
	        // Timer Display (Centering it properly)
	        lblTime = new JLabel("00:05:00", JLabel.CENTER);
	        lblTime.setFont(new Font("Arial", Font.BOLD, 48)); // Timer display font size
	        mainPanel.add(lblTime, "span, align center, wrap");
	     // Add mouse listener to the label to enter interactive mode
	        lblTime.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	                if (!isEditingTime) {  // Only enter edit mode if not already in it there was a bug if you double click you can input twice
	                    enterEditMode(); // Switch to edit mode
	                }
	            }
	            @Override
	            public void mouseEntered(MouseEvent e) {
	                lblTime.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  // Set hand cursor when hovering
	            }

	            @Override
	            public void mouseExited(MouseEvent e) {
	                lblTime.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));  // Reset cursor when leaving
	            }
	        });
	        // Key listener for capturing number input
	        lblTime.addKeyListener(new KeyAdapter() {
	            @Override
	            public void keyTyped(KeyEvent e) {
	                if (isEditingTime) {
	                    char keyChar = e.getKeyChar();
	                    if (Character.isDigit(keyChar)) {
	                        timeInput = shiftLeftAndAddDigit(timeInput, keyChar);  // Shift input to the left and add digit
	                        updateLabelTime();  // Update label with new input
	                    }
	                }
	            }
	            @Override
	            public void keyPressed(KeyEvent e) {
	                if (isEditingTime && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
	                    timeInput = shiftRightAndRemoveDigit(timeInput);  // Shift input to the right and add zero at the start
	                    updateLabelTime();  // Update label after backspace
	                }
	            }
	        });
	        
	        

	        // Time adjustment buttons (centered under the time display)
	        JButton btn30Sec = new JButton("+0:30");
	        JButton btn1Min = new JButton("+1:00");
	        JButton btn5Min = new JButton("+5:00");
	        JPanel adjustButtonsPanel = new JPanel(new MigLayout("", "[]10[]10[]"));
	        // Span and align center to ensure proper centering
	        adjustButtonsPanel.add(btn30Sec);
	        adjustButtonsPanel.add(btn1Min);
	        adjustButtonsPanel.add(btn5Min);
	        mainPanel.add(adjustButtonsPanel, "span ,align center, wrap");
	        // Play button (at the bottom)
	        btnPlay = new JButton("");
	        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
	        btnPlay.setPreferredSize(new Dimension(120, 40)); // Large button at bottom
	        mainPanel.add(btnPlay, "span, align center");

	        
	        btnTimer.addActionListener(e -> {
	        	cardLayout.show(cardPanel, "Timer");
	        	StopwatchPanel.updateIcons(); // Update icons after switching
	        });
	        btnStopwatch.addActionListener(e ->{
	        	cardLayout.show(cardPanel, "Stopwatch");
	        	StopwatchPanel.updateIcons(); // Update icons after switching
	        	
	        });

	        //Creating Stopwatch panel
	        StopwatchPanel panel1 = new StopwatchPanel(this);
	        cardPanel.add(mainPanel, "Timer");
	        cardPanel.add(panel1, "Stopwatch");
	        setContentPane(cardPanel);
	        // Timer to handle blinking cursor
	        cursorBlinkTimer = new Timer(500, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	if (isEditingTime) {
	                    showCursor = !showCursor;  // Toggle cursor visibility
	                    updateLabelTime();  // Refresh the label to show or hide cursor
	                }
	            }
	        });

	        
	        // Prevent entering edit mode if buttons are pressed
	        mainPanel.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	                if (e.getSource() != lblTime && isEditingTime) {
	                    exitEditMode();
	                }
	            }
	        });
	        // Add listener to buttons to exit edit mode when clicked
	        btnTimer.addMouseListener(stopEditModeOnClick);
	        btnStopwatch.addMouseListener(stopEditModeOnClick);
	        btnSound.addMouseListener(stopEditModeOnClick);
	        ThemeToggle.addMouseListener(stopEditModeOnClick);
	        btn30Sec.addMouseListener(stopEditModeOnClick);
	        btn1Min.addMouseListener(stopEditModeOnClick);
	        btn5Min.addMouseListener(stopEditModeOnClick);
	        btnPlay.addMouseListener(stopEditModeOnClick);
	        
	        btnPlay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (state.equals("finished")) {
	                    // If the button says "Play Again," reset and start again
	                    t = new Time(); // Create a new timer instance
	                    t.setTime(ss, mm, hh, lblTime); // Reset the timer
	                    addTimerListener(); // Call the method to add the listener
	                    t.start(); // Start the new timer
	                    t.addTime(1,0);
                        t.pauseTimer(); // Pause the timer
	                    //btnPlay.setText("▶"); // Change button to pause icon
                        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
	                    state = "paused";
	                    SoundPlayer.stopSound(); // Start the sound when the timer starts again
	                    SoundPlayer.isRunning = false;
	                }

	                else if (t == null || !t.isAlive()) {
	                    // Start timer if it's not running
	                    t = new Time();
	                    t.setTime(ss, mm, hh, lblTime); // Reset the timer
	                    addTimerListener(); // Call the method to add the listener
	                    t.start(); // Start the timer thread
	                    //btnPlay.setText("⏸️"); // Change button to pause icon
	                    btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/pause.svg", 25, 25));
	                    state = "playing";
	                }
	                else {
	                    // Toggle pause and resume
	                    if (state.equals("playing")) {
	                        t.pauseTimer(); // Pause the timer
	                        //btnPlay.setText("▶"); // Change button to play icon
	                        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
		                    state = "paused";
		                    System.out.println(state);
	                    } else {
	                        t.resumeTimer(); // Resume the timer
	                        //btnPlay.setText("⏸️"); // Change button to pause icon
	                        btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/pause.svg", 25, 25));
		                    state = "playing";
		                    System.out.println(state);
	                    }
	                }
				}
				
			});
	        btn30Sec.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					createTimer();
					if (t != null) {
					t.addTime(30, 0);
		            // Update the timer with the newly inputted time
		            updateTimer();
					}
					else {
			            t = new Time();
			            t.setTime(30, 0, 0, lblTime);

					}
				}
			});
	        btn1Min.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					createTimer();
					if (t != null) {
					t.addTime(0, 1);
		            // Update the timer with the newly inputted time
		            updateTimer();
					}
					else {
			            t = new Time();
			            t.setTime(0, 1, 0, lblTime);
					}
				}
			});
	        btn5Min.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					createTimer();
					if (t != null) {
					t.addTime(0, 5);
		            // Update the timer with the newly inputted time
		            updateTimer();
					}
					else {
			            t = new Time();
			            t.setTime(0, 5, 0, lblTime);
					}
				}
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
//	                System.out.println(ToggleTheme.isDarkTheme());
	                SwingUtilities.updateComponentTreeUI(MainApp.this); // Refresh the UI
	            }
	        });
	}
	        // Method to add the TimerListener
	        private void addTimerListener() {
	            t.addTimerListener(new TimerListener() {
	                @Override
	                public void onTimerComplete() {
	                	state = "finished";
	                    //btnPlay.setText("Play Again");
	                    btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/replay.svg", 25, 25));
	                    
	                }
	            });
	}
	     // Method to switch to edit mode
	        private void enterEditMode() {
	            isEditingTime = true;
	            lblTime.setFocusable(true);
	            lblTime.requestFocusInWindow(); // Ensure the label can receive key input
	            if(t!= null && t.isAlive()) {
					t.pauseTimer();
					//btnPlay.setText("▶");
					btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
					state = "paused";
	            }
	            cursorBlinkTimer.start(); // Start the cursor blinking

	        }

	        // Method to exit edit mode
	        private void exitEditMode() {
	            isEditingTime = false;
	            cursorBlinkTimer.stop(); // Stop blinking cursor
	            if(t == null) {
	            	t = new Time();
		            t.setTime(ss, mm, hh, lblTime);  // Set the updated time
	            }
	            if(t != null && t.isAlive()) {
				t.pauseTimer();
				state = "paused";
				//btnPlay.setText("▶");
				btnPlay.setIcon(new FlatSVGIcon("Resources/Svg/play.svg", 25, 25));
				}
	            // Update the timer with the newly inputted time
	            updateTimer();
	            t.setTime(ss, mm, hh, lblTime);  // Set the updated time
	            
	            updateLabelTime();  // Update label to remove cursor
	        }
	        // Prevent entering edit mode if buttons are pressed
	        MouseAdapter stopEditModeOnClick = new MouseAdapter() {
	            @Override
	            public void mousePressed(MouseEvent e) {
	                if (isEditingTime) {
	                    exitEditMode();  // Exit edit mode when buttons are pressed
	                }
	            }
	        };

	        // Shift the input to the left and add a new digit on the right
	        private String shiftLeftAndAddDigit(String currentInput, char newDigit) {
	            currentInput = currentInput.substring(1) + newDigit;
	            return currentInput;
	        }

	        // Shift the input to the right, removing the rightmost digit and adding a zero at the start
	        private String shiftRightAndRemoveDigit(String currentInput) {
	            currentInput = "0" + currentInput.substring(0, currentInput.length() - 1);
	            return currentInput;
	        }
	     // Method to get hours (HH)
	        private String getHours() {
	            return timeInput.substring(0, 2);
	        }

	        // Method to get minutes (MM)
	        private String getMinutes() {
	            return timeInput.substring(2, 4);
	        }

	        // Method to get seconds (SS)
	        private String getSeconds() {
	            return timeInput.substring(4, 6);
	        }
	        // Method to update the label with the current input and blink cursor
	        private void updateLabelTime() {
	            String formattedTime = String.format("%s:%s:%s", 
	                getHours(), 
	                getMinutes(), 
	                getSeconds());

	            // Append blinking cursor if we're in edit mode
	            if (isEditingTime && showCursor) {
	                formattedTime += "|";  // Add blinking cursor bar
	            }
	            lblTime.setText(formattedTime);  // Set the updated time in the label
	        }
	        private void createTimer() {
	        	if(t == null) {
	        		t = new Time();
		            t.setTime(ss, mm, hh, lblTime);
	        	}
	        }
	        private void updateTimer() {
	            hh = Integer.valueOf(getHours());
	            mm = Integer.valueOf(getMinutes());
	            ss = Integer.valueOf(getSeconds());
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


	public static void main(String[] args) {
        // Set initial theme
        FlatRobotoFont.install();
        ToggleTheme.setLookAndFeel();
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
		EventQueue.invokeLater(() -> new MainApp().setVisible(true));

	}

}
