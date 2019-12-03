package com.grpc.clientstream.server;

import com.grpc.client.compute.ComputeAverageRequest;
import com.grpc.client.compute.ComputeAverageResponse;
import com.grpc.client.compute.ComputeAverageServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ComputeAverageServerImpl extends ComputeAverageServiceGrpc.ComputeAverageServiceImplBase {

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {

        StreamObserver<ComputeAverageRequest> requestObserver = new StreamObserver<ComputeAverageRequest>() {
            float result = 0;
            int count = 0;
            @Override
            public void onNext(ComputeAverageRequest value) {
                int numData = value.getNumberData();
                result += numData;
                count++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                result = result / count;

                responseObserver.onNext(ComputeAverageResponse.newBuilder().setResult(result).build());

                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
