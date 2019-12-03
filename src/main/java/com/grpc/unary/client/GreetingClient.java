package com.grpc.unary.client;

import com.gRPC.common.greeting.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;

public class GreetingClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        GreetingServiceGrpc.GreetingServiceBlockingStub client = GreetingServiceGrpc.newBlockingStub(channel);

        // Unary Service call
        SquareRootRequest request = SquareRootRequest.newBuilder().setNumber(-1).build();

        try {
            SquareRootResponse response = client.squareRoot(request);
            System.out.println(response);
        }catch (StatusRuntimeException e) {
            System.out.println("Got Exception for Square Root");
            e.printStackTrace();;
        }

    }
}
