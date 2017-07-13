package com.github.lzenczuk.rxwsgame.message;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev on 12/07/17.
 */
public class WSMessageProcessor {

    private Map<Long, Class<? extends WSCommand>> idMessageMap;

    public WSMessageProcessor() {
        idMessageMap = new HashMap<>();

        new Reflections("com.github.lzenczuk.rxwsgame.message").getSubTypesOf(WSCommand.class).forEach(aClass -> {
            System.out.println("Processing message: "+aClass);
            try {
                Long messageId = aClass.newInstance().getMessageId();
                idMessageMap.put(messageId, aClass);
                System.out.println("Map "+messageId+" -> "+aClass);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    public WSCommand parseByteArray(byte[] bytes) throws IllegalAccessException, InstantiationException {
        if(bytes==null || bytes.length==0){
            throw new RuntimeException("Empty byte array");
        }

        Long msgId = Long.valueOf(bytes[0]);
        Class<? extends WSCommand> wsMessageClass = idMessageMap.get(msgId);
        if(wsMessageClass==null){
            System.out.println("-----------> No WSCommand with msgId: "+msgId);
            return null;
        }
        WSCommand wsCommand = wsMessageClass.newInstance();
        wsCommand.applyByteArray(bytes);

        return wsCommand;
    }
}
