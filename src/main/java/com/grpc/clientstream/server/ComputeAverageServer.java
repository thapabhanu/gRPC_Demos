package com.grpc.clientstream.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ComputeAverageServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(9090).addService(new ComputeAverageServerImpl()).build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () ->{
            System.out.println("Shut Down request received.");
            server.shutdown();
            System.out.println("Server Shut down");
        }));

        server.awaitTermination();
    }
}
