package snarl4j.client;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

public class CommandPacketBuilderTest {
    private CommandPacketBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new CommandPacketBuilder("actionName", "applicationId");
    }

    @Test
    public void multipleValues() {
        builder.append("data", "value").append("data2", "value2");
        assertPacket("data=value#?data2=value2");
    }

    @Test
    public void singleValue() {
        assertSame(builder, builder.append("data", "value"));
        assertPacket("data=value");
    }

    private void assertPacket(String expectedPacketDataContents) {
        assertEquals("packet contents do not match",
                "type=SNP#?version=1.0#?action=actionName#?app=applicationId#?" + expectedPacketDataContents + "\r\n", new String(builder.toPacket()));
    }
}
