package com.grpc.bidirectional.client;

import com.gRPC.common.greeting.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        CountDownLatch latch = new CountDownLatch(1);

        GreetingServiceGrpc.GreetingServiceStub clientStub = GreetingServiceGrpc.newStub(channel);

        StreamObserver<ManyTimesGreetRequest> req = clientStub.manyTimesGreet(new StreamObserver<ManyTimesGreetResponse>() {
            @Override
            public void onNext(ManyTimesGreetResponse value) {
                String result = value.getResult();
                System.out.println("Result - "+result);
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList("bhanu", "prachi", "asha").forEach( name -> {
            System.out.println("Sending Name - "+name);
            req.onNext(ManyTimesGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName(name).build()).build());

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        req.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
