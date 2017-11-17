package ca.ubc.cs.cpsc210.translink;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.ui.ArrivalsListFragment;

/**
 * Activity to show list of arrivals to user
 */
public class ArrivalsActivity extends Activity {
    private static final String LOG_TAG = "Arrivals Tag";
    private static final String AA_TAG = "ArrivalsListFragment";
    private ArrivalsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.arrivals_list_layout);

        Intent i = getIntent();
        int stopNumber = i.getIntExtra(getString(R.string.stop_name_key), 99999);
        Stop stop = StopManager.getInstance().getStopWithNumber(stopNumber);
        setTitle(stop.getNumber() + " " + stop.getName());

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setSubtitle(R.string.arrivals_activity_subtitle);
        }

        if (savedInstanceState != null) {
            Log.i(LOG_TAG, "restoring from instance state");
            fragment = (ArrivalsListFragment) getFragmentManager()
                    .findFragmentByTag(AA_TAG);
        }
        else if (fragment == null) {
            Log.i(LOG_TAG, "fragment was null");

            fragment = new ArrivalsListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.arrivals_list, fragment, AA_TAG).commit();
        }

        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_right);
    }
}
