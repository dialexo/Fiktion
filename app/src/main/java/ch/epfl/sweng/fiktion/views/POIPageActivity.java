package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

public class POIPageActivity extends MenuDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // give layout to parent menu class
        includeLayout = R.layout.activity_poipage;
        super.onCreate(savedInstanceState);

        // get POI name
        Intent from = getIntent();
        String poiName = from.getStringExtra("POI_NAME");

        // get POI from database
        Providers.database.getPoi(poiName, new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {

            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });
    }
}
