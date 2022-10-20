package Job.Scheduler;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Future;

public class Scheduler implements IScheduler{
	
	private final DelayQueue<DelayTask> queue;
	
	public Scheduler() {
        this.queue = new DelayQueue<>();
        this.startExecute();
    }

	@Override
	public Future<Long> schedule(Runnable task, long delayTime) {
		DelayTask newTask = new DelayTask(task,delayTime);
		this.queue.offer(newTask);
		return newTask;
	}

	@Override
	public void startExecute() {
		Runnable executor = ()->{
			while(true) {
				try {
					DelayTask task = this.queue.take();
					task.run();
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
		};
		
		new Thread(executor,"Executor").start();
		
	}

}
