package com.grpc.serverstream.server;

import com.gRPC.server.primenum.PrimeNumDecomRequest;
import com.gRPC.server.primenum.PrimeNumDecomResponse;
import com.gRPC.server.primenum.PrimeNumDecompServiceGrpc;
import io.grpc.stub.StreamObserver;

public class PrimeNumDecompServerImpl extends PrimeNumDecompServiceGrpc.PrimeNumDecompServiceImplBase {

    @Override
    public void primeNumDecomp(PrimeNumDecomRequest request, StreamObserver<PrimeNumDecomResponse> responseObserver) {

        int primeNum = request.getPrimeNumber();
        int k = 2;

        try {
            while (primeNum > 1) {
                if (primeNum % k == 0) {
                    responseObserver.onNext(PrimeNumDecomResponse.newBuilder().setResult(k).build());
                    primeNum = primeNum / k;
                } else {
                    k = k + 1;
                }
                Thread.sleep(1000L);
            }

            responseObserver.onCompleted();
        } catch (InterruptedException exp) {
            exp.printStackTrace();
        }
    }
}
