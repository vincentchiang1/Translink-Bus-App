package ca.ubc.cs.cpsc210.translink.ui;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ca.ubc.cs.cpsc210.translink.R;
import ca.ubc.cs.cpsc210.translink.model.Arrival;

import java.util.ArrayList;

/**
 * Fragment to display list of arrivals at selected stop
 */
public class ArrivalsListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        ArrayList<Arrival> arrivals = getArrivalsForSelectedStop();
        ArrivalsAdapter adapter = new ArrivalsAdapter(arrivals);

        setListAdapter(adapter);
    }

    /**
     * Get arrivals for selected stop
     *
     * @return   list of arrivals at selected stop
     */
    private ArrayList<Arrival> getArrivalsForSelectedStop() {
        // TODO: Complete the implementation of this method (Task 9)
        return new ArrayList<>();
    }

    /**
     * Array adapter for list of arrivals displayed to user
     */
    private class ArrivalsAdapter extends ArrayAdapter<Arrival> {
        public ArrivalsAdapter(ArrayList<Arrival> arrivals) {
            super(getActivity(), 0, arrivals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.arrival_item, null);
            }

            Arrival arrival = getItem(position);
            TextView destination = (TextView) convertView.findViewById(R.id.destination);
            destination.setText(arrival.getDestination());
            TextView platform = (TextView) convertView.findViewById(R.id.platform);
            platform.setText(arrival.getRoute().getNumber());
            TextView waitTime = (TextView) convertView.findViewById(R.id.wait_time);
            waitTime.setText(Integer.toString(arrival.getTimeToStopInMins()) + " mins");
            switch (arrival.getStatus()) {
                case "+":
                    waitTime.setBackgroundColor(Color.GREEN);
                    break;
                case "-":
                    waitTime.setBackgroundColor(Color.RED);
                    break;
            }

            return convertView;
        }
    }
}
