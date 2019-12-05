package com.grpc.blog.client;

import com.gRPC.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        BlogServiceGrpc.BlogServiceBlockingStub blockingStub = BlogServiceGrpc.newBlockingStub(channel);

        CreateBlogResponse response = blockingStub.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder().setAuthorId("Auth101").setTitle("My Blog")
                .setContent("This is my first Blog").build()).build());

        System.out.println("Response from CreateBlog "+response.toString());

        ReadBlogResponse readResponse1 = blockingStub.readBlog(
                ReadBlogRequest.newBuilder().setId(response.getBlog().getId()).build());
        System.out.println("Response from ReadBlog "+readResponse1.toString());

        /*try {
        ReadBlogResponse readResponse2 = blockingStub.readBlog(
                ReadBlogRequest.newBuilder().setId("fake_id").build());
        System.out.println("Response from ReadBlog with fake id "+readResponse1.toString());
        } catch (StatusRuntimeException e) {
            System.out.println("Error received for ReadBlog request with fake id");
            e.printStackTrace();
        }*/

        System.out.println("Calling Update RPC service....");
        try {
            Blog updateBlog = Blog.newBuilder()
                    .setId(response.getBlog().getId())
                    .setAuthorId("Updated Author").setTitle("Updated Title").setContent("Update Content").build();

            UpdateBlogResponse updatedBlog = blockingStub.updateBlog(UpdateBlogRequest.newBuilder()
                    .setBlog(updateBlog).build());
            System.out.println("Updated Blog Response" + updatedBlog);
        } catch (StatusRuntimeException e) {
            System.out.println("Error received for UpdateBlog request..");
            e.printStackTrace();
        }

        System.out.println("Calling Delete  RPC service....");
        blockingStub.deleteBlog(DeleteBlogRequest.newBuilder().setId(response.getBlog().getId()).build());
        System.out.println("Blog Deleted");

        /*System.out.println("Reading after deleted..");
        ReadBlogResponse readResponse2 = blockingStub.readBlog(
                ReadBlogRequest.newBuilder().setId(response.getBlog().getId()).build());
        System.out.println("Response from ReadBlog "+readResponse1.toString());*/
        System.out.println("Calling ListBlog...");
        blockingStub.listBlog(null).forEachRemaining(resp ->{
            System.out.println("list: "+resp.getBlog().toString());
        });
        System.out.println("ListBlog completed ...");



    }
}
