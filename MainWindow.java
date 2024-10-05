import javax.swing.*;
import java.awt.*;

public class MainWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Airline Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1200);
        frame.setLayout(new BorderLayout());

        // Set the background image
        JLabel background = new JLabel(new ImageIcon("res/wp2025512.jpg"));
        frame.setContentPane(background);
        background.setLayout(new FlowLayout());

        // Create a panel for buttons with a semi-transparent background
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make the panel transparent
        buttonPanel.setLayout(new FlowLayout());

        JButton adminButton = new JButton("Admin");
        JButton userButton = new JButton("User");
        JButton aboutButton = new JButton("About Us");
        JButton guidelinesButton = new JButton("Guidelines");
        JButton exitButton = new JButton("Exit");

        // Customize buttons
        adminButton.setBackground(Color.LIGHT_GRAY);
        userButton.setBackground(Color.LIGHT_GRAY);
        aboutButton.setBackground(Color.LIGHT_GRAY);
        guidelinesButton.setBackground(Color.LIGHT_GRAY);
        exitButton.setBackground(Color.LIGHT_GRAY);

        // Add buttons to the button panel
        buttonPanel.add(adminButton);
        buttonPanel.add(userButton);
        buttonPanel.add(aboutButton);
        buttonPanel.add(guidelinesButton);
        buttonPanel.add(exitButton);

        // Add the button panel to the background
        background.add(buttonPanel);

        // Set frame visibility
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center the window

        adminButton.addActionListener(e -> new Admin().showAdminLogin());
        userButton.addActionListener(e -> new User()); // User class needs to be implemented
        aboutButton.addActionListener(e -> showAboutUs());
        guidelinesButton.addActionListener(e -> showGuidelines());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private static void showAboutUs() {
        JOptionPane.showMessageDialog(null, "Go Airways: Your trusted airline for domestic travel.");
    }

    private static void showGuidelines() {
        String guidelines = "Guidelines to Book a Ticket:\n"
                + "1. Select your flight.\n"
                + "2. Enter passenger details.\n"
                + "3. Make payment.\n"
                + "4. Receive your PNR number.";
        JOptionPane.showMessageDialog(null, guidelines);
    }
}


