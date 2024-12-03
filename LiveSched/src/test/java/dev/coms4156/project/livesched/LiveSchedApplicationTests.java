package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LiveSchedApplicationTests {

  private LiveSchedApplication liveSchedApplication;

  @BeforeEach
  public void setUp() {
    liveSchedApplication = new LiveSchedApplication();
  }

  @Test
  public void testOverrideDatabase() {
    MyFileDatabase mockDatabase = mock(MyFileDatabase.class);
    String clientId = "testClientId";
    LiveSchedApplication.overrideDatabase(mockDatabase, clientId);
    assertEquals(mockDatabase, LiveSchedApplication.getClientFileDatabase(clientId),
            "Database should be overridden.");
  }

  @Test
  public void testSetupExampleClientDatabase() {
    MyFileDatabase mockDatabase = mock(MyFileDatabase.class);
    liveSchedApplication.setupExampleClientDatabase("demoClientId");
    assertNotNull(LiveSchedApplication.clientDatabases.get("demoClientId"),
            "Example client database should be set up.");

    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.setupExampleClientDatabase(" "),
            "Client Id cannot be an empty string");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.setupExampleClientDatabase(""),
            "Client Id cannot be an empty string");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.setupExampleClientDatabase(null),
            "Client Id cannot be null");
  }

  @Test
  public void testOnTermination() {
    MyFileDatabase mockDatabase = mock(MyFileDatabase.class);
    LiveSchedApplication.clientDatabases.put("testClientId", mockDatabase);
    liveSchedApplication.onTermination();
    verify(mockDatabase, times(3)).saveContentsToFile(anyInt());
  }

  @Test
  public void testGenerateClientFilePath() {
    String clientId = "client1";
    String filePath = liveSchedApplication.generateClientFilePath(clientId, "tasks.txt");

    assertEquals("/tmp/client1_tasks.txt", filePath,
            "The generated file path should match the expected format.");

    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.generateClientFilePath(" ", "tasks.txt"),
            "Client Id cannot be an empty string");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.generateClientFilePath("", "tasks.txt"),
            "Client Id cannot be an empty string");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.generateClientFilePath(null, "tasks.txt"),
            "Client Id cannot be null");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.generateClientFilePath(clientId, " "),
            "FileName cannot be an empty string");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.generateClientFilePath(clientId, ""),
            "FileName cannot be an empty string");
    assertThrows(IllegalArgumentException.class,
            () -> liveSchedApplication.generateClientFilePath(clientId, null),
            "FileName cannot be null");
  }
}
