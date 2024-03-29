package ch.epfl.sweng.fiktion.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.views.parents.MenuDrawerActivity;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

@SuppressWarnings("FieldCanBeLocal") // those fields might be used elsewhere
public class TextSearchActivity extends MenuDrawerActivity {

    private EditText searchField;
    private LinearLayout resultsList;
    private ImageButton searchButton;
    private Context ctx = this;
    private TextView noResults;
    private final int SEARCH_TIMEOUT = 3000;
    private final int BUTTON_TIMEOUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        includeLayout = R.layout.activity_text_search;
        super.onCreate(savedInstanceState);

        // adjust background color
        ScrollView bg = (ScrollView) findViewById(R.id.menu_scroll);
        bg.setBackgroundColor(getResources().getColor(R.color.bgLightGray));

        // get search text
        Intent i = getIntent();
        String searchText = i.getStringExtra("SEARCH_TEXT");

        // find search box
        searchField = (EditText) findViewById(R.id.searchText);
        searchField.setText(searchText);

        // find results list
        resultsList = (LinearLayout) findViewById(R.id.resultsList);

        // find no results text
        noResults = (TextView) findViewById(R.id.noResults);

        // find search button
        searchButton = (ImageButton) findViewById(R.id.searchButton);

        // search
        if (searchText != null && !searchText.isEmpty()) {
            search(searchText);
        }
    }

    /**
     * Searches for given text and updates UI when results are found
     *
     * @param text the text to search POIs for
     */
    private void search(String text) {
        // disable button for a short while to prevent spamming
        searchButton.setEnabled(false);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        searchButton.setEnabled(true);
                    }
                },
                BUTTON_TIMEOUT
        );

        // clear previous results
        if (resultsList.getChildCount() > 0) resultsList.removeAllViews();
        // check and alert if search field is empty
        if (text.isEmpty()) {
            searchField.setError(getString(R.string.empty_search));
            Toast.makeText(ctx, getString(R.string.empty_search), Toast.LENGTH_SHORT).show();
            return;
        }

        // perform search
        DatabaseProvider.getInstance().searchByText(text, new DatabaseProvider.GetMultiplePOIsListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                View pv = POIDisplayer.createPoiCard(poi, ctx);

                // add it to the results list
                resultsList.addView(pv);

                // we found a POI so update no results message
                if (noResults.getVisibility() == View.VISIBLE)
                    noResults.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure() {
                // something went wrong, show error toast
                String err = "Request failed, please try again later.";
                Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
            }
        });

        // show loading text (will be hidden if new result, and replaced by no results if nothing shows up)
        noResults.setText(R.string.loading_text);
        if (noResults.getVisibility() == View.INVISIBLE) noResults.setVisibility(View.VISIBLE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        noResults.setText(R.string.no_results_found);
                    }
                },
                SEARCH_TIMEOUT
        );
    }

    /**
     * Triggered by search button press
     */
    public void triggerSearch(@SuppressLint("Unused paramater") View view) {
        String searchText = searchField.getText().toString();
        // search
        search(searchText);
    }
}
