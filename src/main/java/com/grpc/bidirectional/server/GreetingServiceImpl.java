package com.grpc.bidirectional.server;

import com.gRPC.common.greeting.GreetingServiceGrpc;
import com.gRPC.common.greeting.ManyTimesGreetRequest;
import com.gRPC.common.greeting.ManyTimesGreetResponse;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public StreamObserver<ManyTimesGreetRequest> manyTimesGreet(StreamObserver<ManyTimesGreetResponse> responseObserver) {

        StreamObserver<ManyTimesGreetRequest> reqObserver = new StreamObserver<ManyTimesGreetRequest>() {
            @Override
            public void onNext(ManyTimesGreetRequest value) {
                String result = "Hello " +value.getGreeting().getFirstName()+ " !\n";

                responseObserver.onNext(ManyTimesGreetResponse.newBuilder().setResult(result).build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return reqObserver;
    }
}
