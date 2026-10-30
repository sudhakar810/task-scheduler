package Job.Scheduler;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface IScheduler extends AutoCloseable {

	/** Schedules a task to run once after the given delay. */
	Future<Void> schedule(Runnable task, long delay, TimeUnit unit);

	/** Schedules a task to run once after the given delay in milliseconds. */
	default Future<Void> schedule(Runnable task, long delayMillis) {
		return schedule(task, delayMillis, TimeUnit.MILLISECONDS);
	}

	/** Starts the executor thread. Calling this more than once has no effect. */
	void startExecute();

	/** Stops the executor thread and cancels any tasks that have not run yet. */
	void shutdown();

	@Override
	default void close() {
		shutdown();
	}
}
