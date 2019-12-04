package com.grpc.unary.server;

import com.gRPC.common.greeting.*;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        System.out.println("Server Greet Method...");
        String result = "Hello "+ request.getGreeting().getFirstName();
        responseObserver.onNext(GreetResponse.newBuilder().setResult(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {

        int number = request.getNumber();

        if (number >= 0 ) {
            double result = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder().setNumberRoot(result).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The Square Root request is not positive")
                    .augmentDescription("The value is "+number).asRuntimeException());
        }
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {
        Context context = Context.current();

        try {
            for (int i = 0; i < 3; i++) {
                if (!context.isCancelled()) {
                    System.out.println("Sleep for 100 ms..");
                    Thread.sleep(100);
                } else {
                    return;
                }
            }

            responseObserver.onNext(GreetWithDeadlineResponse.newBuilder().setResult(
                    "Hello "+ request.getGreeting().getFirstName()).build());
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();;
        }
    }
}
