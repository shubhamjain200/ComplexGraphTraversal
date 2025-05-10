package com.apiwiz.assignment;

import java.util.*;
import java.util.concurrent.*;

class Node {
    String id;
    String name;
    Set<Node> parents = new HashSet<>();
    Set<Node> children = new HashSet<>();
    int unprocessedParents = 0;

    public Node(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void execute() {
        System.out.println(name);
    }
}

public class WorkflowExecutor {
    private Map<String, Node> nodes = new HashMap<>();
    private Node rootNode;
    private CountDownLatch completionLatch;
    private Set<String> processedNodes = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        WorkflowExecutor executor = new WorkflowExecutor();
        executor.readInput();
        executor.executeWorkflow();
    }

    private void readInput() {
        Scanner scanner = new Scanner(System.in);
        int numVertices = Integer.parseInt(scanner.nextLine());

        // Read vertices
        for (int i = 0; i < numVertices; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            String id = parts[0];
            String name = parts[1];
            nodes.put(id, new Node(id, name));
        }

        // Read edges
        int numEdges = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < numEdges; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            String sourceId = parts[0];
            String destId = parts[1];

            Node source = nodes.get(sourceId);
            Node dest = nodes.get(destId);

            source.children.add(dest);
            dest.parents.add(source);
            dest.unprocessedParents++;
        }

        // Find the root node (assuming key is "1")
        rootNode = nodes.get("1");
        completionLatch = new CountDownLatch(nodes.size());
    }

    private void executeWorkflow() {
        ExecutorService executor = Executors.newCachedThreadPool();

        // Start with the root node
        processNode(rootNode, executor);

        try {
            // Wait for all nodes to be processed
            completionLatch.await();
            executor.shutdown();
            System.out.println(nodes.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Execution interrupted: " + e.getMessage());
        }
    }

    private void processNode(Node node, ExecutorService executor) {
        executor.submit(() -> {
            if (processedNodes.add(node.id)) {
                // Execute the node
                node.execute();

                // Signal that this node is complete
                completionLatch.countDown();

                // Process all child nodes that are ready (all their parents have been processed)
                for (Node child : node.children) {
                    // Decrease the unprocessed parent count for this child
                    int remainingParents = decrementAndGetUnprocessedParents(child);

                    // If all parents have been processed, process this child
                    if (remainingParents == 0) {
                        processNode(child, executor);
                    }
                }
            }
        });
    }

    private synchronized int decrementAndGetUnprocessedParents(Node node) {
        return --node.unprocessedParents;
    }
}