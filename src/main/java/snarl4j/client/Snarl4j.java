package snarl4j.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Snarl4j {
    private String serverAddress;
    private int serverPort;
    private String applicationId;

    public Snarl4j(String applicationId) {
        this.applicationId = applicationId;
        serverAddress = "localhost";
        serverPort = 9887;
    }

    public void register() {
        CommandPacketBuilder builder = new CommandPacketBuilder("register", applicationId);
        sendPacket(builder);
    }

    public void addClass(String classId) {
        addClass(classId, "");
    }

    public void addClass(String classId, String title) {
        CommandPacketBuilder builder = new CommandPacketBuilder("add_class", applicationId);
        builder.append("class", classId);
        if (title != null && title.trim().length() > 0) {
            builder.append("title", title);
        }
        sendPacket(builder);
    }

    public void notification(String classId, String titleText, String text, int numberOfSecondsToDisplay) {
        CommandPacketBuilder builder = new CommandPacketBuilder("notification", applicationId);
        builder.append("class", classId).append("title", titleText).append("text", text);
        builder.append("timeout", String.valueOf(numberOfSecondsToDisplay));
        sendPacket(builder);
    }

    public void unregister() {
        CommandPacketBuilder builder = new CommandPacketBuilder("unregister", applicationId);
        sendPacket(builder);
    }

    private void sendPacket(CommandPacketBuilder packetBuilder) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setSoTimeout(1000);
            socket.connect(new InetSocketAddress(serverAddress, serverPort));
            sendThePacketToTheServer(packetBuilder, socket);
            handleServerResponse(packetBuilder, whatDidTheServerSay(socket));
        } catch (IOException e) {
            throw new FailedCommandException(new String(packetBuilder.toPacket()).trim(), e);
        } finally {
            close(socket);
        }
    }

    private void sendThePacketToTheServer(CommandPacketBuilder packetBuilder, Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(packetBuilder.toPacket());
        outputStream.flush();
    }

    private void handleServerResponse(CommandPacketBuilder packetBuilder, ServerResponse response) {
        switch (response.response) {
            case ALREADY_REGISTERED:
            case CLASS_ALREADY_EXISTS:
            case NOT_REGISTERED:
            case SUCCESS:
                break;
            default:
                throw new FailedCommandException(
                        new String(packetBuilder.toPacket()).trim(),
                        response.packet
                );
        }
    }

    private ServerResponse whatDidTheServerSay(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String data = reader.readLine();
        SnarlResponse snarlResponse = ResponsePacketParser.parse(data);
        return new ServerResponse(data, snarlResponse);
    }

    private void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress.getHostName();
        this.serverPort = serverAddress.getPort();
    }

    private class ServerResponse {
        String packet;
        SnarlResponse response;

        private ServerResponse(String packet, SnarlResponse response) {
            this.packet = packet;
            this.response = response;
        }
    }
}
