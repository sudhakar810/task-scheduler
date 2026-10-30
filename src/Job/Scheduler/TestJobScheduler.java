package Job.Scheduler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Simple demo/smoke test: run with {@code java Job.Scheduler.TestJobScheduler}. */
public class TestJobScheduler {

	public static void main(String[] args) throws Exception {
		List<String> order = new CopyOnWriteArrayList<>();
		long start = System.nanoTime();

		try (Scheduler scheduler = new Scheduler()) {
			// Scheduled out of order on purpose: the queue must run them by delay.
			Future<Void> task2 = scheduler.schedule(() -> log(order, "Task2", start), 1000); // after 1 second
			Future<Void> task1 = scheduler.schedule(() -> log(order, "Task1", start), 500);  // after 0.5 seconds
			Future<Void> never = scheduler.schedule(() -> log(order, "Never", start), 10, TimeUnit.SECONDS);

			task1.get();
			task2.get();
			scheduler.shutdown();

			check(order.equals(List.of("Task1", "Task2")), "tasks ran in delay order: " + order);
			check(never.isCancelled(), "pending task cancelled on shutdown");
			check(elapsedMillis(start) >= 1000, "Task2 did not run early");
		}
		System.out.println("All checks passed");
	}

	private static void log(List<String> order, String name, long start) {
		order.add(name);
		System.out.println(name + " running after " + elapsedMillis(start) + " ms");
	}

	private static long elapsedMillis(long start) {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError("FAILED: " + message);
		}
		System.out.println("OK: " + message);
	}
}
