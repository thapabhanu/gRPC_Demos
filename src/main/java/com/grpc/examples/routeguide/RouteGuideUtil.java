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

public class RouteGuideUtil {

    private static final double COORD_FACTOR = 1e7;

    public static double getLatitude(com.grpc.examples.routeguide.Point location) {
        return location.getLatitude()/COORD_FACTOR;
    }

    public static double getLogitude(com.grpc.examples.routeguide.Point location) {
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

}
