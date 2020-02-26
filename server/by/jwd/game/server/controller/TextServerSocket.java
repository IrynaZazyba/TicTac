package by.jwd.game.server.controller;

import by.jwd.game.server.service.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TextServerSocket {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED = "\u001B[31m";

    private static final String RULE_NUMBER = "Use number to play.\n";
    private static final String RULE_WAIT = "Wait! Step other player!\n";
    private static final String RULE_STEP = "Your step:";
    private static final String RULE_WINNER= "!You are WINNER! Our congratulations!\n";
    private static final String RULE_LOSE="You are lose!\n";
    private static final int ANSWER_CLIENT_WAIT_TIME = 1000;

    private InputStream in;
    private OutputStream out;

    private ServerSocket server;
    private char type = 'x';
    private Game game;


    public void start() throws InterruptedException {
        try {
            server = new ServerSocket(4827);

            while (true) {
                System.out.println("Ожидаем подключения.");
                Socket socket = server.accept();

                in = socket.getInputStream();
                out = socket.getOutputStream();

                game = new Game();
                byte[] request = readRequestFromClient();
                byte[] response = null;
                while (request[0] != 0) {

                    response = responseProcessing(request);
                    if (game.checkWin()) {
                        sendResponseToClient(response);
                        System.out.println(ANSI_RED + game.getBoard());
                        System.out.println(ANSI_RED + RULE_LOSE + ANSI_RESET);
                        sendCloseConfirmationToClient();
                        break;
                    }

                    sendResponseToClient(response);
                    response = getBoardWithStep();
                    System.out.println(game.getBoard());

                    if (game.checkWin()) {
                        System.out.println(ANSI_RED + RULE_WINNER + ANSI_RESET);
                        sendResponseToClient(getWinBoard());
                        sendCloseConfirmationToClient();
                        break;
                    }

                    System.out.println(RULE_WAIT);

                    sendResponseToClient(response);
                    request = readRequestFromClient();
                }


            }
        } catch (IOException e) {
            System.out.println("Sorry! You can't play!");
        } finally {

            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                System.out.println("Sorry! We couldn't stop.");
            }
        }

    }

    private byte[] readRequestFromClient() throws InterruptedException,
            IOException {
        int i = in.available();
        while (i == 0) {
            Thread.sleep(ANSWER_CLIENT_WAIT_TIME);
            i = in.available();
        }
        byte[] b = new byte[i];
        in.read(b);
        return b;
    }

    private byte[] transformToByteArray(String s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(s.getBytes());
        byte[] response = out.toByteArray();
        out.close();

        return response;
    }


    private byte[] responseProcessing(byte[] request) throws IOException {

        int b = responseProcessingFirst(request);

        switch (b) {
            case 11:

                System.out.println(RULE_NUMBER + game.getBoard());
                System.out.println(ANSI_GREEN + RULE_STEP + ANSI_RESET);
                return transformToByteArray(RULE_NUMBER + game.getBoard() + RULE_WAIT);
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                String x = game.replace(b, '0');

                if (game.checkWin()) {
                    x = ANSI_RED + RULE_WINNER + x + ANSI_RESET;
                } else {
                    System.out.println(game.getBoard() + ANSI_GREEN + RULE_STEP + ANSI_RESET);
                    x = x + RULE_WAIT;
                }
                return transformToByteArray(x);

        }
        return null;
    }

    private int responseProcessingFirst(byte[] request) throws IOException {
        String s = new String(request);
        return Integer.parseInt(s);
    }

    private void sendResponseToClient(byte[] response) throws IOException, InterruptedException {

        out.write(response);
    }

    private int createRespond() throws InterruptedException, IOException {
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while (i < 1 || i > 9) {
            i = sc.nextInt();
        }
        return i;

    }

    private byte[] getWinBoard() throws IOException, InterruptedException {
        String replace = game.getBoard();
        return transformToByteArray(ANSI_RED + RULE_LOSE + replace + ANSI_RESET);

    }

    private byte[] getBoardWithStep() throws IOException, InterruptedException {
        String replace = game.replace(createRespond(), type);
        return transformToByteArray(replace + ANSI_PURPLE + "\n" + RULE_STEP + ANSI_RESET);

    }

    private void sendCloseConfirmationToClient() throws IOException, InterruptedException {
        Thread.sleep(1000);
        out.write(new byte[]{0});
    }


}
