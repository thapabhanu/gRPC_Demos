package com.grpc.examples.routeguide;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.grpc.examples.routeguide.FeatureDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static java.lang.Math.*;

public class RouteGuideUtil {

    private static final double COORD_FACTOR = 1e7;

    public static double getLatitude(com.grpc.examples.routeguide.Point location) {
        return location.getLatitude()/COORD_FACTOR;
    }

    public static double getLongitude(com.grpc.examples.routeguide.Point location) {
        return location.getLongitude()/COORD_FACTOR;
    }

    public static URL getDefaultFeaturesFile() {

        return RouteGuideServer.class.getResource("route_guide_db.json");
    }

    public static List<com.grpc.examples.routeguide.Feature> parseFeatures(URL file) throws IOException {
        FeatureDatabase.Builder db = null;
        try(
                InputStream input = file.openStream();
                InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
        ) {
            db = FeatureDatabase.newBuilder();
            try {
                JsonFormat.parser().merge(reader, db);
            }catch (InvalidProtocolBufferException e) {
                System.out.println("Invalid JSON from URL");
                e.printStackTrace();
            }
        }
        return db.getFeatureList();
    }
    public static boolean exist(com.grpc.examples.routeguide.Feature feature){
        return feature!=null && !feature.getName().isEmpty();
    }

    /**
     * Calculate the distance between two points using the "haversine" formula.
     * The formula is based on http://mathforum.org/library/drmath/view/51879.html.
     *
     * @param start The starting point
     * @param end The end point
     * @return The distance between the points in meters
     */
    public static int calcDistance(com.grpc.examples.routeguide.Point start, com.grpc.examples.routeguide.Point end) {
        int r = 6371000; // earth radius in meters
        double lat1 = toRadians(RouteGuideUtil.getLatitude(start));
        double lat2 = toRadians(RouteGuideUtil.getLatitude(end));
        double lon1 = toRadians(RouteGuideUtil.getLongitude(start));
        double lon2 = toRadians(RouteGuideUtil.getLongitude(end));
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = sin(deltaLat / 2) * sin(deltaLat / 2)
                + cos(lat1) * cos(lat2) * sin(deltaLon / 2) * sin(deltaLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        return (int) (r * c);
    }
}
