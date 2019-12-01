package com.grpc.clientstream.server;

import com.gRPC.common.greeting.*;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

   /* @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {

        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + " response number: " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder().setResult(result).build();

                responseObserver.onNext(response);

                Thread.sleep(1000L);
            }
        }catch (InterruptedException exp) {
            exp.printStackTrace();
        }

        responseObserver.onCompleted();
    }
*/
    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> request = new StreamObserver<LongGreetRequest>() {

            String result = "";
            @Override
            public void onNext(LongGreetRequest value) {
                // Client send the request
                String firstName = value.getGreeting().getFirstName();

                result += "Hello " + firstName + "!";
            }

            @Override
            public void onError(Throwable t) {
                // Client send a error
            }

            @Override
            public void onCompleted() {
                // client done sending all the messages
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());

                responseObserver.onCompleted();
            }
        };

        return request;
    }
}
