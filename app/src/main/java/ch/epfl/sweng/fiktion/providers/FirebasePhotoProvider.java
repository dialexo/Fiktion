package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ch.epfl.sweng.fiktion.utils.CollectionsUtils;

/**
 * Firebase photo provider
 *
 * @author pedro
 */
public class FirebasePhotoProvider extends PhotoProvider {
    private StorageReference stRef;
    private DatabaseReference dbRef;
    final private long MAXIMUM_SIZE = 10 * 1024 * 1024; // 10MB

    public FirebasePhotoProvider() {
        stRef = FirebaseStorage.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public FirebasePhotoProvider(StorageReference stRef, DatabaseReference dbRef) {
        this.stRef = stRef;
        this.dbRef = dbRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadPOIBitmap(Bitmap bitmap, final String poiName, final UploadPhotoListener listener) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // fill the outputStream with the bitmap data and convert it into a byte array
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        // if the number of bytes exceeds MAXIMUM_SIZE, abort the upload
        if (data.length > MAXIMUM_SIZE) {
            listener.onFailure();
            return;
        }

        // create a hash of the data, it will be the name of the photo
        String photoName = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            photoName = CollectionsUtils.bytesToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            listener.onFailure();
            return;
        }

        // get the photo reference which is /Points of interest/#poiName/#photoName
        StorageReference poisRef = stRef.child("Points of interest");
        StorageReference poiRef = poisRef.child(poiName);
        final StorageReference photoRef = poiRef.child(photoName + ".jpg");

        // create an uploadTask which takes care of the upload
        UploadTask uploadTask = photoRef.putBytes(data);

        // add listeners to uploadTask to keep track of the status of the upload
        final String finalPhotoName = photoName;
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // inform the listener that the upload failed
                listener.onFailure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // store the photo name in the database so that we can retrieve it
                final DatabaseReference dbPOIRef = dbRef.child("Photo references").child(poiName);
                dbPOIRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String index = String.valueOf(dataSnapshot.getChildrenCount());
                        dbPOIRef.child(index).setValue(finalPhotoName);

                        // inform the listener that the upload succeeded
                        listener.onSuccess();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // if the photo reference addition to the database fails, delete the photo
                        // from the database
                        photoRef.delete();
                        listener.onFailure();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long totalBytes = taskSnapshot.getTotalByteCount();
                long bytesTransfered = taskSnapshot.getBytesTransferred();

                // inform the listener that the progress has been updated
                if (totalBytes == 0) {
                    // if there is no bytes then the upload is "done"
                    listener.updateProgress(100.0);
                } else {
                    double progress = (100.0 * bytesTransfered) / taskSnapshot.getTotalByteCount();
                    listener.updateProgress(progress);
                }

            }
        });
    }

    @Override
    public void getPOIPhotoNames(String poiName, int numberOfPhotos, final GetPhotoNamesListener listener) {
        // first, get the reference of the poi and listen for its photo references
        Query query = dbRef.child("Photo references").child(poiName).orderByKey();

        if (numberOfPhotos > 0)
            // limit the number of photo references
            query = query.limitToFirst(numberOfPhotos);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // For every photo name, download the associated photo from firebase
                String photoRef = dataSnapshot.getValue() + ".jpg";
                listener.onNewValue(photoRef);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downloadPOIBitmap(final String poiName, String photoName, final DownloadBitmapListener listener) {

        StorageReference photoRef = stRef.child("Points of interest").child(poiName).child(photoName);
        photoRef.getBytes(MAXIMUM_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // convert the bytes into a bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    // "send" the downloaded bitmap to the listener
                    listener.onNewValue(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure();
            }
        });
    }
}
