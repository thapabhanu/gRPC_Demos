package com.grpc.clientstream.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9090).addService(new GreetingServerImpl()).build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Request for Shutdown Received");
            server.shutdown();
            System.out.println("Server Shutdown");
        }));

        server.awaitTermination();
    }
}
