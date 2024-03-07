package org.sel.client.json;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LoggerThread extends Thread {
    private final BlockingQueue<String> itemsToLog = new ArrayBlockingQueue<String>(100);
    private static final String SHUTDOWN_REQ = "SHUTDOWN";
    private volatile boolean shuttingDown, loggerTerminated;

    public void run() {
        try {
            String item;
            while (!(item = itemsToLog.take()).equals(SHUTDOWN_REQ)) {
                System.out.println(item);
            }
        } catch (InterruptedException ignored) {
        } finally {
            loggerTerminated = true;
        }
    }

    public void log(String str) {
        if (shuttingDown || loggerTerminated) return;
        try {
            itemsToLog.put(str);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Unexpected interruption");
        }
    }

    public void shutDown() throws InterruptedException {
        shuttingDown = true;
        itemsToLog.put(SHUTDOWN_REQ);
    }

    private static final LoggerThread instance = new LoggerThread();

    public static LoggerThread getLogger() {
        return instance;
    }

    private LoggerThread() {
        start();
    }
}
