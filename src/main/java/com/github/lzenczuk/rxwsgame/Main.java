package com.github.lzenczuk.rxwsgame;

import com.github.davidmoten.rx.Bytes;
import com.github.lzenczuk.rxwsgame.message.*;
import com.github.lzenczuk.rxwsgame.model.Board;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.io.File;

/**
 * Created by dev on 10/07/17.
 */
public class Main {

    public static void main(String[] args) {

        WSMessageProcessor processor = new WSMessageProcessor();
        Board board = new Board();

        HttpServer<ByteBuf, ByteBuf> server = HttpServer.newServer(8088)
                .enableWireLogging("Http server", LogLevel.DEBUG)
                .start((request, response) -> {
            String uri = request.getUri();

            if(uri.startsWith("/ws")){
                System.out.println("WS uri: "+uri);
                if(request.isWebSocketUpgradeRequested()){
                    System.out.println("WS");

                    return response.acceptWebSocketUpgrade(wsConnection -> {

                        long connectionId = System.currentTimeMillis();

                        Observable<WSCommand> input = wsConnection.getInput()
                                .map(wsf -> {
                                    if (wsf instanceof BinaryWebSocketFrame) {
                                        BinaryWebSocketFrame bwsf = (BinaryWebSocketFrame) wsf;
                                        try {
                                            ByteBuf content = bwsf.content();
                                            byte[] bytes = new byte[content.readableBytes()];
                                            content.readBytes(bytes);

                                            WSCommand wsCommand = processor.parseByteArray(bytes);
                                            System.out.println(wsCommand);
                                            bwsf.release();
                                            return wsCommand;
                                        } catch (IllegalAccessException | InstantiationException e) {
                                            System.out.println("Unknown message");
                                            bwsf.release();
                                            return new ErrorCommand();
                                        }
                                    } else if (wsf instanceof CloseWebSocketFrame) {
                                        return new TerminateCommand(connectionId);
                                    }

                                    return new ErrorCommand();

                                }).filter(wsCommand -> !(wsCommand instanceof ErrorCommand));

                        board.getInput().onNext(input);

                        Observable<WebSocketFrame> output = board.getOutput()
                                .takeUntil(wsEvent -> {
                                    if(wsEvent instanceof TerminateEvent && ((TerminateEvent) wsEvent).getClientId()==connectionId){
                                        System.out.println("----------> terminate");
                                        return true;
                                    }
                                    return false;
                                })
                                .map(wsEvent -> new BinaryWebSocketFrame(Unpooled.wrappedBuffer(wsEvent.toByteArray())));
                        return wsConnection.writeAndFlushOnEach(output);
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
