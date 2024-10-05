public class Flight {
    private String flightName;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double price;

    public Flight(String flightName, String source, String destination, String departureTime, String arrivalTime, double price) {
        this.flightName = flightName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public String getFlightName() {
        return flightName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }

}
