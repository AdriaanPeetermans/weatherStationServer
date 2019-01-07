package helpers;

import org.java_websocket.WebSocket;

import helpers.Server;

public class SocketMessage {
	
	public SocketMessage(WebSocket sock, String message, Server server) {
		this.sock = sock;
		this.message = message;
		this.server = server;
	}
	
	public final WebSocket sock;
	
	public final String message;
	
	public final Server server;
}
