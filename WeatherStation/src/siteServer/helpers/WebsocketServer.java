package siteServer.helpers;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import siteServer.Server;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class WebsocketServer extends WebSocketServer {

    private Set<WebSocket> conns;
    
    private final Function<SocketMessage, Void> responder;
    
    private final Server server;

    public WebsocketServer(int port, Function<SocketMessage, Void> responder, Server server) {
        super(new InetSocketAddress(port));
        this.conns = new HashSet<>();
        this.responder = responder;
        this.server = server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        this.conns.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        this.conns.remove(conn);
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //System.out.println("Message from client: " + message);
//        for (WebSocket sock : conns) {
//            sock.send(message);
//        }
			this.responder.apply(new SocketMessage(conn, message, this.server));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            conns.remove(conn);
            // do some thing if required
        }
        System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

}