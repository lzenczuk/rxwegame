package com.github.lzenczuk.rxwsgame;

import com.github.davidmoten.rx.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by dev on 10/07/17.
 */
public class Main {

    public static void main(String[] args) {

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8088)
                .enableWireLogging("Http server", LogLevel.DEBUG)
                .start((request, response) -> {
            String uri = request.getUri();

            if(uri.startsWith("/ws")){
                System.out.println("WS uri: "+uri);
                if(request.isWebSocketUpgradeRequested()){
                    System.out.println("WS");

                    return response.acceptWebSocketUpgrade(wsConnection -> {

                        PublishSubject<Void> closeConnection = PublishSubject.create();

                        /*wsConnection.writeAndFlushOnEach(outputPublisher.map(s -> {
                            System.out.println("Output write: "+s);
                            return new BinaryWebSocketFrame(Unpooled.copiedBuffer(s.getBytes()));
                        }));*/

                        /*String[] msges = new String[3];
                        msges[0]="test1";
                        msges[1]="test2";
                        msges[2]="test3";*/

                        System.out.println("----------> create output observable");
                        Observable<WebSocketFrame> outOb = Observable.interval(1l, TimeUnit.SECONDS)
                                .map(aLong -> {
                                    System.out.println("Tick mapp");
                                    return "Tick: " + aLong;
                                })
                                .onBackpressureBuffer()
                                .map(s -> {
                                    System.out.println("Output write: " + s);
                                    //return new TextWebSocketFrame(s);
                                    return new BinaryWebSocketFrame(Unpooled.copiedBuffer(s.getBytes()));
                                }).cast(WebSocketFrame.class);

                        /*Observable<WebSocketFrame> outOb = Observable.from(msges).map(s -> {
                            System.out.println("Output write: " + s);
                            return new BinaryWebSocketFrame(Unpooled.copiedBuffer(s.getBytes()));
                        });*/

                        wsConnection.getInput()
                                .filter(wsf -> {
                                    if(wsf instanceof BinaryWebSocketFrame){
                                        return true;
                                    }else{
                                        wsf.release();
                                        return false;
                                    }
                                })
                                .cast(BinaryWebSocketFrame.class)
                                .forEach(bwsf -> {
                                    byte type = bwsf.content().getByte(0);
                                    switch (type){
                                        case 0:
                                            System.out.println("Zero");
                                            break;
                                        case 1:
                                            System.out.println("One");
                                            break;
                                        case 127:
                                            System.out.println("#################################");
                                            System.out.println("-------> End");
                                            System.out.println("#################################");
                                            closeConnection.onCompleted();
                                            break;
                                        default:
                                            System.out.println("Unknown");
                                    }

                                    bwsf.release();
                                });

                        System.out.println("----------> Write output");
                        return Observable.merge(closeConnection, wsConnection.writeAndFlushOnEach(outOb));
                    });
                }else{
                    System.out.println("Not WS");
                    return response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                System.out.println("Other uri: "+uri);

                String path = uri;
                if(uri.endsWith("/")){
                    path = uri+"index.html";
                }

                File file = new File("/home/dev/Documents/java-sandbox/rxwsgame/src/main/resources/public" + path);
                if(file.exists() && file.isFile()){
                    return response.writeBytes(Bytes.from(file));
                }else{
                    System.out.println("Not exists or not file.");
                    return response.setStatus(HttpResponseStatus.NOT_FOUND);
                }
            }
        });

        server.awaitShutdown();
    }
}
