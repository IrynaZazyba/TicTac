package by.jwd.game.client.controller;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TextClientSocket {
    private static final int ANSWER_SERVER_WAIT_TIME = 1000;

    private String ip;
    private int port;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public TextClientSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        try {
            socket = new Socket(ip, port);

            System.out.println("Клиент: соединение установлено.");

            in = socket.getInputStream();
            out = socket.getOutputStream();
            System.out.println("Enter 11 to start!");

            byte[] request = startGameListener();
            byte[] response;
            while (request != null) {

                sendRequestToServer(request);

                response = readAnswerFromServer();
                if (response[0] == 27) {

                    showResponse(response);
                    request = null;
                    break;
                }
                showResponse(response);


                response = giveStep();
                showResponse(response);
                if (response[0] == 27) {

                    request = null;
                    break;
                }


                request = createRequest();


            }


        } catch (IOException | InterruptedException e) {
            System.out.println("Sorry. We couldn't play.");
        } finally {
            try {
                if (sendCloseRequestToServer()) {
                    socket.close();
                    System.out.println("Связь с сервером успешно закрыта.");
                } else {
                    socket.close();
                    System.out.println("Связь с сервером закрыта. Ответ от сервера не получен.");
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Sorry. We couldn't stop.");
            }
        }
    }

    private byte[] createTextStream(int i) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(String.valueOf(i).getBytes());
        byte[] request = out.toByteArray();
        out.close();

        return request;
    }

    private void showResponse(byte[] b) {
        String str = new String(b);
        System.out.println(str);
    }

    private byte[] startGameListener() throws IOException {
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while (i != 11) {
            i = sc.nextInt();
        }

        return createTextStream(i);

    }

    private byte[] createRequest() throws IOException {
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while (i < 1 || i > 9) {
            i = sc.nextInt();
        }

        return createTextStream(i);

    }

    private void sendRequestToServer(byte[] b) throws IOException {

        out.write(b);
    }

    private byte[] readAnswerFromServer() throws InterruptedException,
            IOException {
        int i = in.available();
        while (i == 0) {
            Thread.sleep(ANSWER_SERVER_WAIT_TIME);
            i = in.available();
        }
        byte[] b = new byte[i];
        in.read(b);
        return b;
    }

    private byte[] giveStep() throws InterruptedException,
            IOException {
        int i = in.available();
        while (i == 0) {
            Thread.sleep(ANSWER_SERVER_WAIT_TIME);
            i = in.available();
        }
        byte[] b = new byte[i];
        in.read(b);
        return b;
    }


    private boolean sendCloseRequestToServer() throws IOException, InterruptedException {
        out.write(new byte[]{0});
        byte[] response = readAnswerFromServer();
        if (response[0] == 0) {
            return true;
        }
        return false;
    }
}
