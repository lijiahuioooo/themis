package com.mfw.themis.collector.server;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * The service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: CollectorService.proto")
public final class CollectorServiceGrpc {

  private CollectorServiceGrpc() {}

  public static final String SERVICE_NAME = "com.mfw.themis.collector.server.CollectorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq,
      com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes> getReportMessageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "reportMessage",
      requestType = com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq.class,
      responseType = com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq,
      com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes> getReportMessageMethod() {
    io.grpc.MethodDescriptor<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq, com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes> getReportMessageMethod;
    if ((getReportMessageMethod = CollectorServiceGrpc.getReportMessageMethod) == null) {
      synchronized (CollectorServiceGrpc.class) {
        if ((getReportMessageMethod = CollectorServiceGrpc.getReportMessageMethod) == null) {
          CollectorServiceGrpc.getReportMessageMethod = getReportMessageMethod = 
              io.grpc.MethodDescriptor.<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq, com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "com.mfw.themis.collector.server.CollectorService", "reportMessage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes.getDefaultInstance()))
                  .setSchemaDescriptor(new CollectorServiceMethodDescriptorSupplier("reportMessage"))
                  .build();
          }
        }
     }
     return getReportMessageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CollectorServiceStub newStub(io.grpc.Channel channel) {
    return new CollectorServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CollectorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CollectorServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CollectorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CollectorServiceFutureStub(channel);
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static abstract class CollectorServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Sends message
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq> reportMessage(
        io.grpc.stub.StreamObserver<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes> responseObserver) {
      return asyncUnimplementedStreamingCall(getReportMessageMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReportMessageMethod(),
            asyncClientStreamingCall(
              new MethodHandlers<
                com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq,
                com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes>(
                  this, METHODID_REPORT_MESSAGE)))
          .build();
    }
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static final class CollectorServiceStub extends io.grpc.stub.AbstractStub<CollectorServiceStub> {
    private CollectorServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CollectorServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CollectorServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CollectorServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Sends message
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorReq> reportMessage(
        io.grpc.stub.StreamObserver<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(getReportMessageMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static final class CollectorServiceBlockingStub extends io.grpc.stub.AbstractStub<CollectorServiceBlockingStub> {
    private CollectorServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CollectorServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CollectorServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CollectorServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * <pre>
   * The service definition.
   * </pre>
   */
  public static final class CollectorServiceFutureStub extends io.grpc.stub.AbstractStub<CollectorServiceFutureStub> {
    private CollectorServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CollectorServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CollectorServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CollectorServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_REPORT_MESSAGE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CollectorServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CollectorServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REPORT_MESSAGE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.reportMessage(
              (io.grpc.stub.StreamObserver<com.mfw.themis.collector.server.CollectorServiceOuterClass.MfwCollectorRes>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CollectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CollectorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.mfw.themis.collector.server.CollectorServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CollectorService");
    }
  }

  private static final class CollectorServiceFileDescriptorSupplier
      extends CollectorServiceBaseDescriptorSupplier {
    CollectorServiceFileDescriptorSupplier() {}
  }

  private static final class CollectorServiceMethodDescriptorSupplier
      extends CollectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CollectorServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CollectorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CollectorServiceFileDescriptorSupplier())
              .addMethod(getReportMessageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
