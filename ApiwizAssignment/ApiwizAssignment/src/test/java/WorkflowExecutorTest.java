
import com.apiwiz.assignment.WorkflowExecutor;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkflowExecutorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testWorkflowExecution() {
        String input = String.join("\n",
                "5",
                "1:Node-1",
                "2:Node-2",
                "3:Node-3",
                "4:Node-4",
                "5:Node-5",
                "5",
                "1:2",
                "1:3",
                "2:4",
                "2:5",
                "3:5"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        WorkflowExecutor.main(new String[]{});

        String output = outContent.toString().trim();
        String[] lines = output.split("\\R");

        // Check that all node names are printed
        assertTrue(containsLine(lines, "Node-1"));
        assertTrue(containsLine(lines, "Node-2"));
        assertTrue(containsLine(lines, "Node-3"));
        assertTrue(containsLine(lines, "Node-4"));
        assertTrue(containsLine(lines, "Node-5"));
        // Check that final size is printed
        assertTrue(output.endsWith("5"));
    }

    private boolean containsLine(String[] lines, String line) {
        for (String l : lines) {
            if (l.equals(line)) {
                return true;
            }
        }
        return false;
    }
    @Test
    void testSingleNodeWorkflow() {
        String input = String.join("\n",
                "1",
                "1:Node-1",
                "0"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        WorkflowExecutor.main(new String[]{});

        String output = outContent.toString().trim();
        assertTrue(output.contains("Node-1"));
        assertTrue(output.endsWith("1"));
    }

    @Test
    void testDiamondGraph() {
        String input = String.join("\n",
                "4",
                "1:Node-1",
                "2:Node-2",
                "3:Node-3",
                "4:Node-4",
                "4",
                "1:2",
                "1:3",
                "2:4",
                "3:4"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        WorkflowExecutor.main(new String[]{});

        String output = outContent.toString().trim();
        assertTrue(output.contains("Node-1"));
        assertTrue(output.contains("Node-2"));
        assertTrue(output.contains("Node-3"));
        assertTrue(output.contains("Node-4"));
        assertTrue(output.endsWith("4"));
    }

    @Test
    void testCycleDetectionNotHandled() {
        String input = String.join("\n",
                "3",
                "1:Node-1",
                "2:Node-2",
                "3:Node-3",
                "3",
                "1:2",
                "2:3",
                "3:1" // Cycle here
        );

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        // Set a timeout to prevent infinite wait as it is given in problem statement it is a tree structure so cycle is not
        //possible.
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
            WorkflowExecutor.main(new String[]{});
        });
    }

    @Test
    void testEmptyInput() {
        String input = "0\n0\n";

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        WorkflowExecutor.main(new String[]{});

        String output = outContent.toString().trim();
        // Nothing should be printed except the node count
        assertTrue(output.endsWith("0"));
    }
    @Test
    void testSelfLoop() {
        String input = String.join("\n",
                "1",
                "1:Node-1",
                "1",
                "1:1"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        // Set a timeout to prevent infinite wait
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
            WorkflowExecutor.main(new String[]{});
        });
    }



}
