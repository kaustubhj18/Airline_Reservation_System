import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private DatabaseHandler dbHandler;

    public User() {
        dbHandler = new DatabaseHandler();
        showLoginOrRegisterDialog();
    }

    private void showLoginOrRegisterDialog() {
        Object[] options = {"Login", "Register"};
        int choice = JOptionPane.showOptionDialog(null, "Select an option", "User Login/Register",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            showLoginDialog();
        } else if (choice == 1) {
            showRegistrationDialog();
        }
    }

    private void showLoginDialog() {
        JPanel panel = new JPanel();
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(null, panel, "User Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            if (validateUser(email, password)) {
                showUserPanel();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRegistrationDialog() {
        JPanel panel = new JPanel();
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField ageField = new JTextField(3);
        JTextField phoneField = new JTextField(15);
        JTextField aadhaarField = new JTextField(15);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Aadhaar No:"));
        panel.add(aadhaarField);

        int option = JOptionPane.showConfirmDialog(null, panel, "User Registration", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            int age = Integer.parseInt(ageField.getText());
            String phoneNumber = phoneField.getText();
            String aadhaarNo = aadhaarField.getText();

            if (isEmailTaken(email)) {
                JOptionPane.showMessageDialog(null, "Email is already taken. Please use a different email.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (registerUser(name, email, password, age, phoneNumber, aadhaarNo)) {
                JOptionPane.showMessageDialog(null, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String name, String email, String password, int age, String phoneNumber, String aadhaarNo) {
        String query = "INSERT INTO users (name, email, password, age, phone_number, aadhaar_no) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setInt(4, age);
            stmt.setString(5, phoneNumber);
            stmt.setString(6, aadhaarNo);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if user was successfully added
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean isEmailTaken(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next(); // Returns true if the email exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showUserPanel() {
        JFrame userFrame = new JFrame("User Panel");
        userFrame.setSize(600, 400);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userFrame.setLayout(new FlowLayout());

        JButton searchFlightsButton = new JButton("Search Flights");
        JButton bookFlightButton = new JButton("Book Flight");
        JButton cancelFlightButton = new JButton("Cancel Flight");
        JButton checkInButton = new JButton("Check-In");
        JButton showBookingsButton = new JButton("Show Bookings");

        // Adding action listeners
        searchFlightsButton.addActionListener(e -> searchFlights());
        bookFlightButton.addActionListener(e -> bookFlight());
        cancelFlightButton.addActionListener(e -> cancelFlight());
        checkInButton.addActionListener(e -> checkIn());
        showBookingsButton.addActionListener(e -> showBookings());

        JPanel panel = new JPanel();
        panel.add(searchFlightsButton);
        panel.add(bookFlightButton);
        panel.add(cancelFlightButton);
        panel.add(checkInButton);
        panel.add(showBookingsButton);

        userFrame.add(panel);
        userFrame.setVisible(true);
    }

    private void searchFlights() {
        String source = JOptionPane.showInputDialog("Enter source:");
        String destination = JOptionPane.showInputDialog("Enter destination:");
        String date = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");

        String query = "SELECT flight_id, flight_name, source, destination, departure_time, arrival_time, price FROM flights WHERE source = ? AND destination = ? AND DATE(departure_time) = ?";
        try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(query)) {
            stmt.setString(1, source);
            stmt.setString(2, destination);
            stmt.setString(3, date);
            ResultSet resultSet = stmt.executeQuery();

            JTable flightTable = new JTable(buildFlightTableModel(resultSet));
            JOptionPane.showMessageDialog(null, new JScrollPane(flightTable), "Available Flights", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching flights: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private TableModel buildFlightTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        List<Object[]> data = new ArrayList<>();
        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getObject(i);
            }
            data.add(row);
        }

        return new DefaultTableModel(data.toArray(new Object[0][]), columnNames);
    }

    private void bookFlight() {
        String aadhaarNo = JOptionPane.showInputDialog("Enter Aadhaar No:");
        if (aadhaarNo == null || aadhaarNo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Aadhaar No cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userQuery = "SELECT user_id, name, email FROM users WHERE aadhaar_no = ?";
        int userId = -1;
        try (PreparedStatement userStmt = dbHandler.getConnection().prepareStatement(userQuery)) {
            userStmt.setString(1, aadhaarNo);
            ResultSet userResultSet = userStmt.executeQuery();

            if (userResultSet.next()) {
                userId = userResultSet.getInt("user_id");
            } else {
                JOptionPane.showMessageDialog(null, "No user found with this Aadhaar number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching user details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String flightQuery = "SELECT flight_id, flight_name, source, destination, departure_time, arrival_time, price FROM flights";
        List<Flight> availableFlights = new ArrayList<>();

        try (PreparedStatement flightStmt = dbHandler.getConnection().prepareStatement(flightQuery)) {
            ResultSet flightResultSet = flightStmt.executeQuery();
            while (flightResultSet.next()) {
                Flight flight = new Flight(
                        flightResultSet.getString("flight_name"),
                        flightResultSet.getString("source"),
                        flightResultSet.getString("destination"),
                        flightResultSet.getString("departure_time"),
                        flightResultSet.getString("arrival_time"),
                        flightResultSet.getDouble("price")
                );
                availableFlights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching flights: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder flightOptions = new StringBuilder("Available Flights:\n");
        for (Flight flight : availableFlights) {
            flightOptions.append(": ").append(flight.getFlightName())
                    .append(" (").append(flight.getSource()).append(" to ").append(flight.getDestination())
                    .append(", Price: ").append(flight.getPrice()).append(")\n");
        }

        String flightIdStr = JOptionPane.showInputDialog(flightOptions.toString() + "Enter Flight ID to book:");
        if (flightIdStr == null || flightIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Flight ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int flightId = Integer.parseInt(flightIdStr);
        String pnrNo = generatePNR();
        String seatNo = "1A"; // Placeholder for seat selection logic

        String bookingQuery = "INSERT INTO bookings (user_id, flight_id, pnr_no, seat_no) VALUES (?, ?, ?, ?)";
        try (PreparedStatement bookingStmt = dbHandler.getConnection().prepareStatement(bookingQuery)) {
            bookingStmt.setInt(1, userId);
            bookingStmt.setInt(2, flightId);
            bookingStmt.setString(3, pnrNo);
            bookingStmt.setString(4, seatNo); // Ideally, get seat selection from user
            int rowsAffected = bookingStmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Booking successful! PNR: " + pnrNo, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Booking failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generatePNR() {
        return "PNR" + System.currentTimeMillis(); // Simple example for generating a PNR
    }

    private void cancelFlight() {
        String pnr = JOptionPane.showInputDialog("Enter PNR number to cancel:");
        if (pnr == null || pnr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "PNR number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cancelQuery = "DELETE FROM bookings WHERE pnr_no = ?";
        try (PreparedStatement cancelStmt = dbHandler.getConnection().prepareStatement(cancelQuery)) {
            cancelStmt.setString(1, pnr);
            int rowsAffected = cancelStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Booking canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No booking found with this PNR.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error canceling booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkIn() {
        String pnr = JOptionPane.showInputDialog("Enter PNR number for check-in:");
        if (pnr == null || pnr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "PNR number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String checkInQuery = "UPDATE bookings SET checked_in = TRUE WHERE pnr_no = ?";
        try (PreparedStatement checkInStmt = dbHandler.getConnection().prepareStatement(checkInQuery)) {
            checkInStmt.setString(1, pnr);
            int rowsAffected = checkInStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Check-in successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No booking found with this PNR.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during check-in: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBookings() {
        String aadhaarNo = JOptionPane.showInputDialog("Enter Aadhaar No:");
        if (aadhaarNo == null || aadhaarNo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Aadhaar No cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String bookingQuery = "SELECT b.pnr_no, f.flight_name, b.seat_no FROM bookings b JOIN flights f ON b.flight_id = f.flight_id WHERE b.user_id = (SELECT user_id FROM users WHERE aadhaar_no = ?)";
        try (PreparedStatement stmt = dbHandler.getConnection().prepareStatement(bookingQuery)) {
            stmt.setString(1, aadhaarNo);
            ResultSet resultSet = stmt.executeQuery();

            JTable bookingTable = new JTable(buildBookingTableModel(resultSet));
            JOptionPane.showMessageDialog(null, new JScrollPane(bookingTable), "Your Bookings", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private TableModel buildBookingTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        List<Object[]> data = new ArrayList<>();
        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getObject(i);
            }
            data.add(row);
        }

        return new DefaultTableModel(data.toArray(new Object[0][]), columnNames);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(User::new);
    }
}
