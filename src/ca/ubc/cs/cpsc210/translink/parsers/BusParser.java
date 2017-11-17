package ca.ubc.cs.cpsc210.translink.parsers;

import ca.ubc.cs.cpsc210.translink.model.Bus;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RouteManager;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Parser for bus location data
public class BusParser {

    /**
     * Parse buses from JSON response produced by TransLink query.  All parsed buses are
     * added to the given stop.  Bus location data that is missing any of the required
     * fields (RouteNo, Latitude, Longitude, Destination, RecordedTime) is silently
     * ignored and not added to stop.
     *
     * @param stop            stop to which parsed buses are to be added
     * @param jsonResponse    the JSON response produced by Translink
     * @throws JSONException  when:
     * <ul>
     *     <li>JSON response does not have expected format (JSON syntax problem)</li>
     *     <li>JSON response is not a JSON array</li>
     * </ul>
     */
    public static void parseBuses(Stop stop, String jsonResponse) throws JSONException {
        System.out.println(jsonResponse);
        JSONArray busLocationsArray = new JSONArray(jsonResponse);

        for (int index = 0; index < busLocationsArray.length(); index++) {
            JSONObject busLocationObject = busLocationsArray.getJSONObject(index);
            parseBus(stop, busLocationObject);
        }
    }

    /**
     * Parse single bus from given JSONObject and add it to stop
     *
     * @param stop  stop to which parsed bus is to be added
     * @param busLocationObject  JSON object representing bus
     */
    private static void parseBus(Stop stop, JSONObject busLocationObject) {
        try {
            String routeNum = busLocationObject.getString("RouteNo");
            Route route = RouteManager.getInstance().getRouteWithNumber(routeNum);

            Bus b = new Bus(route, busLocationObject.getDouble("Latitude"),
                    busLocationObject.getDouble("Longitude"), busLocationObject.getString("Destination"),
                    busLocationObject.getString("RecordedTime"));
            stop.addBus(b);
        } catch (JSONException e) {
            // silently ignore missing data
        }
    }
}
