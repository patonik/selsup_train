package org.sel.client.json;


import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {
    @Override
    public void publish(LogRecord logRecord) {
        System.out.println(logRecord);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
