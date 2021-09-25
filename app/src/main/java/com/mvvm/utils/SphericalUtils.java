package com.mvvm.utils;

/**
 * Created by deepak on 15/2/17.
 */

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class containing function related to location based calculation
 */
public class SphericalUtils {
    private static final double EARTH_RADIUS = 6371009;

    private static double hav(double x) {
        double sinHalf = sin(x * 0.5);
        return sinHalf * sinHalf;
    }

    private static double arcHav(double x) {
        return 2 * asin(sqrt(x));
    }

    private static double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
    }

    public static LatLng computeOffset(LatLng from, double distance, double heading) {
        distance /= EARTH_RADIUS;
        heading = toRadians(heading);
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng));
    }

    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    private static double computeAngleBetween(LatLng from, LatLng to) {
        return distanceRadians(toRadians(from.latitude), toRadians(from.longitude),
                toRadians(to.latitude), toRadians(to.longitude));
    }

    public static double computeDistanceBetween(LatLng from, LatLng to) {
        return computeAngleBetween(from, to) * EARTH_RADIUS;
    }


//    public static double distance(Location loca, Location locb) {
//        try {
//            Location selected_location = new Location(loca);
//            selected_location.setLatitude(loca.getLatitude());
//            selected_location.setLongitude(loca.getLongitude());
//            Location near_locations = new Location(locb);
//            near_locations.setLatitude(loca.getLatitude());
//            near_locations.setLongitude(loca.getLongitude());
//            return selected_location.distanceTo(near_locations);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0.0f;
//    }


}