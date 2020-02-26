package by.jwd.game.server.start;

import by.jwd.game.server.controller.TextServerSocket;

public class Start {

	public static void main(String[] args) throws InterruptedException {
		TextServerSocket server = new TextServerSocket();
		server.start();
	
	}

}
