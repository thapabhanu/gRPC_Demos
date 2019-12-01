package com.grpc.serverstream.client;

import com.gRPC.server.primenum.PrimeNumDecomRequest;
import com.gRPC.server.primenum.PrimeNumDecompServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PrimeNumDecompClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        PrimeNumDecompServiceGrpc.PrimeNumDecompServiceBlockingStub client =
                                        PrimeNumDecompServiceGrpc.newBlockingStub(channel);

        PrimeNumDecomRequest request = PrimeNumDecomRequest.newBuilder().setPrimeNumber(120).build();
        client.primeNumDecomp(request).forEachRemaining(response ->{
            System.out.println(response.getResult());
        });
    }
}
