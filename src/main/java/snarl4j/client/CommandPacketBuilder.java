package snarl4j.client;


import java.util.LinkedHashMap;
import java.util.Map;

public class CommandPacketBuilder {
    private Map<String, String> dataValues = new LinkedHashMap<String, String>();

    public CommandPacketBuilder() {
        append("type", "SNP").append("version", "1.0");
    }

    public CommandPacketBuilder append(String data, String value) {
        dataValues.put(data, value);
        return this;
    }

    public byte[] toPacket() {
        StringBuilder builder = new StringBuilder();

        int index = 0;
        for (Map.Entry<String, String> entry : dataValues.entrySet()) {
            if (index > 0) {
                builder.append("#?");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            index++;
        }
        builder.append("\r\n");
        return builder.toString().getBytes();
    }
}
