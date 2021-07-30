package com.calculusmaster.pokecord.util.helpers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.util.Mongo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.slf4j.LoggerFactory;

public class LoggerHelper
{
    public static void time(Class<?> clazz, String command, long timeI, long timeF)
    {
        info(clazz, "Time Info: " + command + " - " + (timeF - timeI) + " ms!");
    }

    public static void init(String name, Runnable init, boolean time)
    {
        info(Pokecord.class, "Starting " + name + " Init!");
        long i = System.currentTimeMillis();

        init.run();

        long f = System.currentTimeMillis();
        info(Pokecord.class, "Completed " + name + " Init!" + (time ? " Time: " + (f - i) + " ms!" : ""));
    }

    public static void init(String name, Runnable init)
    {
        init(name, init, false);
    }

    public static <C, T extends Throwable> void reportError(Class<C> clazz, String error, T exception)
    {
        Document crashReport = new Document()
                .append("error", error)
                .append("source", clazz.getName())
                .append("stack", ExceptionUtils.getStackTrace(exception));

        Mongo.CrashData.insertOne(crashReport);

        error(clazz, error);

        exception.printStackTrace();
    }

    //Core
    public static void info(Class<?> clazz, String msg)
    {
        LoggerFactory.getLogger(clazz).info(msg);
    }

    public static void info(Class<?> clazz, String msg, boolean requireInit)
    {
        if(requireInit && Pokecord.INIT_COMPLETE) info(clazz, msg);
    }

    public static void warn(Class<?> clazz, String msg)
    {
        LoggerFactory.getLogger(clazz).warn(msg);
    }

    public static void error(Class<?> clazz, String msg)
    {
        LoggerFactory.getLogger(clazz).error(msg);
    }

    //Misc
    public static void disableMongoLoggers()
    {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.connection").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.management").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.cluster").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.protocol.insert").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.protocol.query").setLevel(Level.OFF);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver.protocol.update").setLevel(Level.OFF);
    }
}
