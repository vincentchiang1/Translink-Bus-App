package ca.ubc.cs.cpsc210.translink.util;

/**
 * Spherical Geometry Utilities
 */
public class SphericalGeometry {
    private static final int RADIUS = 6371000;   // radius of earth in metres

    /**
     * Find distance in metres between two lat/lon points
     *
     * @param p1  first point
     * @param p2  second point
     * @return distance between p1 and p2 in metres
     */
    public static double distanceBetween(LatLon p1, LatLon p2) {
        double lat1 = p1.getLatitude() / 180.0 * Math.PI;
        double lat2 = p2.getLatitude() / 180.0 * Math.PI;
        double deltaLon = (p2.getLongitude() - p1.getLongitude()) / 180.0 * Math.PI;
        double deltaLat = (p2.getLatitude() - p1.getLatitude()) / 180.0 * Math.PI;

        double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return c * RADIUS;
    }
}
