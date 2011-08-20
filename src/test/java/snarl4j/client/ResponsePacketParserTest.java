package snarl4j.client;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static snarl4j.client.SnarlResponse.*;

public class ResponsePacketParserTest {
    @Test
    public void classAlreadyExists() {
        assertEquals(CLASS_ALREADY_EXISTS, parse("204"));
    }

    @Test
    public void alreadyRegistered() {
        assertEquals(ALREADY_REGISTERED, parse("203"));
    }

    @Test
    public void notRegistered() {
        assertEquals(NOT_REGISTERED, parse("202"));
    }

    @Test
    public void notRunning() {
        assertEquals(NOT_RUNNING, parse("201"));
    }

    @Test
    public void badPacket() {
        assertEquals(BAD_PACKET, parse("107"));
    }

    @Test
    public void timedOut() {
        assertEquals(TIMED_OUT, parse("103"));
    }

    @Test
    public void unknownCommand() {
        assertEquals(UNKNOWN_COMMAND, parse("102"));
    }

    @Test
    public void failedResponse() {
        assertEquals(FAILED, parse("101"));
    }

    @Test
    public void successfulResponse() {
        assertEquals(SUCCESS, parse("0"));
    }

    private SnarlResponse parse(String responseCode) {
        return ResponsePacketParser.parse("SNP/1.0/" + responseCode + "/we do not care about this");
    }
}
