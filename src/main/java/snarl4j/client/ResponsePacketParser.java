package snarl4j.client;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponsePacketParser {
    private static final Pattern RESPONSE_CODE_PATTERN = Pattern.compile("/([0-9]+)/");

    public static SnarlResponse parse(String data) {
        Matcher matcher = RESPONSE_CODE_PATTERN.matcher(data);
        matcher.find();
        return SnarlResponse.find(matcher.group(1));
    }
}
