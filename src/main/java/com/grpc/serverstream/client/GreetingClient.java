package com.grpc.serverstream.client;

import com.gRPC.common.greeting.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        GreetingServiceGrpc.GreetingServiceBlockingStub client = GreetingServiceGrpc.newBlockingStub(channel);

        // Unary Service call
        /*Greeting greeting = Greeting.newBuilder().setFirstName("Bhanu").build();
        GreetRequest request = GreetRequest.newBuilder().setGreeting(greeting).build();
        GreetResponse response = client.greet(request);*/

        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder().setGreeting(
                                                Greeting.newBuilder().setFirstName("Bhanu").build()
                                            ).build();
        client.greetManyTimes(request).forEachRemaining(response ->{
            System.out.println(response.getResult());
        });
    }
}
