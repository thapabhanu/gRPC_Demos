package com.grpc.examples.routeguide;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * A gRPC serve which serves RouteGuide service
 */
public class RouteGuideServer {

    private final int port;
    private final Server server;

    public RouteGuideServer(int port) throws IOException {
        this(port, RouteGuideUtil.getDefaultFeaturesFile());
    }
    public RouteGuideServer(int port, URL file) throws IOException {
        this(ServerBuilder.forPort(50051), port, RouteGuideUtil.parseFeatures(file));
    }
    public RouteGuideServer(ServerBuilder<?> builder, int port, Collection<com.grpc.examples.routeguide.Feature> feature) {
        this.port = port;
        this.server = builder.addService(new RouteGuideServiceImpl(feature)).build();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RouteGuideServer server = new RouteGuideServer(50051);
        server.start();
        server.blockUntilShutdown();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Server Started at port "+ port);
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Shutdown request received");
            RouteGuideServer.this.stop();
            System.out.println("Server shutdown");
        }));
    }

    public void stop() {
        if (server!=null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server!=null) {
            server.awaitTermination();
        }
    }
}
