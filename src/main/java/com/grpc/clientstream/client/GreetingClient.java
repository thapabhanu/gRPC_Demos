package com.grpc.clientstream.client;


import com.gRPC.common.greeting.Greeting;
import com.gRPC.common.greeting.GreetingServiceGrpc;
import com.gRPC.common.greeting.LongGreetRequest;
import com.gRPC.common.greeting.LongGreetResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        GreetingServiceGrpc.GreetingServiceStub client = GreetingServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = client.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                String result = value.getResult();
                System.out.println("Server has sent the response");
                System.out.println(result);
            }

            @Override
            public void onError(Throwable t) {
                // Error from server
            }

            @Override
            public void onCompleted() {
                // onCompleted will be call right after next()
                System.out.println("Server completed sending response");
                latch.countDown();
            }
        });

        System.out.println("Sending Message # 1");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Bhanu1").build()).build());
        System.out.println("Sending Message # 2");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Bhanu2").build()).build());
        System.out.println("Sending Message # 3");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Bhanu3").build()).build());
        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
