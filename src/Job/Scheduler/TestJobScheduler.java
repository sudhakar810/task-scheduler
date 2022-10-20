package Job.Scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestJobScheduler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Runnable task1 = ()->{
			System.out.println("Task1 running");
		};
		
		Runnable task2 = ()->{
			System.out.println("Task2 running");
		};
		
		Scheduler sh = new Scheduler();
		sh.schedule(task1, 2000); // run after 5 second;
		sh.schedule(task2, 5000); // run after 5 second;

	}

}
