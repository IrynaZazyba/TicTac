package by.jwd.game.client.start;

import by.jwd.game.client.controller.TextClientSocket;

public class Start {

	public static void main(String[] args) {
		TextClientSocket client = new TextClientSocket("localhost", 4827);
		client.start();

	}

}
