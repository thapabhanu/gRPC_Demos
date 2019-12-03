package com.grpc.clientstream.client;

import com.grpc.client.compute.ComputeAverageResponse;
import com.grpc.client.compute.ComputeAverageRequest;
import com.grpc.client.compute.ComputeAverageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ComputeAverageClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        CountDownLatch latch = new CountDownLatch(1);

        ComputeAverageServiceGrpc.ComputeAverageServiceStub clientStub = ComputeAverageServiceGrpc.newStub(channel);

        StreamObserver<ComputeAverageRequest> request = clientStub.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                float result = value.getResult();
                System.out.println("Response: Average = "+result);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        request.onNext(ComputeAverageRequest.newBuilder().setNumberData(1).build());
        request.onNext(ComputeAverageRequest.newBuilder().setNumberData(2).build());
        request.onNext(ComputeAverageRequest.newBuilder().setNumberData(3).build());
        request.onNext(ComputeAverageRequest.newBuilder().setNumberData(4).build());

        request.onCompleted();

        try {
            latch.await(3l, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
