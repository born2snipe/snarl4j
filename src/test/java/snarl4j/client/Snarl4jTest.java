package snarl4j.client;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

import static junit.framework.Assert.assertEquals;
import static snarl4j.client.SnarlResponse.*;

public class Snarl4jTest {
    private Snarl4j snarl4j;
    private static TestSnarlServer server = new TestSnarlServer();

    @BeforeClass
    public static void startServer() {
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }

    @Before
    public void setUp() throws Exception {
        server.shouldRespondWith(SUCCESS);

        snarl4j = new Snarl4j();
    }

    @Test(expected = FailedCommandException.class)
    public void shouldBlowUpIfThereIsAProblemConnectingToTheServer() {
        snarl4j.setServerAddress(new InetSocketAddress("localhost", 1234));
        snarl4j.register("appId");
    }

    @Test
    public void shouldNotBlowUpWhenTheServerSaysTheApplicationWasNeverRegistered() {
        server.shouldRespondWith(NOT_REGISTERED);

        snarl4j.unregister("appId");
        server.waitForServerToFinishProcessing();
    }

    @Test(expected = FailedCommandException.class)
    public void shouldBlowUpWhenFailsToUnregisterAnApp() {
        server.shouldRespondWith(FAILED);

        snarl4j.unregister("appId");
        server.waitForServerToFinishProcessing();
    }

    @Test
    public void unregisterAnAppSuccessfully() {
        snarl4j.unregister("appId");

        server.waitForServerToFinishProcessing();
        assertEquals("type=SNP#?version=1.0#?action=unregister#?app=appId", server.clientCommand);
    }

    @Test(expected = FailedCommandException.class)
    public void blowsUpWhenFailingToProcessANotification() {
        server.shouldRespondWith(FAILED);
        snarl4j.notification("appId", "classId", "titleText", "displayText", 10);

        server.waitForServerToFinishProcessing();
    }

    @Test
    public void notificationSuccessfullyProcessed() {
        snarl4j.notification("appId", "classId", "titleText", "displayText", 10);

        server.waitForServerToFinishProcessing();
        assertEquals("type=SNP#?version=1.0#?action=notification#?app=appId#?class=classId#?title=titleText#?text=displayText#?timeout=10", server.clientCommand);
    }

    @Test
    public void addClassSuccessfullyWithATitleWithALotOfWhiteSpace() {
        snarl4j.addClass("applicationId", "classId", "   ");

        server.waitForServerToFinishProcessing();
        assertAddClassCommand("applicationId", "classId", null);
    }

    @Test
    public void addClassSuccessfullyWithAEmptyTitle() {
        snarl4j.addClass("applicationId", "classId", "");

        server.waitForServerToFinishProcessing();
        assertAddClassCommand("applicationId", "classId", null);
    }

    @Test
    public void addClassSuccessfullyWithANullTitle() {
        snarl4j.addClass("applicationId", "classId", null);

        server.waitForServerToFinishProcessing();
        assertAddClassCommand("applicationId", "classId", null);
    }

    @Test
    public void addClassSuccessfullyWithATitle() {
        snarl4j.addClass("applicationId", "classId", "titleText");

        server.waitForServerToFinishProcessing();
        assertAddClassCommand("applicationId", "classId", "titleText");
    }

    @Test
    public void addClassSuccessfullyWithoutATitle() {
        snarl4j.addClass("applicationId", "classId");

        server.waitForServerToFinishProcessing();
        assertAddClassCommand("applicationId", "classId", null);
    }

    @Test
    public void shouldNotBlowUpIfTheClassAlreadyExists() {
        server.shouldRespondWith(CLASS_ALREADY_EXISTS);
        snarl4j.addClass("applicationId", "classId");

        server.waitForServerToFinishProcessing();
    }

    @Test(expected = FailedCommandException.class)
    public void shouldBlowUpIfFailsAddAClass() {
        server.shouldRespondWith(FAILED);
        snarl4j.addClass("applicationId", "classId");

        server.waitForServerToFinishProcessing();
    }

    @Test(expected = FailedCommandException.class)
    public void registeringWithTheServerFails() {
        server.shouldRespondWith(FAILED);

        snarl4j.register("applicationName");
        server.waitForServerToFinishProcessing();
    }

    @Test
    public void registerSuccessfullyWithTheServer() {
        snarl4j.register("applicationName");

        server.waitForServerToFinishProcessing();
        assertEquals("type=SNP#?version=1.0#?action=register#?app=applicationName", server.clientCommand);
    }

    @Test
    public void shouldNotBlowUpIfTheAppHasAlreadyBeenRegistered() {
        snarl4j.register("applicationName");

        server.waitForServerToFinishProcessing();
    }

    private void assertAddClassCommand(String applicationId, String classId, String titleText) {
        String expectedCommand = "type=SNP#?version=1.0#?action=add_class#?app=" + applicationId + "#?class=" + classId;
        if (titleText != null) {
            expectedCommand += "#?title=" + titleText;
        }
        assertEquals(expectedCommand, server.clientCommand);
    }
}
