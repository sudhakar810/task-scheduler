package Job.Scheduler;

import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * A {@link FutureTask} that becomes eligible to run after a fixed delay.
 * Uses {@link System#nanoTime()} so scheduling is unaffected by wall-clock changes.
 */
public class DelayTask extends FutureTask<Void> implements Delayed {

	private final long triggerTimeNanos;

	public DelayTask(Runnable runnable, long delay, TimeUnit unit) {
		super(runnable, null);
		this.triggerTimeNanos = System.nanoTime() + unit.toNanos(delay);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(triggerTimeNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@Override
	public int compareTo(Delayed other) {
		if (other == this) {
			return 0;
		}
		if (other instanceof DelayTask) {
			// Subtract rather than compare directly to stay correct if nanoTime overflows.
			long diff = triggerTimeNanos - ((DelayTask) other).triggerTimeNanos;
			return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
		}
		return Long.compare(getDelay(TimeUnit.NANOSECONDS), other.getDelay(TimeUnit.NANOSECONDS));
	}
}
