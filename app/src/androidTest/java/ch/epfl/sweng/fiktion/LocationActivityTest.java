package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.LocationActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by dialexo on 18.10.17.
 */
@SuppressWarnings("DefaultFileTemplate")
public class LocationActivityTest {

    // setup UI automator
    static UiDevice device = UiDevice.getInstance(getInstrumentation());
    private DatabaseProvider.AddPOIListener emptyAddPoiListener =
            new DatabaseProvider.AddPOIListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onAlreadyExists() {
                }

                @Override
                public void onFailure() {
                }
            };


    @Rule
    public final ActivityTestRule<LocationActivity> mActivityRule =
            new ActivityTestRule<>(LocationActivity.class);

    @BeforeClass
    public static void setup() {
        Config.TEST_MODE = true;
    }

    @AfterClass
    public static void clean() {
        DatabaseProvider.destroyInstance();
    }

    /**
     * Tests if marker exists on map
     */
    @Test
    public void testMarkerMyLocationExists() {
        // busy wait until GPS is ready
        long t = System.currentTimeMillis();
        long end = t + 15000;
        while (System.currentTimeMillis() < end && !mActivityRule.getActivity().gmaps.hasLocation())
            ;

        // get marker when popped
        UiObject marker = device.findObject(new UiSelector().descriptionContains("My position"));
        try {
            // try to click the marker
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void nearbyMarkerTest() {
        GoogleMapsLocationProvider gmaps = mActivityRule.getActivity().gmaps;
        // busy wait until GPS is ready
        long t = System.currentTimeMillis();
        long end = t + 15000;
        while (System.currentTimeMillis() < end && !gmaps.hasLocation()) ;

        if (gmaps.hasLocation()) {
            Position myLocation = gmaps.getPosition();
            //1 lat is around 111km, radius is 50km, so p3 should not be inside radius, while
            //p1 and p2 are inside
            Position pos1 = new Position(myLocation.latitude() + 0.001, myLocation.longitude() + 0.001);
            Position pos2 = new Position(myLocation.latitude() + 0.04, myLocation.longitude() - 0.04);
            Position pos3 = new Position(myLocation.latitude() + 1, myLocation.longitude() + 1);
            //pois
            PointOfInterest p1 = new PointOfInterest("p1", pos1, new TreeSet<String>(), "", 0, "", "");
            PointOfInterest p2 = new PointOfInterest("p2", pos2, new TreeSet<String>(), "", 0, "", "");
            PointOfInterest p3 = new PointOfInterest("p3", pos3, new TreeSet<String>(), "", 0, "", "");

            //pois are put into the database
            DatabaseProvider.getInstance().addPOI(p1, emptyAddPoiListener);
            DatabaseProvider.getInstance().addPOI(p2, emptyAddPoiListener);
            DatabaseProvider.getInstance().addPOI(p3, emptyAddPoiListener);

            // get marker when popped
            UiObject marker = device.findObject(new UiSelector().descriptionContains("p1"));
            try {
                // try to click the marker
                marker.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }

        } //end of if
    }

    @Test
    public void showNearbyList() {
        onView(withId(R.id.nearbyListAcessButton)).perform(click());
    }
}
