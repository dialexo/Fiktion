package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by pedro on 16/11/17.
 */

public class LocalPhotoProvider extends PhotoProvider {
    private Map<String, List<Bitmap>> bitmaps = new TreeMap<>();

    @Override
    public void uploadPOIBitmap(Bitmap bitmap, String poiName, UploadPhotoListener listener) {
        if (bitmaps.containsKey(poiName)) {
            List<Bitmap> poiBitmaps = bitmaps.get(poiName);
            poiBitmaps.add(bitmap);
        } else {
            List<Bitmap> poiBitmaps = new ArrayList<>();
            poiBitmaps.add(bitmap);
            bitmaps.put(poiName, poiBitmaps);
        }
        listener.onSuccess();
    }

    @Override
    public void downloadPOIBitmaps(String poiName, int numberOfBitmaps, DownloadBitmapListener listener) {
        List<Bitmap> poiBitmaps = bitmaps.getOrDefault(poiName, new ArrayList<Bitmap>());
        for (Bitmap b: poiBitmaps) {
            if (poiBitmaps.size() <= numberOfBitmaps) listener.onNewPhoto(b);
        }
    }
}
