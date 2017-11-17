package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Arrival;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A parser for the data returned by the Translink arrivals at a stop query
 */
public class ArrivalsParser {

    /**
     * Parse arrivals from JSON response produced by TransLink query.  All parsed arrivals are
     * added to the given stop assuming that corresponding JSON object has a RouteNo: and an
     * array of Schedules:
     * Each schedule must have an ExpectedCountdown, ScheduleStatus, and Destination.  If
     * any of the aforementioned elements is missing, the arrival is not added to the stop.
     *
     * @param stop             stop to which parsed arrivals are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when:
     * <ul>
     *     <li>JSON response does not have expected format (JSON syntax problem)</li>
     *     <li>JSON response is not an array</li>
     * </ul>
     * @throws ArrivalsDataMissingException  when no arrivals are found in the reply
     */
    public static void parseArrivals(Stop stop, String jsonResponse)
            throws JSONException, ArrivalsDataMissingException {
        JSONArray ja = new JSONArray(jsonResponse);
        int numberArrivalsAdded = 0;
        for (int i = 0; i < ja.length(); ++i) {
            try {
                JSONObject jo = ja.getJSONObject(i);
                // This should be a route arrival, with RouteNo, Direction, RouteName, and Schedules fields
                String routeNo = jo.getString("RouteNo");
                Route route = RouteManager.getInstance().getRouteWithNumber(routeNo);
                JSONArray arrivals = jo.getJSONArray("Schedules");
                for (int a = 0; a < arrivals.length(); a++) {
                    JSONObject ao = arrivals.getJSONObject(a);
                    // This should be an arrival, with ExpectedCountdown, Destination, ScheduleStatus fields
                    int timeToStop = ao.getInt("ExpectedCountdown");
                    String destination = ao.getString("Destination");
                    String status = ao.getString("ScheduleStatus");
                    Arrival arrival = new Arrival(timeToStop, destination, route);
                    arrival.setStatus(status);
                    stop.addArrival(arrival);
                    numberArrivalsAdded++;
                }
            } catch (JSONException e) {
                // Do nothing, in particular don't update any objects
            }
        }
        if (numberArrivalsAdded == 0) {
            throw new ArrivalsDataMissingException("All arrivals are missing some information");
        }
    }
}
