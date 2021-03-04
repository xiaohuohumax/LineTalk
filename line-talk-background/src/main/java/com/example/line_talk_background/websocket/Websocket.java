package com.example.line_talk_background.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws")
@Component
public class Websocket {

    private static final CopyOnWriteArraySet<Websocket> webSocketSet = new CopyOnWriteArraySet<>();

    Session session;

    String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        webSocketSet.add(this);
        System.out.println("link ok");
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        for (Websocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (Exception ignore) {
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        webSocketSet.remove(this);
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
