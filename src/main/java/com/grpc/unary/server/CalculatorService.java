package com.grpc.unary.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * A gRPC Server
 */
public class CalculatorService {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Creating gRPC Unary Server
        Server server = ServerBuilder.forPort(9090).addService(new CalculatorServiceImpl()).build();

        // Starting the server
        server.start();

        // Creating the Shut down hook and execute task before shutdown
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Request for Shutting down");
            server.shutdown();
            System.out.println("Shutting Down ....");
        }));

        // Server await Termination
        server.awaitTermination();
    }
}
