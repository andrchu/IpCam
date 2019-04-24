package net.kaicong.ipcam.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by LingYan on 14-12-17.
 */
public class LocationUtil {

    private static String[] getLocation(Context context) {
        String locations[] = new String[2];
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        //获得上次的记录
        if (location != null) {
            double latitudeNum = location.getLatitude();
            double longitudeNum = location.getLongitude();
            locations[0] = String.format("%.2f", latitudeNum);
            locations[1] = String.format("%.2f", longitudeNum);
            return locations;
        }
        return null;
    }

    //获取经度
    public static String getLatitude(Context context) {
        return null == getLocation(context) ? "0" : getLocation(context)[0];
    }

    //获取维度
    public static String getLongitude(Context context) {
        return null == getLocation(context) ? "0" : getLocation(context)[1];
    }

}
