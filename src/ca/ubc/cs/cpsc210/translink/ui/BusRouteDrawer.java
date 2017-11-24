package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A bus route drawer
public class BusRouteDrawer extends MapViewOverlay {
    /**
     * overlay used to display bus route legend text on a layer above the map
     */
    private BusRouteLegendOverlay busRouteLegendOverlay;
    /**
     * overlays used to plot bus routes
     */
    private List<Polyline> busRouteOverlays;

    /**
     * Constructor
     *
     * @param context the application context
     * @param mapView the map view
     */
    public BusRouteDrawer(Context context, MapView mapView) {
        super(context, mapView);
        busRouteLegendOverlay = createBusRouteLegendOverlay();
        busRouteOverlays = new ArrayList<>();
    }

    /**
     * Plot each visible segment of each route pattern of each route going through the selected stop.
     *
     * @param zoomLevel the zoom level of the map
     */
    public void plotRoutes(int zoomLevel) {

        updateVisibleArea();
        busRouteOverlays.clear();
        busRouteLegendOverlay.clear();

        Polyline line;
        List<GeoPoint> listPoints;
        if (StopManager.getInstance().getSelected() != null) {
            for (Route next : RouteManager.getInstance()) {
                if (next.getStops().contains(StopManager.getInstance().getSelected())) {
                    busRouteLegendOverlay.add(next.getNumber());

                    for (RoutePattern nextPattern : next.getPatterns()) {
                        for (int i = 0; i < nextPattern.getPath().size() - 1; i++) {
                            line = new Polyline(context);
                            line.setColor(busRouteLegendOverlay.getColor(next.getNumber()));
                            line.setWidth(getLineWidth(zoomLevel));
                            listPoints = new ArrayList<>();


                            if (Geometry.rectangleIntersectsLine(northWest, southEast, nextPattern.getPath().get(i), nextPattern.getPath().get(i + 1))) {

                                GeoPoint point1 = new GeoPoint(nextPattern.getPath().get(i).getLatitude(), nextPattern.getPath().get(i).getLongitude());
                                GeoPoint point2 = new GeoPoint(nextPattern.getPath().get(i + 1).getLatitude(), nextPattern.getPath().get(i + 1).getLongitude());
                                listPoints.add(point1);
                                listPoints.add(point2);

                                line.setPoints(listPoints);
                                busRouteOverlays.add(line);

                            }
                        }
                    }

                }

            }


        }
    }

    public List<Polyline> getBusRouteOverlays() {
        return Collections.unmodifiableList(busRouteOverlays);
    }

    public BusRouteLegendOverlay getBusRouteLegendOverlay() {
        return busRouteLegendOverlay;
    }


    /**
     * Create text overlay to display bus route colours
     */
    private BusRouteLegendOverlay createBusRouteLegendOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);
        return new BusRouteLegendOverlay(rp, BusesAreUs.dpiFactor());
    }

    /**
     * Get width of line used to plot bus route based on zoom level
     *
     * @param zoomLevel the zoom level of the map
     * @return width of line used to plot bus route
     */
    private float getLineWidth(int zoomLevel) {
        if (zoomLevel > 14)
            return 7.0f * BusesAreUs.dpiFactor();
        else if (zoomLevel > 10)
            return 5.0f * BusesAreUs.dpiFactor();
        else
            return 2.0f * BusesAreUs.dpiFactor();
    }
}
