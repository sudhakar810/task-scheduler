package Job.Scheduler;

import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Single-threaded delayed-task scheduler backed by a {@link DelayQueue}.
 * Tasks run sequentially on one worker thread, so a long-running task delays the ones after it.
 */
public final class Scheduler implements IScheduler {

	private final DelayQueue<DelayTask> queue = new DelayQueue<>();
	private final AtomicBoolean started = new AtomicBoolean(false);
	private volatile boolean shutdown = false;
	private volatile Thread worker;

	public Scheduler() {
		startExecute();
	}

	@Override
	public Future<Void> schedule(Runnable task, long delay, TimeUnit unit) {
		Objects.requireNonNull(task, "task");
		Objects.requireNonNull(unit, "unit");
		if (delay < 0) {
			throw new IllegalArgumentException("delay must be >= 0: " + delay);
		}
		if (shutdown) {
			throw new RejectedExecutionException("Scheduler has been shut down");
		}
		DelayTask newTask = new DelayTask(task, delay, unit);
		queue.offer(newTask);
		return newTask;
	}

	@Override
	public void startExecute() {
		if (!started.compareAndSet(false, true)) {
			return;
		}
		Thread thread = new Thread(this::runLoop, "Scheduler-Executor");
		thread.setDaemon(true);
		worker = thread;
		thread.start();
	}

	private void runLoop() {
		while (!shutdown) {
			try {
				// FutureTask.run() captures any exception thrown by the task,
				// so a failing task cannot kill the worker thread.
				queue.take().run();
			} catch (InterruptedException ex) {
				// Interrupted by shutdown(); the loop condition decides whether to exit.
			}
		}
	}

	@Override
	public void shutdown() {
		shutdown = true;
		Thread thread = worker;
		if (thread != null) {
			thread.interrupt();
		}
		for (DelayTask pending : queue) {
			pending.cancel(false);
		}
		queue.clear();
	}
}
