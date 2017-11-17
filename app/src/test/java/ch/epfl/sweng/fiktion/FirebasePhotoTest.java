package ch.epfl.sweng.fiktion;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;

import ch.epfl.sweng.fiktion.providers.FirebasePhotoProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pedro on 17/11/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FirebasePhotoTest {

    @Mock
    StorageReference stRef;

    @Mock
    DatabaseReference dbRef;

    private FirebasePhotoProvider photoProvider;

    private OnFailureListener failureListener;

    private OnSuccessListener successListener;

    private OnProgressListener progressListener;

    private void setFailureListener(OnFailureListener failureListener) {
        this.failureListener = failureListener;
    }

    private void setSuccessListener(OnSuccessListener successListener) {
        this.successListener = successListener;
    }

    private void setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    enum Result {
        SUCCESS, FAILURE, NOTHING
    }

    private Result result;

    private void setResult(Result result) {
        this.result = result;
    }

    private double progress = 0.0;

    private void setProgress(double progress) {
        this.progress = progress;
    }

    @Before
    public void setup() {
        photoProvider = new FirebasePhotoProvider(stRef, dbRef);
        result = Result.NOTHING;
    }

    @Test
    public void uploadTest() {
        when(stRef.child(anyString())).thenReturn(stRef);
        UploadTask uploadTask = mock(UploadTask.class);
        when(stRef.putBytes(any(byte[].class))).thenReturn(uploadTask);
        final StorageTask<UploadTask.TaskSnapshot> storageTask = mock(StorageTask.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setFailureListener((OnFailureListener) invocation.getArgument(0));
                return storageTask;
            }
        }).when(uploadTask).addOnFailureListener(any(OnFailureListener.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setSuccessListener((OnSuccessListener) invocation.getArgument(0));
                return storageTask;
            }
        }).when(storageTask).addOnSuccessListener(any(OnSuccessListener.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setProgressListener((OnProgressListener) invocation.getArgument(0));
                return storageTask;
            }
        }).when(storageTask).addOnProgressListener(any(OnProgressListener.class));
        when(dbRef.child(anyString())).thenReturn(dbRef);
        when(dbRef.setValue(anyBoolean())).thenReturn(null);

        Bitmap bitmap = mock(Bitmap.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7};
                ((OutputStream) invocation.getArgument(2)).write(bytes);
                return null;
            }
        }).when(bitmap).compress(any(Bitmap.CompressFormat.class), anyInt(), any(OutputStream.class));

        PhotoProvider.UploadPhotoListener listener = new PhotoProvider.UploadPhotoListener() {
            @Override
            public void onSuccess() {
                setResult(Result.SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(Result.FAILURE);
            }

            @Override
            public void updateProgress(double progress) {
                setProgress(progress);
            }
        };

        photoProvider.uploadPOIBitmap(bitmap, "poiTest", listener);
        UploadTask.TaskSnapshot snapshot = mock(UploadTask.TaskSnapshot.class);
        failureListener.onFailure(null);
        assertThat(result, is(Result.FAILURE));
        successListener.onSuccess(snapshot);
        assertThat(result, is(Result.SUCCESS));
        when(snapshot.getBytesTransferred()).thenReturn((long) 2);
        when(snapshot.getTotalByteCount()).thenReturn((long) 4);
        progressListener.onProgress(snapshot);
        assertThat(progress, is(50.0));
        when(snapshot.getTotalByteCount()).thenReturn((long) 0);
        progressListener.onProgress(snapshot);
        assertThat(progress, is(100.0));
    }
}
