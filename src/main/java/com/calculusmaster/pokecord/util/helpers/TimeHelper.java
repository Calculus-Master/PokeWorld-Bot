package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.commands.economy.CommandShop;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    //TODO: Actually implement this for shops
    public static void start()
    {
        ThreadPoolHandler.TIME.scheduleAtFixedRate(TimeHelper::updateTimes, 5, 15 * 60, TimeUnit.SECONDS);
    }

    private static void updateTimes()
    {
        SerializedTime now = SerializedTime.now();

        LoggerHelper.info(TimeHelper.class, "Time Updater Running! Now: " + now);

        for(TimeTask t : TimeTask.values())
        {
            SerializedTime interval = SerializedTime.interval(t.target);
            SerializedTime cache = SerializedTime.cache(t.target);

            if(now.toSeconds() - cache.toSeconds() >= interval.toSeconds())
            {
                LoggerHelper.info(TimeHelper.class, "Time Updater Task Running - " + t.target + " - Difference: " + (now.toSeconds() - cache.toSeconds()) + ", Minimum: " + interval.toSeconds());

                t.task.run();

                cache.update(now);
            }
        }
    }

    private enum TimeTask
    {
        SHOPS("shops", CommandShop::updateShops);

        private final String target;
        private final Runnable task;
        TimeTask(String target, Runnable task)
        {
            this.target = target;
            this.task = task;
        }
    }

    private static class SerializedTime
    {
        private final String target;
        private final String type;

        private final int days;
        private final int hours;
        private final int minutes;
        private final int seconds;

        SerializedTime(String target, String type, int days, int hours, int minutes, int seconds)
        {
            this.target = target;
            this.type = type;

            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        SerializedTime(String target, String type, PackagedTime time)
        {
            this(target, type, time.days(), time.hours(), time.minutes(), time.seconds());
        }

        public void update(SerializedTime updatedTime)
        {
            List<Bson> updates = List.of(
                    Updates.set("days", updatedTime.days),
                    Updates.set("hours", updatedTime.hours),
                    Updates.set("minutes", updatedTime.minutes),
                    Updates.set("seconds", updatedTime.seconds)
            );

            Mongo.TimeData.updateOne(query(this.target, this.type), updates);
        }

        public int toSeconds()
        {
            return (((this.days * 24 + this.hours) * 60) + this.minutes) * 60 + this.seconds;
        }

        static SerializedTime interval(String target)
        {
            Document interval = Mongo.TimeData.find(query(target, "interval")).first();

            if(interval == null) throw new NullPointerException("Time Data is null! Target: " + target + ", Type: Interval");

            return new SerializedTime(target, "interval", new PackagedTime(interval));
        }

        static SerializedTime cache(String target)
        {
            Document cache = Mongo.TimeData.find(query(target, "cache")).first();

            if(cache == null) throw new NullPointerException("Time Data is null! Target: " + target + ", Type: Cache");

            return new SerializedTime(target, "cache", new PackagedTime(cache));
        }

        static SerializedTime now()
        {
            LocalDateTime now = LocalDateTime.now();
            return new SerializedTime("now", "now", now.getDayOfYear(), now.getHour(), now.getMinute(), now.getSecond());
        }

        static Bson query(String target, String type)
        {
            return Filters.and(
                    Filters.eq("target", target),
                    Filters.eq("type", type)
            );
        }

        @Override
        public String toString()
        {
            return "SerializedTime{target=" + this.target + ", type=" + this.type + ", time=%s:%s:%s:%s}".formatted(this.days, this.hours, this.minutes, this.seconds);
        }
    }

    private record PackagedTime(int days, int minutes, int hours, int seconds)
    {
        PackagedTime(Document d)
        {
            this(d.getInteger("days"), d.getInteger("hours"), d.getInteger("minutes"), d.getInteger("seconds"));
        }
    }
}
