package ua.mycompany.multithreading;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by margarita on 04.09.15.
 */
public class ThreadPool {

    private Queue<Runnable> tasks;
    private boolean started = false;


    private Queue<Executor> executors;

    public ThreadPool(int n) {
        executors = new LinkedList<>();
        tasks = new LinkedList<>();
        for (int i = 0; i < n; ++i) {
            executors.add(new Executor());
        }
    }

    public void startPool() {
        executors.stream().forEach(Thread::start);
    }

    public Runnable getNextTask() {
        Runnable r;
        synchronized(tasks) {
            while(tasks.size() <= 0) {
                try {
                    tasks.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            r = tasks.poll();
        }
        return r;
    }


    public void submit(Runnable c) {
        if (!started) {
            startPool();
            started = true;
        }
        synchronized(tasks) {
            tasks.add(c);
            tasks.notifyAll();
        }
    }

    private class Executor extends Thread {

        @Override
        public void run() {
            while (!isInterrupted()) {
                Runnable r = getNextTask();
                r.run();
            }
        }

    }


    public static void main(String... args) {

        ThreadPool pool = new ThreadPool(5);

        for (int j = 0; j < 10; ++j) {

            int k = j;
            pool.submit(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 10; ++i) {
                        final int ii = i;
                        System.out.println("Task #" + k + ii);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
//        pool.join();
    }

//    private void join() {
//        for (Thread thread: executors) {
//            thread.interrupt();
//        }
//    }

}
