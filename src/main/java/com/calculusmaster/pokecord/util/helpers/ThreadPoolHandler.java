package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.Pokeworld;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolHandler
{
    public static int THREAD_POOL_TYPE;
    private static final List<ExecutorService> THREAD_POOLS = new ArrayList<>();

    public static ScheduledExecutorService TIME;

    public static ScheduledExecutorService SPAWN;
    public static ScheduledExecutorService LOCATION;
    public static ScheduledExecutorService RAID;

    public static ExecutorService ACHIEVEMENT;
    public static ExecutorService LISTENER_EVENT;
    public static ExecutorService BOUNTY;
    public static ExecutorService CATCH;
    public static ExecutorService LISTENER_COMMAND;

    public static void init()
    {
        ACHIEVEMENT = createThreadPool();
        LISTENER_EVENT = createThreadPool();
        BOUNTY = createThreadPool();
        CATCH = createThreadPool();
        LISTENER_COMMAND = createThreadPool(6);

        TIME = (ScheduledExecutorService)createThreadPool(ThreadPoolType.CUSTOM_TIME_HELPER);

        SPAWN = (ScheduledExecutorService)createThreadPool(ThreadPoolType.GUILDS_SCHEDULED);
        LOCATION = (ScheduledExecutorService)createThreadPool(ThreadPoolType.GUILDS_SCHEDULED);
        RAID = (ScheduledExecutorService)createThreadPool(ThreadPoolType.GUILDS_SCHEDULED);
    }

    private static ExecutorService createThreadPool(ThreadPoolType type)
    {
        ExecutorService es = switch(type) {
            case CONFIG -> createThreadPool(ThreadPoolType.values()[THREAD_POOL_TYPE]);
            case DEFAULT_CACHED -> Executors.newCachedThreadPool();
            case CUSTOM_CACHED -> new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            case DEFAULT_FIXED -> Executors.newFixedThreadPool(4);
            case DEFAULT_SCHEDULED -> Executors.newScheduledThreadPool(4);
            case GUILDS_SCHEDULED -> Executors.newScheduledThreadPool(Pokeworld.BOT_JDA.getGuilds().size());
            case CUSTOM_TIME_HELPER -> Executors.newScheduledThreadPool(1);
        };

        THREAD_POOLS.add(es);
        return es;
    }

    private static ExecutorService createThreadPool()
    {
        return createThreadPool(ThreadPoolType.DEFAULT_CACHED);
    }

    private static ExecutorService createThreadPool(int size)
    {
        ExecutorService es =  Executors.newFixedThreadPool(size);

        THREAD_POOLS.add(es);
        return es;
    }

    public static void close() throws InterruptedException
    {
        for(ExecutorService es : THREAD_POOLS)
        {
            es.shutdownNow();
            es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        }

        THREAD_POOLS.clear();
    }

    private enum ThreadPoolType
    {
        CONFIG,
        DEFAULT_CACHED,
        CUSTOM_CACHED,
        DEFAULT_FIXED,
        DEFAULT_SCHEDULED,
        GUILDS_SCHEDULED,
        CUSTOM_TIME_HELPER;
    }
}
