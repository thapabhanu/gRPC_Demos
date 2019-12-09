package com.grpc.examples.routeguide;

import io.grpc.stub.StreamObserver;
import com.grpc.examples.routeguide.*;
import java.util.Collection;

public class RouteGuideServiceImpl extends com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideImplBase {

    private Collection<com.grpc.examples.routeguide.Feature> features;

    public RouteGuideServiceImpl(Collection<com.grpc.examples.routeguide.Feature> features) {
        this.features = features;
    }

    @Override
    public void getFeature(com.grpc.examples.routeguide.Point request, StreamObserver<com.grpc.examples.routeguide.Feature> responseObserver) {

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

    @Override
    public void listFeatures(com.grpc.examples.routeguide.Rectangle request, StreamObserver<com.grpc.examples.routeguide.Feature> responseObserver) {

    }
}
