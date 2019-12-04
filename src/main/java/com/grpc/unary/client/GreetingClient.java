package com.grpc.unary.client;

import com.gRPC.common.greeting.*;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) throws SSLException {
        GreetingClient client = new GreetingClient();
        client.run();
    }

    private void run() throws SSLException {
        // channel using plain text - only for development
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                                    .usePlaintext()
                                    .build();

        // With server authentication SSL/TLS; custom CA root certificates; not on Android
        ManagedChannel secureChannel = NettyChannelBuilder.forAddress("localhost", 9090)
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
                .build();

        doCallGreet(channel);
        //doCallSquareRoot(channel);
        //doCallGreetWithDeadline(secureChannel);

        System.out.println("Shutting down channel...");
        channel.shutdown();
    }
    private void doCallGreet( ManagedChannel channel) {
        GreetingServiceGrpc.GreetingServiceBlockingStub client = GreetingServiceGrpc.newBlockingStub(channel);

        GreetResponse response = client.greet(GreetRequest.newBuilder().setGreeting(
                    Greeting.newBuilder().setFirstName("Bhanu").build()).build());

        System.out.println("Response -->> "+response);
    }

    private void doCallGreetWithDeadline( ManagedChannel channel) {
        GreetingServiceGrpc.GreetingServiceBlockingStub client = GreetingServiceGrpc.newBlockingStub(channel);

        try {
            System.out.println("Calling RPC service with 3000 ms dead line");
            GreetWithDeadlineResponse response = client.withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder().setFirstName("Bhanu").build()).build());
            System.out.println("Greet with deadline response = " + response);
        }catch (StatusRuntimeException e){

            if(e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline Exceed, so aborting...");
            } else {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("Calling RPC service with 100 ms dead line");
            GreetWithDeadlineResponse response = client.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder().setFirstName("Bhanu").build()).build());
            System.out.println("Greet with deadline response = " + response);
        }catch (StatusRuntimeException e){

            if(e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline Exceed, so aborting...");
            } else {
                e.printStackTrace();
            }
        }
    }
    private void doCallSquareRoot( ManagedChannel channel) {
        GreetingServiceGrpc.GreetingServiceBlockingStub client = GreetingServiceGrpc.newBlockingStub(channel);

        // Unary Service call
        SquareRootRequest request = SquareRootRequest.newBuilder().setNumber(-1).build();

        try {
            SquareRootResponse response = client.squareRoot(request);
            System.out.println("RPC Square Root response = "+ response);
        }catch (StatusRuntimeException e) {
            System.out.println("Got Exception for Square Root");
            e.printStackTrace();;
        }
    }
}
