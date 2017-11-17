package ca.ubc.cs.cpsc210.translink;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import ca.ubc.cs.cpsc210.translink.parsers.ArrivalsParser;
import ca.ubc.cs.cpsc210.translink.parsers.BusParser;
import ca.ubc.cs.cpsc210.translink.parsers.exception.ArrivalsDataMissingException;
import ca.ubc.cs.cpsc210.translink.providers.DataProvider;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.providers.HttpArrivalDataProvider;
import ca.ubc.cs.cpsc210.translink.providers.HttpBusLocationDataProvider;
import ca.ubc.cs.cpsc210.translink.ui.LocationListener;
import ca.ubc.cs.cpsc210.translink.ui.MapDisplayFragment;
import ca.ubc.cs.cpsc210.translink.ui.StopSelectionListener;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.json.JSONException;

/**
 * Main activity
 */
public class BusesAreUs extends Activity implements LocationListener, StopSelectionListener {
    private static final String TSA_TAG = "TSA_TAG";
    private static final String MAP_TAG = "Map Fragment Tag";
    private MapDisplayFragment fragment;
    private TextView nearestStopLabel;
    private Stop myNearestStop;
    public static final String TRANSLINK_API_KEY = "This user hasn't registered or update the API key yet";
    public static BusesAreUs activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        Log.i(TSA_TAG, "onCreate");

        setContentView(R.layout.map_layout);
        myNearestStop = null;

        if (savedInstanceState != null) {
            Log.i(TSA_TAG, "restoring from instance state");
            fragment = (MapDisplayFragment) getFragmentManager()
                    .findFragmentByTag(MAP_TAG);
            StopManager stopManager = StopManager.getInstance();
            int stopid = savedInstanceState.getInt("nearestStop", -1);
            if (stopid != -1) {
                myNearestStop = stopManager.getStopWithNumber(stopid);
            }
        } else if (fragment == null) {
            Log.i(TSA_TAG, "fragment was null");

            fragment = new MapDisplayFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.map_fragment, fragment, MAP_TAG).commit();
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TSA_TAG, "onSaveInstanceState");

        if (myNearestStop != null) {
            outState.putInt("nearestStop", myNearestStop.getNumber());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nearestStopLabel = (TextView) findViewById(R.id.nearestStopLabel);
        if (myNearestStop == null) {
            nearestStopLabel.setText(R.string.out_of_range);
        } else {
            nearestStopLabel.setText(myNearestStop.getName());
        }
    }

    /**
     * Update nearest stop text view when user location changes
     *
     * @param nearest stop that is nearest to user (null if no stop within StopManager.RADIUS metres)
     */
    @Override
    public void onLocationChanged(Stop nearest, LatLon locn) {
        // TODO: Complete the implementation of this method (Task 6)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                handleAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show about dialog to user
     */
    private void handleAbout() {
        Log.d(TSA_TAG, "showing about dialog");
        AlertDialog.Builder dialogBldr = new AlertDialog.Builder(this);
        dialogBldr.setTitle(R.string.about);
        dialogBldr.setView(getLayoutInflater().inflate(R.layout.about_dialog_layout, null));
        dialogBldr.setNeutralButton(R.string.ok, null);
        dialogBldr.create().show();
    }

    /**
     * Set selected stop in StopManager.
     *
     * @param stop stop selected by user
     */
    @Override
    public void onStopSelected(Stop stop) {
        // TODO: Complete the implementation of this method (Task 7)
    }

    /**
     * Return a scaling factor for resources that should stay "about the same size" on the screen
     * @return      a factor to multiply fonts and widths of things to keep them visible on screen of varying resolution
     */
    public static float dpiFactor() {
        float x = activity.getResources().getDisplayMetrics().density;
        return x > 2.0f ? x / 2.0f : 1.0f;
    }

    /**
     * Download arrivals data for stop selected by user;
     *
     * @param stop stop selected by user
     */
    @Override
    public void onStopMoreInfo(Stop stop) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadArrivalDataTask().execute(stop);
        } else {
            Toast.makeText(this, "Unable to establish network connection!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start activity to show arrivals to user
     *
     * @param stop  Stop for which arrivals are to be shown
     */
    private void startArrivalActivity(Stop stop) {
        Intent i = new Intent(BusesAreUs.this, ArrivalsActivity.class);
        i.putExtra(getString(R.string.stop_name_key), stop.getNumber());
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_from_right, android.R.anim.fade_out);
    }

    /**
     * Task that will download and parse arrivals data
     */
    private class DownloadArrivalDataTask extends AsyncTask<Stop, Integer, String> {
        private ProgressDialog progressDialog;
        private Stop stop;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BusesAreUs.this, getString(R.string.arrivals_download_title),
                    getString(R.string.arrivals_download_msg), true, false);
        }

        @Override
        protected String doInBackground(Stop... stops) {
            stop = stops[0];
            DataProvider dataProvider = new HttpArrivalDataProvider(stop);
            String response = null;

            try {
                response = dataProvider.dataSourceToString();
            } catch (Exception e) {
                Log.d(BusesAreUs.TSA_TAG, e.getMessage(), e);
                Toast.makeText(getApplicationContext(), "Error downloading Translink data", Toast.LENGTH_LONG).show();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(getApplicationContext(), R.string.api_network, Toast.LENGTH_LONG).show();
            } else if (response.equals("Error")) {
                Log.d(BusesAreUs.TSA_TAG, "No arrivals data");
                Toast.makeText(getApplicationContext(), "No arrivals information available", Toast.LENGTH_LONG).show();
            } else {
                try {
                    stop.clearArrivals();
                    ArrivalsParser.parseArrivals(stop, response);
                    startArrivalActivity(stop);
                } catch (JSONException | ArrivalsDataMissingException e) {
                    Log.d(BusesAreUs.TSA_TAG, e.getMessage(), e);
                    Toast.makeText(getApplicationContext(), R.string.api_json, Toast.LENGTH_LONG).show();
                }
            }

            progressDialog.dismiss();
        }
    }

    /**
     * Task that will download and parse bus location data
     */
    private class DownloadBusLocationDataTask extends AsyncTask<Stop, Integer, String> {
        private ProgressDialog progressDialog;
        private Stop stop;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BusesAreUs.this, getString(R.string.translink_download_title),
                    getString(R.string.bus_locations_download_msg), true, false);
        }

        @Override
        protected String doInBackground(Stop... stns) {
            stop = stns[0];
            DataProvider dataProvider = new HttpBusLocationDataProvider(stop);
            String response = null;

            try {
                response = dataProvider.dataSourceToString();
            } catch (Exception e) {
                Log.d(BusesAreUs.TSA_TAG, e.getMessage(), e);
                Toast.makeText(getApplicationContext(), "Error downloading Translink data", Toast.LENGTH_LONG).show();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(getApplicationContext(), R.string.api_network, Toast.LENGTH_LONG).show();
            } else if (response.equals("Error")) {
                Log.d(BusesAreUs.TSA_TAG, "No bus locations data");
                Toast.makeText(getApplicationContext(), "No bus location information available", Toast.LENGTH_LONG).show();
            } else {
                try {
                    stop.clearBuses();
                    BusParser.parseBuses(stop, response);
                    fragment.plotBuses();

                } catch (JSONException e) {
                    Log.d(BusesAreUs.TSA_TAG, e.getMessage(), e);
                    Toast.makeText(getApplicationContext(), R.string.api_json, Toast.LENGTH_LONG).show();
                }
            }

            progressDialog.dismiss();
        }
    }
}
