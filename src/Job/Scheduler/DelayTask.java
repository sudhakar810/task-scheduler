package Job.Scheduler;

import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DelayTask extends FutureTask<Long> implements Delayed {
	
	private final long startTime;
	private final Runnable task;

	public DelayTask(Runnable runnable, long delayTime) {
		super(runnable, null);
		this.task = runnable;
		this.startTime = System.currentTimeMillis() + delayTime;
		
	}
	
	@Override
	public long getDelay(TimeUnit unit) {
		long diff = this.startTime -System.currentTimeMillis();
		return unit.convert(diff,TimeUnit.MICROSECONDS);
	}

	@Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MICROSECONDS), ((DelayTask) o).getDelay(TimeUnit.MICROSECONDS));
    }

	

}
