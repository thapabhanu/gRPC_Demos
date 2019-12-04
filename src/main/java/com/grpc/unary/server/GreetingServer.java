package com.grpc.unary.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.File;
import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {

        // Plaintext server
        //Server server = ServerBuilder.forPort(9090).addService(new GreetingServerImpl()).build();

        // SSL enabled server
        Server server = ServerBuilder.forPort(50051)
                        .addService(new GreetingServerImpl())
                        .addService(ProtoReflectionService.newInstance())
                        /*.useTransportSecurity(
                                new File("ssl/server.crt"),
                                new File( "ssl/server.pem")
                        )*/
                        .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Request for Shutdown Received");
            server.shutdown();
            System.out.println("Server Shutdown");
        }));

        server.awaitTermination();
    }
}
