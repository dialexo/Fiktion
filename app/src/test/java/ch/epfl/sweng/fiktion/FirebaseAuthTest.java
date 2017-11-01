
package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseAuthProvider;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, FirebaseAuth.class, GeoFire.class, FirebaseAuthProvider.class})
public class FirebaseAuthTest {

    private FirebaseAuthProvider auth;
    private String email = "test@epfl.ch";
    private String password = "testing";
    @Mock
    FirebaseAuth fbAuth;
    @Mock
    FirebaseDatabase fbDatabase;
    @Mock
    DatabaseReference dbRef;
    @Mock
    FirebaseUser fbUser;
    @Mock
    GeoFire geofire;
    @Mock
    Task<AuthResult> taskAuthSucceedResult;
    @Mock
    Task<AuthResult> taskAuthFailResult;
    @Mock
    Task<Void> taskVoidSucceedResult;
    @Mock
    Task<Void> taskVoidFailResult;
    @Mock
    AuthResult result;
    @Mock
    OnCompleteListener<AuthResult> onCompleteAuthListener;
    @Mock
    UserProfileChangeRequest.Builder updateProfileBuilder;
    @Mock
    UserProfileChangeRequest updateProfile;
    @Mock
    OnCompleteListener<Void> onCompleteVoidListener;
    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> testOnCompleteAuthListener;
    @Captor
    private ArgumentCaptor<OnCompleteListener<Void>> testOnCompleteVoidListener;


    @Before
    public void setUp() throws Exception {

        mockStatic(FirebaseAuth.class);
        mockStatic(FirebaseDatabase.class);

        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(fbDatabase);
        Mockito.when(fbDatabase.getReference()).thenReturn(dbRef);
        whenNew(GeoFire.class).withAnyArguments().thenReturn(geofire);
        Mockito.when(FirebaseAuth.getInstance()).thenReturn(fbAuth);
        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(fbDatabase);

        setTasks();

        auth = new FirebaseAuthProvider();

    }

    private void setTasks() {
        Mockito.when(taskAuthSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthSucceedResult);
        Mockito.when(taskAuthFailResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthFailResult.isSuccessful()).thenReturn(false);
        Mockito.when(taskAuthFailResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthFailResult);
        Mockito.when(taskVoidSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskVoidSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskVoidSucceedResult.addOnCompleteListener(testOnCompleteVoidListener.capture())).
                thenReturn(taskVoidSucceedResult);
        Mockito.when(taskVoidFailResult.isComplete()).thenReturn(true);
        Mockito.when(taskVoidFailResult.isSuccessful()).thenReturn(false);
        Mockito.when(taskVoidFailResult.addOnCompleteListener(testOnCompleteVoidListener.capture())).
                thenReturn(taskVoidFailResult);
    }
/*
    @Test
    public void testSuccessfulDeleteAccount() {
        //test successful
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.delete()).thenReturn(taskVoidSucceedResult);
        Mockito.when()
        auth.deleteAccount(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                //successfully deleted in firebase
                Mockito.verify(fbUser.delete());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        }, new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                //successfully deleted in the database
                Mockito.verify(fbUser.delete());
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }
*/
    @Test
    public void testFailDeleteAccount() {
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.delete()).thenReturn(taskVoidFailResult);
        auth.deleteAccount(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.delete());
            }
        }, new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Mockito.verify(fbUser.delete());
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.delete());
            }
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    @Test
    public void testNotLoggedInDeleteAccount() {
        //test successful
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);

        auth.deleteAccount(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                //success
            }
        }, new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Test
    public void succeedSendPasswordResetEmail() {
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbAuth.sendPasswordResetEmail(email)).thenReturn(taskVoidSucceedResult);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.sendPasswordResetEmail(email));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }

    @Test
    public void failSendPasswordResetEmail() {
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbAuth.sendPasswordResetEmail(email)).thenReturn(taskVoidFailResult);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbAuth.sendPasswordResetEmail(email));
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }




    @Test
    public void SuccedUpdateEmail() {
        final String newEmail = "new@email.ch";
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.updateEmail(newEmail)).thenReturn(taskVoidSucceedResult);

        auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbUser.updateEmail(newEmail));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }

    @Test
    public void failUpdateEmail() {
        final String newEmail = "new@email.ch";
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.updateEmail(newEmail)).thenReturn(taskVoidFailResult);

        auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.updateEmail(newEmail));
            }
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    @Test
    public void succedSendEmailVerification() {

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.sendEmailVerification()).thenReturn(taskVoidSucceedResult);
        auth.sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbUser.sendEmailVerification());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }

    @Test
    public void failSendEmailVerification() {

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.sendEmailVerification()).thenReturn(taskVoidFailResult);
        auth.sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.sendEmailVerification());
            }


        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    /*
    @Test
    public void succeedCreateUser() {
        Mockito.when(fbAuth.createUserWithEmailAndPassword(email, password)).thenReturn(taskAuthSucceedResult);
        auth.createUserWithEmailAndPassword(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.createUserWithEmailAndPassword(email, password));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthSucceedResult);
    }
*/
    @Test
    public void failCreateUser() {
        Mockito.when(fbAuth.createUserWithEmailAndPassword(email, password)).thenReturn(taskAuthFailResult);
        auth.createUserWithEmailAndPassword(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbAuth.createUserWithEmailAndPassword(email, password));
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthFailResult);
    }

    @Test
    public void succeedSignIn() {
        setTasks();
        Mockito.when(fbAuth.signInWithEmailAndPassword(email, password)).thenReturn(taskAuthSucceedResult);

        auth.signIn(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.signInWithEmailAndPassword(email, password));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthSucceedResult);
    }

    @Test
    public void testAuthSignOut() {
        Mockito.doNothing().when(fbAuth).signOut();
        auth.signOut();
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);
        auth.getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

  /*  @Test
    public void getCurrentUser() {
        final String name = "default";
        final String email = "test@test.ch";
        final String id = "id";

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getDisplayName()).thenReturn(name);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbUser.getUid()).thenReturn(id);
        Mockito.when(fbUser.isEmailVerified()).thenReturn(false);


        auth.getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                Assert.assertEquals(auth.getEmail(), email);
                Assert.assertEquals(user.getName(), name);

            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });
    }
*/

}
