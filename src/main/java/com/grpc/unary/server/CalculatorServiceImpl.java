package com.grpc.unary.server;

import com.gRPC.unary.calculator.Calculator;
import com.gRPC.unary.calculator.CalculatorRequest;
import com.gRPC.unary.calculator.CalculatorResponse;
import com.gRPC.unary.calculator.CalculatorServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    /**
     * A unary service method. Calculator method will process the client request
     * it will add 2 numbers and return the sum up number
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void calculator(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        Calculator calculator = request.getCalculator();
        int input1 = calculator.getInput1();
        int input2 = calculator.getInput2();

        int result = input1 + input2;

        CalculatorResponse response = CalculatorResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
