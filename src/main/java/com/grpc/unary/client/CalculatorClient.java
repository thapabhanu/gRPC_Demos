package com.grpc.unary.client;

import com.gRPC.unary.calculator.Calculator;
import com.gRPC.unary.calculator.CalculatorRequest;
import com.gRPC.unary.calculator.CalculatorResponse;
import com.gRPC.unary.calculator.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * A gRPC Unary service client. Client will send 2 numbers to server.
 * Server will sum these 2 numbers and return the sum up number
 */
public class CalculatorClient {

    public static void main(String[] args) {

        // Creating Channel to Server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                                        .usePlaintext().build();
        // Creating Sync Stub
        CalculatorServiceGrpc.CalculatorServiceBlockingStub client = CalculatorServiceGrpc.newBlockingStub(channel);

        // Setting up input data
        Calculator calculator = Calculator.newBuilder().setInput1(10).setInput2(3).build();
        CalculatorRequest request = CalculatorRequest.newBuilder().setCalculator(calculator).build();

        // Calling remote calculator method using sync stub
        CalculatorResponse response = client.calculator(request);

        // Printing response
        System.out.println("Response "+ response.getResult());
    }
}
