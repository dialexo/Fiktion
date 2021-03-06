package ch.epfl.sweng.fiktion.providers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidServices;

/**
 * Provider of device's last know location using FusedLocationProviderCLient
 * Created by Rodrigo on 11.12.2017.
 */

public class FusedLocationProvider extends CurrentLocationProvider {
    private FusedLocationProviderClient mFuseProvider;

    public FusedLocationProvider(Activity ctx) {
        mFuseProvider = LocationServices.getFusedLocationProviderClient(ctx);
    }

    /**
     * Constructor with a mocked instance
     *
     * @param mockProv mocked instance
     */
    public FusedLocationProvider(FusedLocationProviderClient mockProv) {
        mFuseProvider = mockProv;
    }

    public void getLastLocation(Activity ctx, final OnSuccessListener<Location> listener, final AndroidServices.OnGPSDisabledCallback gpsDisabledCallback) {
        // check permissions
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            AndroidPermissions.promptLocationPermission(ctx);
        } else {
            // check location enable and ask otherwise
            AndroidServices.checkLocationEnable(ctx, gpsDisabledCallback);
            mFuseProvider.getLastLocation().addOnSuccessListener(ctx, listener);
        }
    }

}
