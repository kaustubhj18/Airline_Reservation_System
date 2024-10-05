import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class Admin {
    private DatabaseHandler dbHandler; // DatabaseHandler to manage connections and queries

    public Admin() {
        dbHandler = new DatabaseHandler();
    }

    public void showAdminLogin() {
        JPanel panel = new JPanel();
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createHorizontalStrut(15)); // Spacer
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(null, panel, "Admin Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Check credentials
            if (username.equals("VAK") && password.equals("12345")) {
                showAdminPanel();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAdminPanel() {
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setSize(600, 400);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLayout(new FlowLayout());

        JButton addFlightButton = new JButton("Add Flight");
        JButton viewUsersButton = new JButton("View Users");
        JButton viewBookingsButton = new JButton("View Bookings");
        JButton viewFlightsButton = new JButton("Show Flights");
        JButton exitButton = new JButton("Exit");

        adminFrame.add(addFlightButton);
        adminFrame.add(viewFlightsButton);
        adminFrame.add(viewUsersButton);
        adminFrame.add(viewBookingsButton);
        adminFrame.add(exitButton);

        adminFrame.setVisible(true);
        adminFrame.setLocationRelativeTo(null); // Center the window

        addFlightButton.addActionListener(e -> showAddFlightDialog());
        viewUsersButton.addActionListener(e -> showUserDetails());
        viewBookingsButton.addActionListener(e -> showUserBookings());
        viewFlightsButton.addActionListener(e -> showAllFlights());
        exitButton.addActionListener(e -> adminFrame.dispose());
    }

    private void showAllFlights() {
        String query = "SELECT * FROM flights";
        try {
            ResultSet resultSet = dbHandler.getAllFlights();

            // Get metadata to know the number of columns
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column names
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            // Create a data list to hold the rows
            ArrayList<Object[]> data = new ArrayList<>();
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                data.add(row);
            }

            // Convert List to Array
            Object[][] rowData = data.toArray(new Object[0][]);

            // Create JTable
            JTable table = new JTable(rowData, columnNames);
            table.setFillsViewportHeight(true);

            // Add JScrollPane to contain the JTable
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(null, scrollPane, "Flight Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddFlightDialog() {
        JPanel panel = new JPanel();
        JTextField flightNameField = new JTextField(15);
        JTextField sourceField = new JTextField(15);
        JTextField destinationField = new JTextField(15);
        JTextField departureTimeField = new JTextField(15);
        JTextField arrivalTimeField = new JTextField(15);
        JTextField priceField = new JTextField(15);

        panel.add(new JLabel("Flight Name:"));
        panel.add(flightNameField);
        panel.add(new JLabel("Source:"));
        panel.add(sourceField);
        panel.add(new JLabel("Destination:"));
        panel.add(destinationField);
        panel.add(new JLabel("Departure Time (HH:MM):"));
        panel.add(departureTimeField);
        panel.add(new JLabel("Arrival Time (HH:MM):"));
        panel.add(arrivalTimeField); // Fixing the variable name here
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int option = JOptionPane.showConfirmDialog(null, panel, "Add Flight", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String flightName = flightNameField.getText();
            String source = sourceField.getText();
            String destination = destinationField.getText();
            String departureTime = departureTimeField.getText();
            String arrivalTime = arrivalTimeField.getText();
            double price;

            try {
                price = Double.parseDouble(priceField.getText());
                // Create and add the flight to the database
                Flight flight = new Flight(flightName, source, destination, departureTime, arrivalTime, price);
                dbHandler.addFlight(flight);
                JOptionPane.showMessageDialog(null, "Flight added successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUserDetails() {
        String query = "SELECT * FROM users";
        try {
            ResultSet resultSet = dbHandler.getAllUsers();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            ArrayList<Object[]> data = new ArrayList<>();
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                data.add(row);
            }

            Object[][] rowData = data.toArray(new Object[0][]);
            JTable table = new JTable(rowData, columnNames);
            table.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(null, scrollPane, "User Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showUserBookings() {
        StringBuilder bookingDetails = new StringBuilder();
        try {
            ResultSet resultSet = dbHandler.getAllBookings();
            while (resultSet.next()) {
                bookingDetails.append("Booking ID: ").append(resultSet.getInt("booking_id")).append("\n")
                        .append("User ID: ").append(resultSet.getInt("user_id")).append("\n")
                        .append("Flight ID: ").append(resultSet.getInt("flight_id")).append("\n")
                        .append("PNR No: ").append(resultSet.getString("pnr_no")).append("\n")
                        .append("Seat No: ").append(resultSet.getString("seat_no")).append("\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, bookingDetails.toString(), "User Bookings", JOptionPane.INFORMATION_MESSAGE);
    }
}
