package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MapViewOverlay {
    protected MapView mapView;
    protected Context context;
    protected LatLon northWest;
    protected LatLon southEast;


    public MapViewOverlay(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    /**
     * Update the fields northWest and southEast to correspond to the corners of the visible area of the map.
     * These fields can then be used to determine what stops and bus route pattern segments are visible.
     */
    protected void updateVisibleArea() {
        GeoPoint northwest = (GeoPoint) mapView.getProjection().fromPixels(0, 0);
        GeoPoint southeast = (GeoPoint) mapView.getProjection().fromPixels(mapView.getWidth(), mapView.getHeight());
        northWest = new LatLon(northwest.getLatitude(), northwest.getLongitude());
        southEast = new LatLon(southeast.getLatitude(), southeast.getLongitude());
    }
}
