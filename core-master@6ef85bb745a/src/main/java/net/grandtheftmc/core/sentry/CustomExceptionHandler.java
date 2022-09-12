package net.grandtheftmc.core.sentry;

import io.sentry.Sentry;
import io.sentry.event.EventBuilder;
import net.grandtheftmc.core.Core;

import java.io.*;

/**
 * Created by Luke Bingham on 31/08/2017.
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

//    private Thread.UncaughtExceptionHandler defaultUEH;

    public CustomExceptionHandler() {
//        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t.getName() + " threw an exception: " + e.getLocalizedMessage());
        Core.getInstance().getSentryClient().sendEvent(new EventBuilder());

//        Sentry.capture(e.getMessage());
//        defaultUEH.uncaughtException(t, e);
    }
}
