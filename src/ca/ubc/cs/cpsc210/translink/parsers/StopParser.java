package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.exception.StopDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.providers.FileDataProvider;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A parser for the data returned by Translink stops query
 */
public class StopParser {

    private String filename;

    public StopParser(String filename) {
        this.filename = filename;
    }
    /**
     * Parse stop data from the file and add all stops to stop manager.
     *
     */
    public void parse() throws IOException, StopDataMissingException, JSONException{
        DataProvider dataProvider = new FileDataProvider(filename);

        parseStops(dataProvider.dataSourceToString());
    }
    /**
     * Parse stop information from JSON response produced by Translink.
     * Stores all stops and routes found in the StopManager and RouteManager.
     *
     * @param  jsonResponse    string encoding JSON data to be parsed
     * @throws JSONException when:
     * <ul>
     *     <li>JSON data does not have expected format (JSON syntax problem)</li>
     *     <li>JSON data is not an array</li>
     * </ul>
     * If a JSONException is thrown, no stops should be added to the stop manager
     * @throws StopDataMissingException when
     * <ul>
     *  <li> JSON data is missing Name, StopNo, Routes or location (Latitude or Longitude) elements for any stop</li>
     * </ul>
     * If a StopDataMissingException is thrown, all correct stops are first added to the stop manager.
     */

    public void parseStops(String jsonResponse)
            throws JSONException, StopDataMissingException {
        JSONArray stops = new JSONArray(jsonResponse);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < stops.length(); ++i) {
            int stopNo = 0;
            try {
                JSONObject onestop = stops.getJSONObject(i);
                stopNo = onestop.getInt("StopNo");
                String stopName = onestop.getString("Name");
                double stopLat = onestop.getDouble("Latitude");
                double stopLon = onestop.getDouble("Longitude");
                String routes = onestop.getString("Routes");
                String[] routearray = routes.split(", *");
                Stop s = StopManager.getInstance().getStopWithNumber(stopNo, stopName, new LatLon(stopLat, stopLon));
                for (String route : routearray) {
                    Route r = RouteManager.getInstance().getRouteWithNumber(route);
                    s.addRoute(r);
                }
            } catch (JSONException e) {
                sb.append(stopNo != 0 ? stopNo : "unnumbered route");
                sb.append(" ");
            }
        }
        if (sb.length() > 0) {
            throw new StopDataMissingException("Missing required data about stops: " + sb.toString());
        }
    }
}
