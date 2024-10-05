import java.sql.*;

public class DatabaseHandler {
    private static Connection connection;

    public DatabaseHandler() {
        try {
            connection = DatabaseConnection.getConnection(); // Use the connection method
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFlight(Flight flight) {
        String query = "INSERT INTO flights (flight_name, source, destination, departure_time, arrival_time, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, flight.getFlightName());
            stmt.setString(2, flight.getSource());
            stmt.setString(3, flight.getDestination());
            stmt.setString(4, flight.getDepartureTime());
            stmt.setString(5, flight.getArrivalTime());
            stmt.setDouble(6, flight.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllUsers() {
        String query = "SELECT * FROM users";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getAllBookings() {
        String query = "SELECT * FROM bookings";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Optional: Close the connection if you need to
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllFlights() {
        String query = "SELECT * FROM flights";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection() {
        return connection;
    }
}
