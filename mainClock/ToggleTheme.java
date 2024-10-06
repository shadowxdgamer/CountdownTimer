package mainClock;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class ToggleTheme {
    private static boolean isDarkTheme = true;

    public static void setLookAndFeel() {
        try {
            if (isDarkTheme) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toggleTheme() {
        isDarkTheme = !isDarkTheme; // Toggle the theme flag
        setLookAndFeel(); // Set the new look and feel
    }

    public static boolean isDarkTheme() {
        return isDarkTheme;
    }
}
