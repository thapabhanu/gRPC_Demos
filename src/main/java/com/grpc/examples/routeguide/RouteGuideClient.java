package com.grpc.examples.routeguide;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RouteGuideClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        com.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideBlockingStub stub =
                com.grpc.examples.routeguide.RouteGuideGrpc.newBlockingStub(channel);

    }
}
