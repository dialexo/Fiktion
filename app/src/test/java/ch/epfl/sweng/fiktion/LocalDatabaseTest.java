package ch.epfl.sweng.fiktion;

import org.junit.After;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 24/11/17.
 */

public class LocalDatabaseTest {

    private LocalDatabaseProvider db = new LocalDatabaseProvider();

    private PointOfInterest poiWithName(String name) {
        return new PointOfInterest(name, new Position(0, 0),
                new TreeSet<String>(), "", 0, "", "");
    }

    private PointOfInterest poi = poiWithName("poi");
    private PointOfInterest poi2 = poiWithName("poi2");
    private User user = new User("user", "user");
    private User user2 = new User("user2", "user2");

    private DatabaseProvider.AddPOIListener emptyAddPOIListener = new DatabaseProvider.AddPOIListener() {
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
    private DatabaseProvider.AddUserListener emptyAddUserListener = new DatabaseProvider.AddUserListener() {
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

    @After
    public void empty() {
        db = new LocalDatabaseProvider();
    }

    @Test
    public void addPOITest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.AddPOIListener listener = new DatabaseProvider.AddPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onAlreadyExists() {
                result.set("A");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPOI(poi, listener);
        assertThat(result.get(), is("S"));
        db.addPOI(poi, listener);
        assertThat(result.get(), is("A"));
        PointOfInterest s = poiWithName("ADDPOIS");
        PointOfInterest a = poiWithName("ADDPOIA");
        PointOfInterest f = poiWithName("ADDPOIF");
        db.addPOI(s, listener);
        assertThat(result.get(), is("S"));
        db.addPOI(a, listener);
        assertThat(result.get(), is("A"));
        db.addPOI(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void getPOITest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.GetPOIListener listener = new DatabaseProvider.GetPOIListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                result.set("S");
            }

            @Override
            public void onModifiedValue(PointOfInterest poi) {
                result.set("M");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };
        db.getPOI("poi", listener);
        assertThat(result.get(), is("D"));

        db.addPOI(poi, emptyAddPOIListener);
        assertThat(result.get(), is("S"));

        db.getPOI("poi2", listener);
        assertThat(result.get(), is("D"));

        String s = "GETPOIS";
        String m = "GETPOIM";
        String d = "GETPOID";
        String f = "GETPOIF";
        db.getPOI(s, listener);
        assertThat(result.get(), is("S"));
        db.getPOI(m, listener);
        assertThat(result.get(), is("M"));
        db.getPOI(d, listener);
        assertThat(result.get(), is("D"));
        db.getPOI(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void modifyPOITest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPOI(poi, emptyAddPOIListener);
        db.modifyPOI(poi, listener);
        assertThat(result.get(), is("S"));
        db.modifyPOI(poi2, listener);
        assertThat(result.get(), is("D"));
        PointOfInterest s = poiWithName("MODIFYPOIS");
        PointOfInterest d = poiWithName("MODIFYPOID");
        PointOfInterest f = poiWithName("MODIFYPOIF");
        db.modifyPOI(s, listener);
        assertThat(result.get(), is("S"));
        db.modifyPOI(d, listener);
        assertThat(result.get(), is("D"));
        db.modifyPOI(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void upvoteTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPOI(poi, emptyAddPOIListener);
        db.upvote("poi", listener);
        assertThat(result.get(), is("S"));
        db.upvote("poi2", listener);
        assertThat(result.get(), is("D"));
        String s = "UPVOTES";
        String d = "UPVOTED";
        String f = "UPVOTEF";
        db.upvote(s, listener);
        assertThat(result.get(), is("S"));
        db.upvote(d, listener);
        assertThat(result.get(), is("D"));
        db.upvote(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void downvoteTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPOI(poi, emptyAddPOIListener);
        db.downvote("poi", listener);
        assertThat(result.get(), is("S"));
        db.downvote("poi2", listener);
        assertThat(result.get(), is("D"));
        String s = "DOWNVOTES";
        String d = "DOWNVOTED";
        String f = "DOWNVOTEF";
        db.downvote(s, listener);
        assertThat(result.get(), is("S"));
        db.downvote(d, listener);
        assertThat(result.get(), is("D"));
        db.downvote(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void findNearPOIsTest() {
        final Mutable<String> result = new Mutable<>("good");
        final Mutable<Integer> count = new Mutable<>(0);

        DatabaseProvider.GetMultiplePOIsListener listener = new DatabaseProvider.GetMultiplePOIsListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                count.set(count.get() + 1);
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPOI(poi, emptyAddPOIListener);
        PointOfInterest farPOI = new PointOfInterest("farPOI", new Position(10, 10),
                new TreeSet<String>(), "", 0, "", "");
        db.addPOI(farPOI, emptyAddPOIListener);
        db.findNearPOIs(new Position(1, 1), 200, listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));
        db.findNearPOIs(new Position(1000, 1000), 30, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void searchByTextTest() {
        final Mutable<String> result = new Mutable<>("good");
        final Mutable<Integer> count = new Mutable<>(0);
        DatabaseProvider.GetMultiplePOIsListener listener = new DatabaseProvider.GetMultiplePOIsListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                count.set(count.get() + 1);
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPOI(poi, emptyAddPOIListener);
        db.addPOI(poiWithName("iop"), emptyAddPOIListener);
        PointOfInterest countryPOI = new PointOfInterest("oip", new Position(0, 0),
                new TreeSet<String>(), "", 0, "poi", "");
        db.addPOI(countryPOI, emptyAddPOIListener);
        db.searchByText("poi", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(2));

        count.set(0);
        db.searchByText("SEARCHN", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));

        count.set(0);
        db.searchByText("SEARCHF", listener);
        assertThat(result.get(), is("F"));
        assertThat(count.get(), is(0));
    }

    @Test
    public void addUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.AddUserListener listener = new DatabaseProvider.AddUserListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onAlreadyExists() {
                result.set("A");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addUser(user, listener);
        assertThat(result.get(), is("S"));
        db.addUser(user, listener);
        assertThat(result.get(), is("A"));
        User s = new User("ADDUSERS", "ADDUSERS");
        User a = new User("ADDUSERA", "ADDUSERA");
        User f = new User("ADDUSERF", "ADDUSERF");
        db.addUser(s, listener);
        assertThat(result.get(), is("S"));
        db.addUser(a, listener);
        assertThat(result.get(), is("A"));
        db.addUser(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void getUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.GetUserListener listener = new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User u) {
                result.set("S");
            }

            @Override
            public void onModifiedValue(User user) {

            }


            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };
        db.getUserById("user", listener);
        assertThat(result.get(), is("D"));

        db.addUser(user, emptyAddUserListener);

        db.getUserById("user", listener);
        assertThat(result.get(), is("S"));
        db.getUserById("user2", listener);
        assertThat(result.get(), is("D"));
        String s = "GETUSERS";
        String d = "GETUSERD";
        String f = "GETUSERF";
        db.getUserById(s, listener);
        assertThat(result.get(), is("S"));
        db.getUserById(d, listener);
        assertThat(result.get(), is("D"));
        db.getUserById(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void deleteUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.DeleteUserListener listener = new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };
        db.addUser(user, emptyAddUserListener);

        db.deleterUserById("user", listener);
        assertThat(result.get(), is("S"));

        db.deleterUserById("user", listener);
        assertThat(result.get(), is("D"));

        String s = "DELETEUSERS";
        String d = "DELETEUSERD";
        String f = "DELETEUSERF";
        db.deleterUserById(s, listener);
        assertThat(result.get(), is("S"));
        db.deleterUserById(d, listener);
        assertThat(result.get(), is("D"));
        db.deleterUserById(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void modifyUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyUserListener listener = new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addUser(user, emptyAddUserListener);

        db.modifyUser(user, listener);
        assertThat(result.get(), is("S"));
        db.modifyUser(user2, listener);
        assertThat(result.get(), is("D"));
        db.modifyUser(new User("DELETEUSERF", "DELETEUSERF"), listener);
        assertThat(result.get(), is("F"));

        User s = new User("MODIFYUSERS", "MODIFYUSERS");
        User d = new User("MODIFYUSERD", "MODIFYUSERD");
        User f = new User("MODIFYUSERF", "MODIFYUSERF");
        db.modifyUser(s, listener);
        assertThat(result.get(), is("S"));
        db.modifyUser(d, listener);
        assertThat(result.get(), is("D"));
        db.modifyUser(f, listener);
        assertThat(result.get(), is("F"));
    }

    private Comment commentWithText(String text) throws NoSuchAlgorithmException {
        return new Comment(text, "author", new Date(0), 0);
    }

    @Test
    public void addCommentTest() throws NoSuchAlgorithmException {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.AddCommentListener listener = new DatabaseProvider.AddCommentListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addComment(commentWithText("text1"), "poi1", listener);
        assertThat(result.get(), is("S"));
        result.set("");

        db.addComment(commentWithText("text2"), "poi1", listener);
        assertThat(result.get(), is("S"));
        result.set("");

        db.addComment(commentWithText("text3"), "ADDCOMMENTS", listener);
        assertThat(result.get(), is("S"));

        db.addComment(commentWithText("text4"), "ADDCOMMENTF", listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void getCommentsTest() throws NoSuchAlgorithmException {
        DatabaseProvider.AddCommentListener emptyAddCommentListener = new DatabaseProvider.AddCommentListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        };
        final Mutable<String> result = new Mutable<>("good");
        final Mutable<Integer> count = new Mutable<>(0);

        DatabaseProvider.GetCommentsListener listener = new DatabaseProvider.GetCommentsListener() {
            @Override
            public void onNewValue(Comment comment) {
                count.set(count.get() + 1);
            }

            @Override
            public void onModifiedValue(Comment comment) {

            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.getPOIComments("poi1", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(0));

        db.addComment(commentWithText("text"), "poi1", emptyAddCommentListener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));
        count.set(0);

        db.addComment(commentWithText("text"), "poi2", emptyAddCommentListener);
        db.getPOIComments("poi2", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));
        count.set(0);

        db.getPOIComments("GETCOMMENTN", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));

        db.getPOIComments("GETCOMMENTF", listener);
        assertThat(result.get(), is("F"));
        assertThat(count.get(), is(1));
    }
}
