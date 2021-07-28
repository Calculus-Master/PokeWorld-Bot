package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.duel.players.Trainer;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    public static void start()
    {
        ThreadPoolHandler.TIME.scheduleAtFixedRate(TimeHelper::updateTimes, 5, 15 * 60, TimeUnit.SECONDS);
    }

    private static void updateTimes()
    {
        final SerializedTime now = SerializedTime.now();
        final SerializedTime cached = SerializedTime.cached();

        LoggerHelper.info(TimeHelper.class, "Time Updater Running! Now: " + now + ", Cached: " + cached);

        Mongo.TimeData.find().forEach(d -> {
            SerializedTime interval = new SerializedTime(d);

            if(interval.target.equals("cache")) return;

            if(now.toSeconds() - cached.toSeconds() >= interval.toSeconds()) TimeTask.cast(interval.target).task.run();
        });

        SerializedTime.updateCached(now);
    }

    private enum TimeTask
    {
        DAILY_TRAINERS(Trainer::createDailyTrainers);

        private Runnable task;
        TimeTask(Runnable task)
        {
            this.task = task;
        }

        static TimeTask cast(String s)
        {
            for(TimeTask task : values()) if(task.toString().equalsIgnoreCase(s)) return task;
            return null;
        }
    }

    private static class SerializedTime
    {
        private final String target;
        private final int days;
        private final int hours;
        private final int minutes;
        private final int seconds;

        SerializedTime(String target, int days, int hours, int minutes, int seconds)
        {
            this.target = target;

            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        SerializedTime(Document data)
        {
            this(data.getString("target"), data.getInteger("days"), data.getInteger("hours"), data.getInteger("minutes"), data.getInteger("seconds"));
        }

        public static SerializedTime now()
        {
            LocalDateTime current = LocalDateTime.now();
            return new SerializedTime("now", current.getDayOfYear(), current.getHour(), current.getMinute(), current.getSecond());
        }

        public static SerializedTime cached()
        {
            return new SerializedTime(Objects.requireNonNull(Mongo.TimeData.find(Filters.eq("target", "cache")).first()));
        }

        public static void updateCached(SerializedTime updated)
        {
            List<Bson> updates = Arrays.asList(
                    Updates.set("days", updated.days),
                    Updates.set("hours", updated.hours),
                    Updates.set("minutes", updated.minutes),
                    Updates.set("seconds", updated.seconds));

            Mongo.TimeData.updateOne(Filters.eq("target", "cache"), updates);
        }

        public int toSeconds()
        {
            return this.days * 24 * 60 * 60 + this.hours * 60 * 60 + this.minutes * 60 + this.seconds;
        }

        public Document serialize()
        {
            return new Document()
                    .append("target", this.target)
                    .append("days", this.days)
                    .append("hours", this.hours)
                    .append("minutes", this.minutes)
                    .append("seconds", this.seconds);
        }

        @Override
        public String toString()
        {
            return "SerializedTime{" +
                    "target='" + target + '\'' +
                    ", days=" + days +
                    ", hours=" + hours +
                    ", minutes=" + minutes +
                    ", seconds=" + seconds +
                    '}';
        }
    }
}
