package com.grpc.examples.routeguide;

import io.grpc.stub.StreamObserver;
import com.grpc.examples.routeguide.*;
import java.util.Collection;

import static java.lang.Math.max;
import static java.lang.Math.min;

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
}
