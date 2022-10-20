package Job.Scheduler;

import java.util.concurrent.Future;

public interface IScheduler {
	
	public Future<Long> schedule(Runnable task, long delayTime);
	
	public void startExecute();
}
