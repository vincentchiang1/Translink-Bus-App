package ca.ubc.cs.cpsc210.translink.ui;

import android.view.View;
import android.widget.Button;
import ca.ubc.cs.cpsc210.translink.R;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * StopInfoWindow displayed when stop is tapped
 */
class StopInfoWindow extends MarkerInfoWindow {
    private StopSelectionListener stopSelectionListener;

    /**
     * Constructor
     *
     * @param listener   listener to handle user selection of stop
     * @param mapView    the map view on which this info window will be displayed
     */
    public StopInfoWindow(StopSelectionListener listener, MapView mapView) {
        super(R.layout.bonuspack_bubble, mapView);
        stopSelectionListener = listener;

        Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stop selected = (Stop) mMarkerRef.getRelatedObject();
                stopSelectionListener.onStopMoreInfo(selected);
                StopInfoWindow.this.close();
            }
        });
    }

    @Override public void onOpen(Object item){
        super.onOpen(item);
        mView.findViewById(R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
        stopSelectionListener.onStopSelected((Stop)mMarkerRef.getRelatedObject());
    }
}
