package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.HomeActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.IsNot.not;

public class HomeActivityTest {

    @Rule
    public final ActivityTestRule<HomeActivity> homeActivityActivityTestRule =
            new ActivityTestRule<>(HomeActivity.class);

    private final ViewInteraction homeMainLayout = onView(withId(R.id.home_main_layout));
    private final ViewInteraction menuDrawer = onView(withId(R.id.menu_drawer));

    private static ViewAction swipeRightFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    private static ViewAction swipeLeftFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER);
    }

    private static void waitSomeTime(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void resetProviders() {
        Config.TEST_MODE = true;

        waitSomeTime(2000);
    }

    @After
    public void reset() {
        waitSomeTime(500);
    }

    @Test
    public void menuDrawerOpensAndClosesOnSwipe() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        homeMainLayout.perform(swipeLeftFast());
        menuDrawer.check(matches(not(isDisplayed())));
    }

    @Test
    public void homeToHomeWhenHomeClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(0).perform(click());
        waitSomeTime(1000);
        menuDrawer.check(matches(not(isDisplayed())));
    }

    @Test
    public void backHomeWhenHomeClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());
        closeSoftKeyboard();
        waitSomeTime(1000);
        onView(withId(R.id.add_poi_scroll)).perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(0).perform(click());
        waitSomeTime(1000);
        homeMainLayout.check(matches(isDisplayed()));
    }

    @Test
    public void showMapWhenNearbyClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(2).perform(click());
        waitSomeTime(1000);
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void CloseDrawerWhenProfileClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(3).perform(click());
        onView(withId(R.id.profileMain)).check(matches(isDisplayed()));
    }

    // Will be modified when linked
    @Test
    public void CloseDrawerWhenDiscoverClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(4).perform(click());
        waitSomeTime(1000);
        onView(withId(R.id.discover_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void AddPoiToAddPoiWhenContributeClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());
        closeSoftKeyboard();
        waitSomeTime(1000);
        onView(withId(R.id.add_poi_scroll)).perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());
        waitSomeTime(1000);
        menuDrawer.check(matches(not(isDisplayed())));
    }

    @Test
    public void displayTextSearchActivityOnMenuSearchClick() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(1).perform(click());
        closeSoftKeyboard();
        waitSomeTime(1000);
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
    }

    @Test
    public void CloseDrawerWhenSettingsClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(6).perform(click());
        onView(withId(R.id.accountSettings)).check(matches(isDisplayed()));
    }

    @Test
    public void UseGpsButtonOpenMap() {
        closeSoftKeyboard();
        onView(withId(R.id.useGPSButton)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void displayTextSearchActivityWhenSearchClicked() {
        closeSoftKeyboard();
        onView(withId(R.id.placeText)).perform(typeText("poi"));
        closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
        onView(withId(R.id.searchText)).check(matches(withText("poi")));
    }
}
