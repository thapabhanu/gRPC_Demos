package com.grpc.examples.routeguide;

import com.grpc.examples.routeguide.Point;
import com.grpc.examples.routeguide.Rectangle;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RouteGuideClient {

    private static Logger logger = Logger.getLogger(RouteGuideClient.class.getName());

    private ManagedChannel channel;
    com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideBlockingStub blkStub;
    com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideStub asyncStub;

    private Random random = new Random();

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
    public void recordRoute(List<com.grpc.examples.routeguide.Feature> featureList, int numPoints){

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver requestObserver = asyncStub.routeRecords(new StreamObserver<com.grpc.examples.routeguide.RouteSummary>() {
            @Override
            public void onNext(com.grpc.examples.routeguide.RouteSummary value) {
                System.out.println("Response"+ value.toString());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        for(int i=0; i<numPoints; i++) {
            int index = random.nextInt(featureList.size());
            Point point = featureList.get(index).getLocation();
            requestObserver.onNext(point);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        requestObserver.onCompleted();

        try {
            countDownLatch.await(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void routeChat(){

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<com.grpc.examples.routeguide.RouteNote> reqObserver =
                asyncStub.routeChat(new StreamObserver<com.grpc.examples.routeguide.RouteNote>() {
            @Override
            public void onNext(com.grpc.examples.routeguide.RouteNote value) {
                System.out.println("RounteChat Response");
                System.out.println(value.getMessage());
                System.out.println(value.getLocation().toString());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        com.grpc.examples.routeguide.RouteNote[] notes =   {newNote("First message", 0, 0), newNote("Second message", 0, 1),
                newNote("Third message", 1, 0), newNote("Fourth message", 1, 1)};

        for(com.grpc.examples.routeguide.RouteNote note: notes) {
            reqObserver.onNext(note);
        }
        //reqObserver.onNext();
        reqObserver.onCompleted();

        try {
            latch.await(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private com.grpc.examples.routeguide.RouteNote newNote(String msg, int x, int y) {
        return com.grpc.examples.routeguide.RouteNote.newBuilder().setMessage(msg)
                .setLocation(Point.newBuilder().setLatitude(x).setLongitude(y)).build();
    }

    public static void main(String[] args) {

        List<com.grpc.examples.routeguide.Feature> features;
        try {
            features = RouteGuideUtil.parseFeatures(RouteGuideUtil.getDefaultFeaturesFile());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        RouteGuideClient client = new RouteGuideClient("localhost", 50051);
        client.getFeatures(413628156, -749015468);
        //client.listFeatures(413628156, -749015468, 419999544, -740371136);
        client.recordRoute(features, 10);
        client.routeChat();
    }

    private static void info(String msg, Object... params){
        logger.log(Level.INFO, msg, params);
    }
}
