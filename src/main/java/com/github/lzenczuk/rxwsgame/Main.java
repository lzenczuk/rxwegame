package com.github.lzenczuk.rxwsgame;

import com.github.davidmoten.rx.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.io.File;

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
                    return response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                }else{
                    System.out.println("Not WS");
                    return response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                System.out.println("Other uri: "+uri);

                File file = new File("/home/dev/Downloads" + uri);
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
