package com.onlinereservationapplication.admin;

public class BusRouteWithSeats
{
    private Busroute busRoute;
    private int availableSeats;

    // Getter for busRoute
    public Busroute getBusRoute() {
        return busRoute;
    }

    // Setter for busRoute
    public void setBusRoute(Busroute busRoute) {
        this.busRoute = busRoute;
    }

    // Getter for availableSeats
    public int getAvailableSeats() {
        return availableSeats;
    }

    // Setter for availableSeats
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
