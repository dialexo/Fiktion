package ch.epfl.sweng.fiktion.providers;

import android.location.Location;

import ch.epfl.sweng.fiktion.models.Position;

/**
 * A general map and location provider
 * Created by dialexo on 17.10.17.
 */

public abstract class LocationProvider {

    // current location
    protected Location location;

    /**
     * Returns the current location in an android.location.Location object
     *
     * @return Location : current android location
     */
    public Location getLocation() {
        return location == null ? null : new Location(this.location);
    }

    /**
     * Returns the current position in a Position object
     *
     * @return Position : current position
     */
    public Position getPosition() {
        return location == null ? null : new Position(this.location.getLatitude(), this.location.getLongitude());
    }
}
