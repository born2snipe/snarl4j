package snarl4j.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestSnarlServer implements Runnable {
    private static final int DEFAULT_PORT = 9887;
    private SnarlResponse response = SnarlResponse.FAILED;
    private boolean kill;
    public String clientCommand;

    public void shouldRespondWith(SnarlResponse response) {
        this.response = response;
    }

    public void start() {
        new Thread(this, "test snarl server").start();
    }

    public void stop() {
        kill = true;
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            serverSocket.setReuseAddress(true);
            while (!kill) {
                communicateWithClient(serverSocket.accept());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(serverSocket);
        }
    }

    private void communicateWithClient(Socket client) {
        readClientCommand(client);
        respondToClient(client);
    }

    private void readClientCommand(Socket client) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientCommand = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void respondToClient(Socket client) {
        try {
            OutputStream outputStream = client.getOutputStream();
            String msg = "SNP/1.0/" + response.getCode() + "/" + response.name() + "\r\n";
            outputStream.write(msg.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(client);
        }
    }

    private void close(Socket client) {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {

            }
        }
    }

    private void close(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {

            }
        }
    }

    public void waitForServerToFinishProcessing() {
        long start = System.currentTimeMillis();
        long elapsedTime = 0L;
        while (clientCommand == null && elapsedTime < 1000L) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
            } finally {
                elapsedTime = System.currentTimeMillis() - start;
            }
        }
    }
}
