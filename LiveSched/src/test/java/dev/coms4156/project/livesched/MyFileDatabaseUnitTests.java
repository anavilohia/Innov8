package dev.coms4156.project.livesched;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MyFileDatabaseUnitTests {

    private MyFileDatabase database;
    private static final String TASK_FILE = "tasks.dat";
    private static final String RESOURCE_FILE = "resources.dat";

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        String taskPath = new File(tempDir, TASK_FILE).getAbsolutePath();
        String resourcePath = new File(tempDir, RESOURCE_FILE).getAbsolutePath();
        database = new MyFileDatabase(1, taskPath, resourcePath);
    }

    @Test
    void testConstructor() {
        assertNotNull(database);
        assertTrue(database.getAllTasks().isEmpty());
        assertTrue(database.getAllResourceTypes().isEmpty());
    }

    @Test
    void testSetAndGetAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(createDummyTask());
        database.setAllTasks(tasks);
        assertEquals(1, database.getAllTasks().size());
    }

    @Test
    void testSetAndGetAllResourceTypes() {
        List<ResourceType> resources = new ArrayList<>();
        resources.add(createDummyResourceType());
        database.setAllResourceTypes(resources);
        assertEquals(1, database.getAllResourceTypes().size());
    }

    @Test
    void testInvalidContentType() {
        assertThrows(IllegalArgumentException.class, () -> database.saveContentsToFile(3));
        assertThrows(IllegalArgumentException.class, () -> database.deSerializeObjectFromFile(3));
    }

    @Test
    void testNullInput() {
        database.setAllTasks(null);
        assertTrue(database.getAllTasks().isEmpty());

        database.setAllResourceTypes(null);
        assertTrue(database.getAllResourceTypes().isEmpty());
    }

    @Test
    void testToString() {
        // As the toString method is not implemented, we just check it doesn't throw an exception
        assertDoesNotThrow(() -> database.toString());
    }

    private Task createDummyTask() {
        Map<ResourceType, Integer> resources = new HashMap<>();
        resources.put(createDummyResourceType(), 1);
        return new Task("DummyTask", resources, 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 0, 0);
    }

    private ResourceType createDummyResourceType() {
        return new ResourceType("DummyResource", 5, 0, 0);
    }
}