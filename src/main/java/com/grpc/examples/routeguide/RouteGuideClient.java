package com.grpc.examples.routeguide;

import com.grpc.examples.routeguide.Point;
import com.grpc.examples.routeguide.Rectangle;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RouteGuideClient {

    private static Logger logger = Logger.getLogger(RouteGuideClient.class.getName());

    private ManagedChannel channel;
    com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideBlockingStub blkStub;
    com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideStub asyncStub;

    public RouteGuideClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }
    public RouteGuideClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blkStub = com.grpc.examples.routeguide.RouteGuideGrpc.newBlockingStub(channel);
        asyncStub = com.grpc.examples.routeguide.RouteGuideGrpc.newStub(channel);
    }

    public void getFeatures(int lat, int lng) {
        Point point = Point.newBuilder().setLatitude(lat).setLongitude(lng).build();
        com.grpc.examples.routeguide.Feature feature = blkStub.getFeature(point);
        System.out.println("Got response from GetFeatures");
        info("Got response from GetFeatures: FeaturesName: {0}, Lat: {1},Lng:{2}", feature.getName(),
                feature.getLocation().getLatitude(), feature.getLocation().getLongitude());
    }

    public void listFeatures(int lowLat, int lowLon, int hiLat, int hiLon){
        info("*** ListFeatures: lowLat={0} lowLon={1} hiLat={2} hiLon={3}", lowLat, lowLon, hiLat,
                hiLon);
        Rectangle rectangle = Rectangle.newBuilder()
                                .setLo(Point.newBuilder().setLatitude(lowLat).setLongitude(lowLat).build())
                                .setHi(Point.newBuilder().setLatitude(hiLat).setLongitude(hiLon).build())
                                .build();
        blkStub.listFeatures(rectangle).forEachRemaining(feature -> {
            info("Got response from listFeatures: FeaturesName: {0}, Lat: {1},Lng:{2}", feature.getName(),
                    feature.getLocation().getLatitude(), feature.getLocation().getLongitude());
        });
    }

    public static void main(String[] args) {
        RouteGuideClient client = new RouteGuideClient("localhost", 50051);
        client.getFeatures(413628156, -749015468);
        client.listFeatures(413628156, -749015468, 419999544, -740371136);
    }

    private static void info(String msg, Object... params){
        logger.log(Level.INFO, msg, params);
    }
}
