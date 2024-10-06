package mainClock;

import java.awt.FlowLayout;
import javax.swing.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class Test2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set the FlatLaf look and feel
            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("FlatLaf SVG Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLayout(new FlowLayout());

            // Create an SVG icon using FlatLaf Extras
            FlatSVGIcon svgIcon = new FlatSVGIcon("src/Resources/Svg/mute.svg", 32, 32); // Adjust size as needed

            // Create a JLabel with SVG icon
            JLabel svgLabel = new JLabel("Label with SVG Icon");
            svgLabel.setIcon(svgIcon);

            // Create a JButton with SVG icon
            JButton svgButton = new JButton("Button with SVG Icon");
            svgButton.setIcon(svgIcon);

            // Add the label and button to the frame
            frame.add(svgLabel);
            frame.add(svgButton);

            frame.setVisible(true);
        });
    }
}
