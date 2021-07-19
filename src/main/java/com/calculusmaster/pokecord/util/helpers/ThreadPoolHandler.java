package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.Pokecord;

import java.util.concurrent.*;

public class ThreadPoolHandler
{
    public static int THREAD_POOL_TYPE;

    public static ScheduledExecutorService SPAWN;

    public static final ExecutorService ACHIEVEMENT = ThreadPoolHandler.customThreadPool();
    public static final ExecutorService LISTENER_EVENT = ThreadPoolHandler.customThreadPool();
    public static final ExecutorService BOUNTY = ThreadPoolHandler.customThreadPool();
    public static final ExecutorService CATCH = ThreadPoolHandler.customThreadPool();
    public static final ExecutorService LISTENER_COMMAND = Executors.newFixedThreadPool(6);

    private static ExecutorService customThreadPool()
    {
        return switch(THREAD_POOL_TYPE) {
            case 0 -> Executors.newCachedThreadPool();
            case 1 -> new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            case 2 -> Executors.newFixedThreadPool(4);
            default -> Executors.newFixedThreadPool(THREAD_POOL_TYPE * 2);
        };
    }

    public static void createSpawnThreadPool()
    {
        SPAWN = Executors.newScheduledThreadPool(Pokecord.BOT_JDA.getGuilds().size());
    }
}
