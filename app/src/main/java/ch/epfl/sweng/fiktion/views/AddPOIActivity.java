package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.fiktion.R;

import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LATITUDE;
import static ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity.NEW_POI_LONGITUDE;

public class AddPOIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi);
    }

    // get the coordinates from the child GetLocationFromMapActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Double latitude = data.getDoubleExtra(NEW_POI_LATITUDE, 0);
                Double longitude = data.getDoubleExtra(NEW_POI_LONGITUDE, 0);

                ((EditText)findViewById(R.id.add_poi_latitude)).setText(latitude.toString());
                ((EditText)findViewById(R.id.add_poi_longitude)).setText(longitude.toString());
            }
        }
    }

    public void startGetLocationFromMapActivity(View view) {
        Intent getLocationFromMapIntent = new Intent(this, GetLocationFromMapActivity.class);
        startActivityForResult(getLocationFromMapIntent, 1);
    }
}
