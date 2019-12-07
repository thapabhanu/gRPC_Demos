package com.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("This is BlogServer..");

        Server server = ServerBuilder.forPort(50051)
                .addService(new BlogServerImpl())
                .addService(ProtoReflectionService.newInstance())
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