package com.grpc.unary.server;

import com.gRPC.common.greeting.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {

        int number = request.getNumber();

        if (number >= 0 ) {
            double result = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder().setNumberRoot(result).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The Sqaure Root request is not positive")
                    .augmentDescription("The value is "+number).asRuntimeException());
        }
    }
}
