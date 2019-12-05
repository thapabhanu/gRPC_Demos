package com.grpc.blog.server;

import com.gRPC.blog.*;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServerImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient client = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase db = client.getDatabase("mydb");
    private MongoCollection<Document> collection = db.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        Blog blog = request.getBlog();

        Document document = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        collection.insertOne(document);
        String id = document.getObjectId("_id").toString();

        responseObserver.onNext(CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build()).build());

        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        String id = request.getId();
        Document result = null;
        try {
            result = collection.find(eq("_id", new ObjectId(id))).first();
        }catch (Exception e){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No record found for id "+ id)
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException());
        }

        if( result == null ) {
            System.out.println("No record found for id = "+ id);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No record found for id "+ id).asRuntimeException());
        } else {
            System.out.println("Record found, sending response");
            ReadBlogResponse response = ReadBlogResponse.newBuilder().setBlog(
                    Blog.newBuilder().setId(id).setAuthorId(result.getString("title"))
                    .setTitle(result.getString("title")).setContent(result.getString("content")).build()
            ).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("Update blog request received...");

        Blog blog = request.getBlog();
        Document result = null;
        try {
            System.out.println("Finding id for update request "+ blog.getId());
            result = collection.find(eq("_id", new ObjectId(blog.getId()))).first();
        }catch (Exception e){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No record found for id "+ blog.getId())
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException());
        }

        if(result!=null) {
            Document replaceDocument = new Document("author_id", blog.getAuthorId())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent())
                    .append("_id", new ObjectId(blog.getId()));

            System.out.println("Updating the update request into DB");
            collection.replaceOne(eq("_id", result.getObjectId("_id")), replaceDocument);

            System.out.println("Returning the response...");
            responseObserver.onNext(UpdateBlogResponse.newBuilder()
                    .setBlog(documentToBlog(replaceDocument)).build());

            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No record found for id "+ blog.getId())
                    .asRuntimeException());
        }
    }

    private Blog documentToBlog(Document doc) {

        return Blog.newBuilder().setId(doc.getObjectId("_id").toString())
                .setAuthorId(doc.getString("author_id"))
                .setTitle(doc.getString("title"))
                .setContent(doc.getString("content")).build();
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
        String blogId = request.getId();
        DeleteResult result = null;
        try{
            result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
        }catch (Exception e){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No record found for id "+ blogId)
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException());
        }
        if(result.getDeletedCount() == 0){
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No record found for id "+ blogId)
                    .asRuntimeException());
        } else {
            responseObserver.onNext(DeleteBlogResponse.newBuilder().setId(blogId).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
        System.out.println("Listblog request recevied");
        collection.find().iterator().forEachRemaining(doc->{
            responseObserver.onNext(ListBlogResponse.newBuilder()
                    .setBlog(documentToBlog(doc)).build());
        });
        responseObserver.onCompleted();
    }
}
