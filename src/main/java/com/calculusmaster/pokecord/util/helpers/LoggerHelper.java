package com.calculusmaster.pokecord.util.helpers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.calculusmaster.pokecord.Pokecord;
import org.slf4j.LoggerFactory;

public class LoggerHelper
{
    public static void time(Class<?> clazz, String command, long timeI, long timeF)
    {
        info(clazz, "Time Info: " + command + " - " + (timeF - timeI) + " ms!");
    }

    public static void init(String name, Runnable init)
    {
        info(Pokecord.class, "Starting " + name + " Init!");
        init.run();
    }

    //Core
    public static void info(Class<?> clazz, String msg)
    {
        LoggerFactory.getLogger(clazz).info(msg);
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
