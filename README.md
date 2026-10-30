# task-scheduler

A minimal in-process job scheduler in Java, built on `java.util.concurrent.DelayQueue`
and `FutureTask`.

## How it works

- `Scheduler.schedule(task, delay, unit)` wraps the `Runnable` in a `DelayTask`
  (a `FutureTask` that implements `Delayed`) and puts it on a `DelayQueue`.
- A single daemon worker thread calls `queue.take()`, which blocks until the task with the
  earliest trigger time is due, then runs it.
- The returned `Future<Void>` can be used to wait for completion (`get()`) or cancel the task.
- `shutdown()` (or `close()`, since `Scheduler` is `AutoCloseable`) stops the worker and
  cancels tasks that have not run yet.

Tasks run sequentially on one thread, so a long-running task delays the tasks after it.

## Usage

```java
try (Scheduler scheduler = new Scheduler()) {
    Future<Void> f = scheduler.schedule(() -> System.out.println("hello"), 2, TimeUnit.SECONDS);
    f.get(); // wait for it to run
}
```

## Build and run the demo

Requires JDK 8+.

```sh
javac -d out src/Job/Scheduler/*.java
java -cp out Job.Scheduler.TestJobScheduler
```
