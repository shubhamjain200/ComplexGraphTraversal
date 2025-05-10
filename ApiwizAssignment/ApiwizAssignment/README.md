Workflow Executor – README
Overview
This Java program simulates the execution of a directed acyclic graph (DAG)-based workflow, where each node represents a task and edges represent dependencies. A node can only execute after all of its parent (dependency) nodes have completed execution. The execution is parallelized wherever possible to optimize performance using a thread pool.

Approach
Input Reading:

First, the number of nodes (vertices) is read.

Each node is read in the format id:name, and stored in a Map<String, Node>.

Then, edges are read in the format sourceId:destinationId, and parent-child relationships are established accordingly.

Workflow Execution:

Execution starts from the root node (assumed to be the node with ID "1").

Each node is executed in its own thread if all its parent nodes have been processed.

Execution order respects dependency constraints.

Concurrency Management:

Uses ExecutorService with a cached thread pool for concurrent execution.

A CountDownLatch ensures the main thread waits for the completion of all nodes.

Thread safety is handled via:

A synchronizedSet (processedNodes) to prevent duplicate processing.

A synchronized method to decrement and retrieve the count of unprocessed parents.

Tools / Libraries Used
Java Standard Libraries:

java.util.*: For collections and input reading.

java.util.concurrent.*: For multithreading, synchronization, and concurrency management.

Concurrency Utilities:

ExecutorService: For parallel execution.

CountDownLatch: To wait until all tasks complete.

Concurrent Collections: Used for thread-safe node processing.

Assumptions
The graph is a Directed Acyclic Graph (DAG).

There is exactly one root node, identified by ID "1".

Input is well-formed and valid (no malformed lines or invalid IDs).

Each node’s execute() method is thread-safe and self-contained.

Nodes have unique IDs.

Design Decisions
Decoupling Execution Logic:

Node execution is encapsulated within the Node class for simplicity and future extensibility.

Concurrency Handling:

Thread safety is ensured via synchronization where necessary without blocking overall execution.

A thread pool allows the system to scale based on available resources.

Dynamic Execution:

Child nodes are only submitted for execution once all their parent nodes are completed.

Maintains a counter (unprocessedParents) for this purpose.

Efficient Termination Handling:

CountDownLatch is used to ensure graceful shutdown only after all nodes are processed.

TEST CASES SUMMARY:

Test CasePurposeEmpty InputNo nodes or edgesSelf-loopCycle within single nodeMissing Parent NodeInvalid edge with undefined parentDuplicate Node IDsTests overwriting behaviorLarge WorkflowPerformance & concurrency stress
