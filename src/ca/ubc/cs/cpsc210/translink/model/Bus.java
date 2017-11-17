package ca.ubc.cs.cpsc210.translink.model;

import ca.ubc.cs.cpsc210.translink.util.LatLon;

// Represents a bus serving a particular route having a location at a specified time
public class Bus {
    private Route route;
    private String dest;
    private String time;
    private LatLon latlon;

    /**
     * Constructor
     * @param route  the bus route
     * @param lat    latitude of bus
     * @param lon    longitude of bus
     * @param dest   destination
     * @param time   time at which location was recorded
     */
    public Bus(Route route, double lat, double lon, String dest, String time) {
        this.route = route;
        this.dest = dest;
        this.time = time;
        latlon = new LatLon(lat, lon);
    }

    /**
     * Gets bus route
     * @return bus route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Gets bus location as LatLon object
     * @return bus location
     */
    public LatLon getLatLon() {
        return latlon;
    }

    /**
     * Gets destination
     * @return destination of this bus
     */
    public String getDestination() {
        return dest;
    }

    /**
     * Gets time bus location was recorded
     * @return  time location was recorded
     */
    public String getTime() {
        return time;
    }

}
