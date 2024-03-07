package org.sel.client.json;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UEHLogger implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, throwable.getMessage());
    }
}
