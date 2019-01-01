package siteServer;

import java.util.function.Function;

import siteServer.helpers.SocketMessage;
import siteServer.helpers.WebsocketServer;

public abstract class Server {
	
	public Server(int port) {
		this.port = port;
	}
	
	public abstract void handleMessage(SocketMessage t);
	
	public void start() {
		Function<SocketMessage, Void> responder = new Function<SocketMessage, Void>() {

			@Override
			public Void apply(SocketMessage t) {
				t.server.handleMessage(t);
				return null;
			}
			
		};
		WebsocketServer socketServer = new WebsocketServer(this.port, responder, this);
		socketServer.start();
	}
	
	public final int port;
}
