package com.grpc.bidirectional.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingService {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090).build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Shutdown request Received");
            server.shutdown();
            System.out.println("Shutdown");
        }));

        server.awaitTermination();
    }
}
