package com.calculusmaster.pokecord.util.helpers;

import java.util.concurrent.*;

public class ThreadPoolHandler
{
    public static int THREAD_POOL_TYPE;

    public static final ExecutorService ACHIEVEMENT = ThreadPoolHandler.customThreadPool();
    public static final ExecutorService LISTENER_EVENT = ThreadPoolHandler.customThreadPool();
    public static final ExecutorService BOUNTY = ThreadPoolHandler.customThreadPool();

    private static ExecutorService customThreadPool()
    {
        return switch(THREAD_POOL_TYPE) {
            case 0 -> Executors.newCachedThreadPool();
            case 1 -> new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            case 2 -> Executors.newFixedThreadPool(4);
            default -> Executors.newFixedThreadPool(THREAD_POOL_TYPE * 2);
        };
    }
}
