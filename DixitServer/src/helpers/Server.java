package helpers;

import java.util.function.Function;

import helpers.SocketMessage;
import helpers.WebsocketServer;

public abstract class Server {
	
	public Server(int port) {
		this.port = port;
	}
	
	public abstract void handleMessage(SocketMessage t);
	
	public abstract void handleClose(SocketMessage t);
	
	protected WebsocketServer webSocketServer;
	
	public void start() {
		Function<SocketMessage, Void> responder = new Function<SocketMessage, Void>() {

			@Override
			public Void apply(SocketMessage t) {
				t.server.handleMessage(t);
				return null;
			}
			
		};
		Function<SocketMessage, Void> closer = new Function<SocketMessage, Void>() {

			@Override
			public Void apply(SocketMessage t) {
				t.server.handleClose(t);
				return null;
			}
			
		};
		this.webSocketServer = new WebsocketServer(this.port, responder, closer, this);
		this.webSocketServer.start();
	}
	
	public final int port;
}
