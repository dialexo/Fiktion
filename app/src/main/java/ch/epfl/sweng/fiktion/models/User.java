package ch.epfl.sweng.fiktion.models;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * This class represents the User in the application
 *
 * @author Rodrigo
 */

public class User {
    private String name;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private Set<String> favourites;
    //private Set<String> wishlist;
    //private Set<String> rated;

    /**
     * Creates a new User with given paramaters
     *
     * @param input_name Username
     * @param input_id   User id
     */
    public User(String input_name, String input_id, Set<String> favs) {
        name = input_name;
        id = input_id;
        favourites = favs;
    }

    /**
     * Adds new favorite point of interest to this user's favorite list
     *
     * @param favID POI ID
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void addFavourite(final DatabaseProvider db,final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.add(favID)) {
            db.modifyUser(new User(name, id, favourites), new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    favourites.remove(favID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    favourites.remove(favID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Removes given point of interest of this user favorite list
     *
     * @param favID POI ID
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void removeFavourite(final DatabaseProvider database, final String favID, final AuthProvider.AuthListener listener) {
        if (favourites.remove(favID)) {
            database.modifyUser(new User(name, id, favourites), new DatabaseProvider.ModifyUserListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onDoesntExist() {
                    favourites.add(favID);
                    listener.onFailure();
                }

                @Override
                public void onFailure() {
                    favourites.add(favID);
                    listener.onFailure();
                }
            });
        } else {
            listener.onFailure();
        }
    }

    /**
     * Changes this user's username
     *
     * @param newName  New username value
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void changeName(DatabaseProvider database, final String newName, final AuthProvider.AuthListener listener) {
        //verification is done in the activity
        database.modifyUser(new User(newName, id, favourites), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                name = newName;
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                listener.onFailure();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });

    }


    @Override
    public boolean equals(Object other) {
        if (other == null || !User.class.isAssignableFrom(other.getClass())) {
            return false;
        }

        User otherUser = (User) other;

        return this.name.equals(otherUser.name)
                && this.id.equals(otherUser.id);
    }

    /**
     * @return the user display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return user ID
     */
    public String getID() {
        return id;
    }

    /**
     * @return the user set with his favourite POI's IDs
     */
    public Set<String> getFavourites() {
        return Collections.unmodifiableSet(new TreeSet<>(favourites));
    }


}
