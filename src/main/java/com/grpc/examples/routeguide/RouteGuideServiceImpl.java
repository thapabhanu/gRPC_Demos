package com.grpc.examples.routeguide;

import io.grpc.stub.StreamObserver;
import com.grpc.examples.routeguide.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class RouteGuideServiceImpl extends com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideImplBase {

    private Collection<com.grpc.examples.routeguide.Feature> features;

    public RouteGuideServiceImpl(Collection<com.grpc.examples.routeguide.Feature> features) {
        this.features = features;
    }

    @Override
    public void getFeature(com.grpc.examples.routeguide.Point request, StreamObserver<com.grpc.examples.routeguide.Feature> responseObserver) {
        responseObserver.onNext(checkFeature(request));
        responseObserver.onCompleted();
    }

    /**
     * Get feature at given point
     * @param point
     * @return
     */
    private com.grpc.examples.routeguide.Feature checkFeature(com.grpc.examples.routeguide.Point point) {
        for(com.grpc.examples.routeguide.Feature feature: features){
            if(feature.getLocation().getLongitude()==point.getLongitude() &&
                feature.getLocation().getLatitude()==point.getLatitude()) {
                return feature;
            }
        }
        return com.grpc.examples.routeguide.Feature.newBuilder().setName("").setLocation(point).build();
    }


    // A server-to-client streaming RPC.
    @Override
    public void listFeatures(com.grpc.examples.routeguide.Rectangle request,
                             StreamObserver<com.grpc.examples.routeguide.Feature> responseObserver) {
        int left = min(request.getLo().getLongitude(), request.getHi().getLongitude());
        int right = max(request.getLo().getLongitude(), request.getHi().getLongitude());
        int top = max(request.getLo().getLatitude(), request.getHi().getLatitude());
        int bottom = min(request.getLo().getLatitude(), request.getHi().getLatitude());

        for (com.grpc.examples.routeguide.Feature feature : features) {
            if (!RouteGuideUtil.exist(feature)) {
                continue;
            }

            int lat = feature.getLocation().getLatitude();
            int lon = feature.getLocation().getLongitude();
            if (lon >= left && lon <= right && lat >= bottom && lat <= top) {
                responseObserver.onNext(feature);
            }
        }
        responseObserver.onCompleted();
    }

    // A client-to-server streaming RPC.
    @Override
    public StreamObserver<com.grpc.examples.routeguide.Point> routeRecords(
                        StreamObserver<com.grpc.examples.routeguide.RouteSummary> responseObserver) {

        return new StreamObserver<com.grpc.examples.routeguide.Point>() {
            int featureCount = 0;
            int distance = 0;
            com.grpc.examples.routeguide.Point previous;
            final long startTime = System.nanoTime();

            @Override
            public void onNext(com.grpc.examples.routeguide.Point value) {
                if(RouteGuideUtil.exist(checkFeature(value))) {
                    featureCount++;
                }
                if (previous!=null) {
                    distance += RouteGuideUtil.calcDistance(previous, value);
                }
                previous = value;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
                responseObserver.onNext(com.grpc.examples.routeguide.RouteSummary.newBuilder()
                        .setFeatureCount(featureCount).setDistance(distance)
                        .setElapsedTime((int)seconds)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    /**
     *
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<com.grpc.examples.routeguide.RouteNote> routeChat(
                            StreamObserver<com.grpc.examples.routeguide.RouteNote> responseObserver) {
        System.out.println("Executing server.routeChat method......");
        StreamObserver<com.grpc.examples.routeguide.RouteNote> requestObserver =
                                        new StreamObserver<com.grpc.examples.routeguide.RouteNote>() {
            @Override
            public void onNext(com.grpc.examples.routeguide.RouteNote value) {
                List<com.grpc.examples.routeguide.RouteNote> rnList = getOrCreateNote(value.getLocation());

                for(com.grpc.examples.routeguide.RouteNote prevNote: rnList.toArray(new com.grpc.examples.routeguide.RouteNote[0])) {
                    responseObserver.onNext(prevNote);
                }

                rnList.add(value);
            }

            @Override
            public void onError(Throwable t) {
                // Error condition
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    private ConcurrentMap<com.grpc.examples.routeguide.Point, List<com.grpc.examples.routeguide.RouteNote>> routesNotes =
                    new ConcurrentHashMap<com.grpc.examples.routeguide.Point, List<com.grpc.examples.routeguide.RouteNote>>();

    private List<com.grpc.examples.routeguide.RouteNote> getOrCreateNote(com.grpc.examples.routeguide.Point location) {
        List<com.grpc.examples.routeguide.RouteNote> notes = Collections.synchronizedList(new ArrayList<com.grpc.examples.routeguide.RouteNote>());
        List<com.grpc.examples.routeguide.RouteNote> prevNotes = routesNotes.putIfAbsent(location, notes);

        return prevNotes!=null? prevNotes: notes;
    }
}
